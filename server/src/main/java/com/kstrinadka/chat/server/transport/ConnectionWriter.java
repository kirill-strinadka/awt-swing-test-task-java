package com.kstrinadka.chat.server.transport;

import com.kstrinadka.chat.server.protocol.ServerResponse;

public interface ConnectionWriter {

    void write(ServerResponse response);
}
