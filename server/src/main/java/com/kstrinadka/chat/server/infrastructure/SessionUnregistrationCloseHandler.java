package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.SessionRegistry;
import com.kstrinadka.chat.server.transport.ConnectionCloseHandler;
import com.kstrinadka.chat.server.transport.ConnectionContext;

public final class SessionUnregistrationCloseHandler implements ConnectionCloseHandler {

    private final SessionRegistry sessionRegistry;

    public SessionUnregistrationCloseHandler(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void onConnectionClosed(ConnectionContext context) {
        if (context == null) {
            return;
        }
        context.authenticatedUsername().ifPresent(sessionRegistry::unregister);
    }
}
