package com.kstrinadka.chat.server.transport;

import java.net.SocketAddress;

public final class DefaultConnectionContextFactory implements ConnectionContextFactory {

    @Override
    public ConnectionContext create(
            ClientConnection connection,
            OutboundChannel outboundChannel,
            SocketAddress remoteAddress
    ) {
        return new ConnectionContext(connection, outboundChannel, remoteAddress);
    }
}
