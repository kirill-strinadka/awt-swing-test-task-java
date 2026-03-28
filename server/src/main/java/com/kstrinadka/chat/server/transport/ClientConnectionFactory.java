package com.kstrinadka.chat.server.transport;

import java.net.Socket;

public interface ClientConnectionFactory {

    ClientConnection create(Socket socket);
}
