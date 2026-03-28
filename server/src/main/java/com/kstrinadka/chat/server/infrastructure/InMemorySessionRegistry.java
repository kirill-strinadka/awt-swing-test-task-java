package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.SessionRegistry;
import com.kstrinadka.chat.server.domain.ClientSession;
import com.kstrinadka.chat.server.transport.ConnectionContext;

import java.util.Collection;
import java.util.Optional;

public final class InMemorySessionRegistry implements SessionRegistry {

    public InMemorySessionRegistry() {
    }

    @Override
    public boolean register(String username, ConnectionContext connectionContext, ClientSession session) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Optional<ConnectionContext> findConnectionByUsername(String username) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Optional<ClientSession> findSessionByUsername(String username) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void unregister(String username) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isOnline(String username) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Collection<ClientSession> getAllSessions() {
        throw new UnsupportedOperationException("not implemented");
    }
}
