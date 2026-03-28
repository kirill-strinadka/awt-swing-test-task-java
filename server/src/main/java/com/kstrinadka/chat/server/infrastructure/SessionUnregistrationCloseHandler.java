package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.SessionRegistry;
import com.kstrinadka.chat.server.transport.ConnectionCloseHandler;
import com.kstrinadka.chat.server.transport.ConnectionContext;

import java.util.Objects;

/**
 * Removes authenticated users from {@link SessionRegistry} when the TCP session ends.
 * {@link com.kstrinadka.chat.server.transport.SocketClientConnection} also calls {@link ConnectionContext#markClosed()}
 * and {@code close()} in its {@code finally}; this handler is idempotent with that path.
 */
public final class SessionUnregistrationCloseHandler implements ConnectionCloseHandler {

    private final SessionRegistry sessionRegistry;

    public SessionUnregistrationCloseHandler(SessionRegistry sessionRegistry) {
        this.sessionRegistry = Objects.requireNonNull(sessionRegistry);
    }

    @Override
    public void onConnectionClosed(ConnectionContext context) {
        if (context == null) {
            return;
        }

        context.authenticatedUsername().ifPresent(sessionRegistry::unregister);
        context.markClosed();

        var connection = context.connection();
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }
}
