package com.kstrinadka.chat.server.transport;

import java.net.SocketAddress;

public interface ConnectionContextFactory {

    ConnectionContext create(
            ClientConnection connection,
            OutboundChannel outboundChannel,
            SocketAddress remoteAddress
    );
}
