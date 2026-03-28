package com.kstrinadka.chat.server.transport;

import com.kstrinadka.chat.server.protocol.ServerResponse;

/**
 * Thread-safe outbound path for a single TCP connection (responses and pushed events).
 */
public interface OutboundChannel {

    void send(ServerResponse response);
}
