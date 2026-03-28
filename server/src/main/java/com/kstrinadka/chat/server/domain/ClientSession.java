package com.kstrinadka.chat.server.domain;

import java.time.Instant;

public final class ClientSession {

    public SessionId sessionId() {
        throw new UnsupportedOperationException("not implemented");
    }

    public String username() {
        throw new UnsupportedOperationException("not implemented");
    }

    public Instant authenticatedAt() {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean isActive() {
        throw new UnsupportedOperationException("not implemented");
    }

    public void markClosed() {
        throw new UnsupportedOperationException("not implemented");
    }
}
