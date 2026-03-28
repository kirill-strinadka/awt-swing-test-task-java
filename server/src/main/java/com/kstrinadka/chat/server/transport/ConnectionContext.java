package com.kstrinadka.chat.server.transport;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class ConnectionContext {

    private final String connectionId;
    private final AtomicReference<ConnectionState> connectionState;
    private final OutboundChannel outboundChannel;
    private final AtomicReference<String> authenticatedUsername;
    private final ClientConnection connection;
    private final SocketAddress remoteAddress;

    public ConnectionContext(
            ClientConnection connection,
            OutboundChannel outboundChannel,
            SocketAddress remoteAddress
    ) {
        this.connectionId = UUID.randomUUID().toString();
        this.connectionState = new AtomicReference<>(ConnectionState.CONNECTED);
        this.outboundChannel = Objects.requireNonNull(outboundChannel);
        this.authenticatedUsername = new AtomicReference<>(null);
        this.connection = Objects.requireNonNull(connection);
        this.remoteAddress = remoteAddress;
    }

    public String connectionId() {
        return connectionId;
    }

    public ConnectionState connectionState() {
        return connectionState.get();
    }

    public OutboundChannel outboundChannel() {
        return outboundChannel;
    }

    public Optional<String> authenticatedUsername() {
        return Optional.ofNullable(authenticatedUsername.get());
    }

    public boolean isAuthenticated() {
        return authenticatedUsername.get() != null
                && connectionState.get() == ConnectionState.AUTHENTICATED;
    }

    public void markAuthenticated(String username) {
        Objects.requireNonNull(username);
        authenticatedUsername.set(username);
        connectionState.set(ConnectionState.AUTHENTICATED);
    }

    public void markClosed() {
        connectionState.set(ConnectionState.CLOSED);
    }

    public ClientConnection connection() {
        return connection;
    }

    public SocketAddress remoteAddress() {
        return remoteAddress;
    }
}
