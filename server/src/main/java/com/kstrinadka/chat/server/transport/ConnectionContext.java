package com.kstrinadka.chat.server.transport;

import java.net.SocketAddress;
import java.util.Optional;

public final class ConnectionContext {

    public String connectionId() {
        throw new UnsupportedOperationException("not implemented");
    }

    public ConnectionState connectionState() {
        throw new UnsupportedOperationException("not implemented");
    }

    public OutboundChannel outboundChannel() {
        throw new UnsupportedOperationException("not implemented");
    }

    public Optional<String> authenticatedUsername() {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean isAuthenticated() {
        throw new UnsupportedOperationException("not implemented");
    }

    public void markAuthenticated(String username) {
        throw new UnsupportedOperationException("not implemented");
    }

    public void markClosed() {
        throw new UnsupportedOperationException("not implemented");
    }

    public ClientConnection connection() {
        throw new UnsupportedOperationException("not implemented");
    }

    public SocketAddress remoteAddress() {
        throw new UnsupportedOperationException("not implemented");
    }
}
