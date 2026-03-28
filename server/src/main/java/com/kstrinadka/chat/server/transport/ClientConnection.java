package com.kstrinadka.chat.server.transport;

import com.kstrinadka.chat.server.protocol.ServerResponse;

public interface ClientConnection extends AutoCloseable {

    void run();

    void send(ServerResponse response);

    boolean isOpen();

    @Override
    void close();
}
