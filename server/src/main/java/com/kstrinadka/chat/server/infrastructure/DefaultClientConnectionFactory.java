package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.RequestDispatcher;
import com.kstrinadka.chat.server.protocol.ProtocolMessageCodec;
import com.kstrinadka.chat.server.protocol.ProtocolValidator;
import com.kstrinadka.chat.server.transport.ClientConnection;
import com.kstrinadka.chat.server.transport.ClientConnectionFactory;
import com.kstrinadka.chat.server.transport.ConnectionCloseHandler;
import com.kstrinadka.chat.server.transport.ConnectionContextFactory;
import com.kstrinadka.chat.server.transport.SocketClientConnection;

import java.net.Socket;
import java.util.Objects;

public final class DefaultClientConnectionFactory implements ClientConnectionFactory {

    private final ProtocolMessageCodec codec;
    private final ProtocolValidator validator;
    private final RequestDispatcher dispatcher;
    private final ConnectionContextFactory contextFactory;
    private final ConnectionCloseHandler closeHandler;
    private final int maxRawMessageLength;

    public DefaultClientConnectionFactory(
            ProtocolMessageCodec codec,
            ProtocolValidator validator,
            RequestDispatcher dispatcher,
            ConnectionContextFactory contextFactory,
            ConnectionCloseHandler closeHandler,
            int maxRawMessageLength
    ) {
        this.codec = Objects.requireNonNull(codec);
        this.validator = Objects.requireNonNull(validator);
        this.dispatcher = Objects.requireNonNull(dispatcher);
        this.contextFactory = Objects.requireNonNull(contextFactory);
        this.closeHandler = Objects.requireNonNull(closeHandler);
        if (maxRawMessageLength <= 0) {
            throw new IllegalArgumentException("maxRawMessageLength must be positive");
        }
        this.maxRawMessageLength = maxRawMessageLength;
    }

    @Override
    public ClientConnection create(Socket socket) {
        return new SocketClientConnection(
                socket,
                codec,
                validator,
                dispatcher,
                contextFactory,
                closeHandler,
                maxRawMessageLength
        );
    }
}
