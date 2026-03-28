package com.kstrinadka.chat.server.domain;

import java.time.Instant;
import java.util.Objects;

public final class ClientSession {

    private final SessionId sessionId;
    private final String username;
    private final Instant authenticatedAt;
    private volatile boolean active = true;

    public ClientSession(SessionId sessionId, String username, Instant authenticatedAt) {
        this.sessionId = Objects.requireNonNull(sessionId);
        this.username = Objects.requireNonNull(username);
        this.authenticatedAt = Objects.requireNonNull(authenticatedAt);
    }

    public SessionId sessionId() {
        return sessionId;
    }

    public String username() {
        return username;
    }

    public Instant authenticatedAt() {
        return authenticatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void markClosed() {
        active = false;
    }
}
