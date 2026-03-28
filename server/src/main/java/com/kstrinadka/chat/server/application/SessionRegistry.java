package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.domain.ClientSession;
import com.kstrinadka.chat.server.transport.ConnectionContext;

import java.util.Collection;
import java.util.Optional;

public interface SessionRegistry {

    boolean register(String username, ConnectionContext connectionContext, ClientSession session);

    Optional<ConnectionContext> findConnectionByUsername(String username);

    Optional<ClientSession> findSessionByUsername(String username);

    void unregister(String username);

    boolean isOnline(String username);

    Collection<ClientSession> getAllSessions();
}
