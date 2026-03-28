package com.kstrinadka.chat.server.transport;

import com.kstrinadka.chat.server.config.ServerConfig;

import java.io.IOException;

public final class ChatServer {

    private final ServerConfig config;
    private final ClientConnectionFactory clientConnectionFactory;

    public ChatServer(ServerConfig config, ClientConnectionFactory clientConnectionFactory) {
        this.config = config;
        this.clientConnectionFactory = clientConnectionFactory;
    }

    public void start() throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    public void stop() throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean isRunning() {
        throw new UnsupportedOperationException("not implemented");
    }
}
