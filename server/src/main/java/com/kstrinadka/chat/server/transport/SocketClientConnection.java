package com.kstrinadka.chat.server.transport;

import com.kstrinadka.chat.server.application.RequestDispatcher;
import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.ErrorResponse;
import com.kstrinadka.chat.server.protocol.ProtocolException;
import com.kstrinadka.chat.server.protocol.ProtocolMessageCodec;
import com.kstrinadka.chat.server.protocol.ProtocolTypes;
import com.kstrinadka.chat.server.protocol.ProtocolValidator;
import com.kstrinadka.chat.server.protocol.ServerResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SocketClientConnection implements ClientConnection {

    private static final Logger log = LoggerFactory.getLogger(SocketClientConnection.class);

    private final Socket socket;
    private final ProtocolMessageCodec codec;
    private final ProtocolValidator validator;
    private final RequestDispatcher dispatcher;
    private final ConnectionContextFactory contextFactory;
    private final ConnectionCloseHandler closeHandler;
    private final int maxRawMessageLength;

    private final Object writeLock = new Object();
    private final AtomicBoolean open = new AtomicBoolean(true);

    private volatile BufferedReader reader;
    private volatile BufferedWriter writer;
    private volatile ConnectionContext context;

    public SocketClientConnection(
            Socket socket,
            ProtocolMessageCodec codec,
            ProtocolValidator validator,
            RequestDispatcher dispatcher,
            ConnectionContextFactory contextFactory,
            ConnectionCloseHandler closeHandler,
            int maxRawMessageLength
    ) {
        this.socket = Objects.requireNonNull(socket);
        this.codec = Objects.requireNonNull(codec);
        this.validator = Objects.requireNonNull(validator);
        this.dispatcher = Objects.requireNonNull(dispatcher);
        this.contextFactory = Objects.requireNonNull(contextFactory);
        this.closeHandler = Objects.requireNonNull(closeHandler);
        if (maxRawMessageLength <= 0) {
            throw new IllegalArgumentException("maxRawMessageLength must be positive");
        }
        this.maxRawMessageLength = maxRawMessageLength;
    }

    @Override
    public void run() {
        try {
            this.reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
            );
            this.writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
            );

            OutboundChannel outboundChannel = this::send;
            this.context = contextFactory.create(this, outboundChannel, socket.getRemoteSocketAddress());

            String rawLine;
            while (isOpen() && (rawLine = reader.readLine()) != null) {
                if (rawLine.isBlank()) {
                    sendProtocolError(null, "EMPTY_REQUEST", "Request line must not be blank");
                    continue;
                }

                if (rawLine.length() > maxRawMessageLength) {
                    sendProtocolError(null, "REQUEST_TOO_LARGE", "Raw request exceeds max allowed length");
                    continue;
                }

                try {
                    ClientRequest request = codec.decodeRequest(rawLine);
                    validator.validate(request);

                    ServerResponse response = dispatcher.dispatch(context, request);
                    if (response != null) {
                        send(response);
                    }
                } catch (ProtocolException e) {
                    sendProtocolError(null, "PROTOCOL_ERROR", e.getMessage());
                } catch (Exception e) {
                    log.warn("Unexpected error handling request", e);
                    sendProtocolError(null, "INTERNAL_ERROR", "Internal server error");
                }
            }
        } catch (IOException e) {
            log.debug("Connection I/O ended: {}", e.toString());
        } finally {
            if (context != null) {
                closeHandler.onConnectionClosed(context);
            }
            close();
        }
    }

    @Override
    public void send(ServerResponse response) {
        if (response == null || !isOpen()) {
            return;
        }

        try {
            String encoded = codec.encodeResponse(response);
            synchronized (writeLock) {
                if (!isOpen()) {
                    return;
                }
                BufferedWriter w = writer;
                if (w == null) {
                    return;
                }
                w.write(encoded);
                w.write('\n');
                w.flush();
            }
        } catch (Exception e) {
            log.debug("Failed to write response, closing connection", e);
            close();
        }
    }

    @Override
    public boolean isOpen() {
        return open.get() && !socket.isClosed();
    }

    @Override
    public void close() {
        if (!open.compareAndSet(true, false)) {
            return;
        }

        try {
            socket.close();
        } catch (IOException ignored) {
        }

        if (context != null) {
            context.markClosed();
        }
    }

    private void sendProtocolError(String requestId, String code, String message) {
        send(new ErrorResponse(ProtocolTypes.ERROR, requestId, code, message));
    }
}
