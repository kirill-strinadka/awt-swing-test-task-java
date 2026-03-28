package com.kstrinadka.chat.server.transport;

import java.net.Socket;

public interface ConnectionContextFactory {

    ConnectionContext create(Socket socket);
}
