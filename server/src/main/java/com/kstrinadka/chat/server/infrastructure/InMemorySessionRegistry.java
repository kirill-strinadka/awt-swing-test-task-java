package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.SessionRegistry;
import com.kstrinadka.chat.server.domain.ClientSession;
import com.kstrinadka.chat.server.transport.ConnectionContext;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public final class InMemorySessionRegistry implements SessionRegistry {

    private record Entry(ConnectionContext connectionContext, ClientSession session) {
    }

    private final ConcurrentMap<String, Entry> sessionsByUsername = new ConcurrentHashMap<>();

    @Override
    public boolean register(String username, ConnectionContext connectionContext, ClientSession session) {
        if (username == null || username.isBlank() || connectionContext == null || session == null) {
            return false;
        }
        return sessionsByUsername.putIfAbsent(username, new Entry(connectionContext, session)) == null;
    }

    @Override
    public Optional<ConnectionContext> findConnectionByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(sessionsByUsername.get(username)).map(Entry::connectionContext);
    }

    @Override
    public Optional<ClientSession> findSessionByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(sessionsByUsername.get(username)).map(Entry::session);
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
        return sessionsByUsername.values().stream().map(Entry::session).collect(Collectors.toUnmodifiableList());
    }
}
