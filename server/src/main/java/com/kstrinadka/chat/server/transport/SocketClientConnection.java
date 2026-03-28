package com.kstrinadka.chat.server.transport;

import com.kstrinadka.chat.server.application.RequestDispatcher;
import com.kstrinadka.chat.server.protocol.ProtocolMessageCodec;
import com.kstrinadka.chat.server.protocol.ProtocolValidator;
import com.kstrinadka.chat.server.protocol.ServerResponse;

import java.net.Socket;

public final class SocketClientConnection implements ClientConnection {

    private final Socket socket;
    private final ProtocolMessageCodec codec;
    private final ProtocolValidator validator;
    private final RequestDispatcher dispatcher;
    private final ConnectionContextFactory contextFactory;

    public SocketClientConnection(
            Socket socket,
            ProtocolMessageCodec codec,
            ProtocolValidator validator,
            RequestDispatcher dispatcher,
            ConnectionContextFactory contextFactory
    ) {
        this.socket = socket;
        this.codec = codec;
        this.validator = validator;
        this.dispatcher = dispatcher;
        this.contextFactory = contextFactory;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void send(ServerResponse response) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("not implemented");
    }
}
