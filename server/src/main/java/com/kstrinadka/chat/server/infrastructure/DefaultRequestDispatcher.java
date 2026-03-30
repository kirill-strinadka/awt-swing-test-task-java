package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.AuthUseCase;
import com.kstrinadka.chat.server.application.RequestDispatcher;
import com.kstrinadka.chat.server.application.SendMessageUseCase;
import com.kstrinadka.chat.server.protocol.AuthRequest;
import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.ErrorResponse;
import com.kstrinadka.chat.server.protocol.ProtocolTypes;
import com.kstrinadka.chat.server.protocol.SendMessageRequest;
import com.kstrinadka.chat.server.protocol.PingRequest;
import com.kstrinadka.chat.server.protocol.ServerResponse;
import com.kstrinadka.chat.server.transport.ConnectionContext;

import java.util.Objects;

public final class DefaultRequestDispatcher implements RequestDispatcher {

    private final AuthUseCase authUseCase;
    private final SendMessageUseCase sendMessageUseCase;

    public DefaultRequestDispatcher(AuthUseCase authUseCase, SendMessageUseCase sendMessageUseCase) {
        this.authUseCase = Objects.requireNonNull(authUseCase);
        this.sendMessageUseCase = Objects.requireNonNull(sendMessageUseCase);
    }

    @Override
    public ServerResponse dispatch(ConnectionContext context, ClientRequest request) {
        if (request instanceof AuthRequest authRequest) {
            return authUseCase.handle(context, authRequest);
        }
        if (request instanceof SendMessageRequest sendMessageRequest) {
            return sendMessageUseCase.handle(context, sendMessageRequest);
        }

        if (request instanceof PingRequest) {
            // Keep-alive ping: no response needed for this connection
            return null;
        }

        return new ErrorResponse(
                ProtocolTypes.ERROR,
                request != null ? request.requestId() : null,
                "UNSUPPORTED_REQUEST",
                "Unsupported request type"
        );
    }
}
