package com.kstrinadka.chat.server.transport;

import com.kstrinadka.chat.server.config.ServerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ChatServer {

    private static final Logger log = LoggerFactory.getLogger(ChatServer.class);

    private final ServerConfig config;
    private final ClientConnectionFactory clientConnectionFactory;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private volatile ServerSocket serverSocket;
    private volatile ExecutorService connectionExecutor;

    public ChatServer(ServerConfig config, ClientConnectionFactory clientConnectionFactory) {
        this.config = Objects.requireNonNull(config);
        this.clientConnectionFactory = Objects.requireNonNull(clientConnectionFactory);
    }

    /**
     * Blocks accepting connections until {@link #stop()} closes the server socket or an I/O error occurs.
     */
    public void start() throws IOException {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        ServerSocket ss = new ServerSocket();
        try {
            InetAddress address = InetAddress.getByName(config.host());
            ss.bind(new InetSocketAddress(address, config.port()));
            this.serverSocket = ss;
        } catch (IOException e) {
            running.set(false);
            try {
                ss.close();
            } catch (IOException ignored) {
            }
            this.serverSocket = null;
            throw e;
        }

        this.connectionExecutor = Executors.newVirtualThreadPerTaskExecutor();

        try {
            while (running.get()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    if (!running.get()) {
                        break;
                    }
                    throw e;
                }
                socket.setSoTimeout(config.socketReadTimeoutMillis());
                ClientConnection connection = clientConnectionFactory.create(socket);
                connectionExecutor.submit(connection::run);
            }
        } catch (IOException e) {
            if (running.get()) {
                log.warn("Accept loop failed", e);
                throw e;
            }
        } finally {
            stop();
        }
    }

    public void stop() {
        running.set(false);

        ServerSocket ss = serverSocket;
        if (ss != null && !ss.isClosed()) {
            try {
                ss.close();
            } catch (IOException e) {
                log.debug("Error closing server socket", e);
            }
        }

        ExecutorService ex = connectionExecutor;
        if (ex != null) {
            ex.shutdownNow();
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
