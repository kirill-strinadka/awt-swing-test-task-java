package com.kstrinadka.chat.server.transport;

/**
 * Invoked once when a client connection finishes (normal EOF, error, or {@link ClientConnection#close()}).
 */
@FunctionalInterface
public interface ConnectionCloseHandler {

    void onConnectionClosed(ConnectionContext context);
}
