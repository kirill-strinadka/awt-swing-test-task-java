package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.SessionRegistry;
import com.kstrinadka.chat.server.domain.ClientSession;
import com.kstrinadka.chat.server.transport.ConnectionContext;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemorySessionRegistry implements SessionRegistry {

    private record SessionEntry(ConnectionContext connectionContext, ClientSession clientSession) {
    }

    private final Map<String, SessionEntry> sessionsByUsername = new ConcurrentHashMap<>();

    @Override
    public boolean register(String username, ConnectionContext connectionContext, ClientSession session) {
        if (username == null || username.isBlank() || connectionContext == null || session == null) {
            return false;
        }
        return sessionsByUsername.putIfAbsent(username, new SessionEntry(connectionContext, session)) == null;
    }

    @Override
    public Optional<ConnectionContext> findConnectionByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        SessionEntry entry = sessionsByUsername.get(username);
        return entry == null ? Optional.empty() : Optional.of(entry.connectionContext());
    }

    @Override
    public Optional<ClientSession> findSessionByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        SessionEntry entry = sessionsByUsername.get(username);
        return entry == null ? Optional.empty() : Optional.of(entry.clientSession());
    }

    @Override
    public void unregister(String username) {
        if (username != null && !username.isBlank()) {
            sessionsByUsername.remove(username);
        }
    }

    @Override
    public boolean isOnline(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return sessionsByUsername.containsKey(username);
    }

    @Override
    public Collection<ClientSession> getAllSessions() {
        return sessionsByUsername.values().stream()
                .map(SessionEntry::clientSession)
                .toList();
    }
}
