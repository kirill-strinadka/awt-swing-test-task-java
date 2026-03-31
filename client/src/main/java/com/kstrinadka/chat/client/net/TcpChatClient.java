package com.kstrinadka.chat.client.net;

import com.kstrinadka.chat.client.protocol.ClientRequest;
import com.kstrinadka.chat.client.protocol.IncomingResponse;
import com.kstrinadka.chat.client.protocol.JacksonProtocolCodec;
import com.kstrinadka.chat.client.protocol.ProtocolCodec;
import com.kstrinadka.chat.client.protocol.PingRequest;
import com.kstrinadka.chat.client.protocol.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

public class TcpChatClient implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(TcpChatClient.class);

    private final ProtocolCodec protocolCodec;
    private volatile TcpChatClientListener listener;
    private final ExecutorService readerExecutor;
    private final ScheduledExecutorService heartbeatExecutor;
    private volatile ScheduledFuture<?> heartbeatTask;
    private final ConcurrentHashMap<String, CompletableFuture<ServerResponse>> pendingRequests =
            new ConcurrentHashMap<>();

    private final Object lifecycleLock = new Object();
    private final Object writeLock = new Object();
    private volatile ConnectionToServer connection;
    private volatile boolean connected;

    public TcpChatClient(TcpChatClientListener listener) {
        this(new JacksonProtocolCodec(), listener);
    }

    public TcpChatClient(ProtocolCodec protocolCodec, TcpChatClientListener listener) {
        this.protocolCodec = Objects.requireNonNull(protocolCodec, "protocolCodec must not be null");
        this.listener = Objects.requireNonNull(listener, "listener must not be null");
        this.readerExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "tcp-chat-client-reader");
            thread.setDaemon(true);
            return thread;
        });
        this.heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "tcp-chat-client-heartbeat");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void setListener(TcpChatClientListener listener) {
        this.listener = Objects.requireNonNull(listener, "listener must not be null");
    }

    public void connect(String host, int port) {
        Objects.requireNonNull(host, "host must not be null");

        synchronized (lifecycleLock) {
            if (connected) {
                throw new IllegalStateException("Client is already connected");
            }

            try {
                log.info("Connecting to {}:{}", host, port);

                this.connection = ConnectionToServer.open(host, port);
                this.connected = true;

                startReaderLoop();
                startHeartbeat();

                log.info("Connected to {}:{}", host, port);
            } catch (IOException ex) {
                closeConnectionQuietly();
                throw new IllegalStateException("Failed to connect to server", ex);
            }
        }
    }

    @Override
    public void close() {
        disconnect();
    }

    public void disconnect() {
        synchronized (lifecycleLock) {
            if (!connected && connection == null) {
                return;
            }

            log.info("Disconnecting TCP client");
            connected = false;
            stopHeartbeat();
            closeConnectionQuietly();

            failAllPendingRequests(new IllegalStateException("Disconnected"));
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendRequest(ClientRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        String json = protocolCodec.encode(request);

        synchronized (writeLock) {

            if (!connected) {
                throw new IllegalStateException("Client is not connected");
            }
            ConnectionToServer currentConnection = this.connection;
            if (currentConnection == null) {
                throw new IllegalStateException("Connection is not initialized");
            }

            try {
                currentConnection.writeLine(json);
                log.debug("Request sent: {}", json);
            } catch (IOException ex) {
                handleReaderLoopFailure(ex);
                throw new IllegalStateException("Failed to send request", ex);
            }
        }
    }

    public CompletableFuture<ServerResponse> sendRequestAwaitResponse(ClientRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        String requestId = request.requestId();
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("requestId must not be blank");
        }

        CompletableFuture<ServerResponse> responseFuture = new CompletableFuture<>();
        CompletableFuture<ServerResponse> previous = pendingRequests.putIfAbsent(requestId, responseFuture);

        if (previous != null) {
            throw new IllegalStateException("Request with same requestId is already pending: " + requestId);
        }

        try {
            sendRequest(request);
            return responseFuture;
        } catch (RuntimeException ex) {
            pendingRequests.remove(requestId);
            responseFuture.completeExceptionally(ex);
            throw ex;
        }
    }

    private void startReaderLoop() {
        readerExecutor.submit(() -> {
            try {
                runReaderLoop();
            } catch (Throwable ex) {
                handleReaderLoopFailure(ex);
            }
        });
    }

    private void runReaderLoop() throws IOException {
        while (connected) {
            ConnectionToServer currentConnection = this.connection;
            if (currentConnection == null) {
                return;
            }
            String line = currentConnection.readLine();
            if (line == null) {
                throw new IOException("Server closed the connection");
            }

            handleIncomingLine(line);
        }
    }

    private void handleIncomingLine(String line) {
        log.debug("Response line received: {}", line);

        ServerResponse response;
        try {
            response = protocolCodec.decode(line);
        } catch (RuntimeException ex) {
            TcpChatClientListener currentListener = this.listener;
            currentListener.onProtocolError(line, ex);
            return;
        }

        dispatch(response);
    }

    private void dispatch(ServerResponse response) {
        String requestId = response.requestId();

        // server response
        if (requestId != null && !requestId.isBlank()) {
            CompletableFuture<ServerResponse> future = pendingRequests.remove(requestId);
            if (future != null) {
                future.complete(response);
                return;
            }

            log.warn("Received response for unknown requestId: {}", requestId);
            return;
        }


        // incoming message
        if (response instanceof IncomingResponse incomingResponse) {
            listener.onIncoming(incomingResponse);
            return;
        }

        log.warn("Received server response without requestId and without INCOMING type: {}", response.type());
    }

    private void handleReaderLoopFailure(Throwable cause) {
        synchronized (lifecycleLock) {
            boolean wasConnected = connected;
            connected = false;
            stopHeartbeat();
            closeConnectionQuietly();
            failAllPendingRequests(cause);

            if (wasConnected) {
                log.warn("TCP client disconnected due to failure", cause);
                listener.onDisconnected(cause);
            }
        }
    }

    private void startHeartbeat() {
        stopHeartbeat();
        heartbeatTask = heartbeatExecutor.scheduleAtFixedRate(
                this::sendHeartbeatSafely,
                30_000L,
                30_000L,
                TimeUnit.MILLISECONDS
        );
    }

    private void stopHeartbeat() {
        ScheduledFuture<?> task = heartbeatTask;
        if (task != null) {
            task.cancel(true);
            heartbeatTask = null;
        }
    }

    private void sendHeartbeatSafely() {
        if (!connected) {
            return;
        }
        try {
            PingRequest ping = PingRequest.of(UUID.randomUUID().toString());
            sendRequest(ping);
        } catch (RuntimeException ex) {
            // Heartbeat failures should not crash the client; reader loop will handle disconnects.
        }
    }

    private void failAllPendingRequests(Throwable cause) {
        pendingRequests.forEach((requestId, future) -> future.completeExceptionally(cause));
        pendingRequests.clear();
    }

    private void closeConnectionQuietly() {
        ConnectionToServer currentConnection = this.connection;
        this.connection = null;

        if (currentConnection != null) {
            currentConnection.close();
        }
    }

}
