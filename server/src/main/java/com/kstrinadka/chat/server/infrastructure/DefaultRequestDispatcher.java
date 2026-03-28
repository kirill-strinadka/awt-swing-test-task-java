package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.AuthUseCase;
import com.kstrinadka.chat.server.application.RequestDispatcher;
import com.kstrinadka.chat.server.application.SendMessageUseCase;
import com.kstrinadka.chat.server.protocol.AuthRequest;
import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.SendMessageRequest;
import com.kstrinadka.chat.server.protocol.ServerResponse;
import com.kstrinadka.chat.server.transport.ConnectionContext;

public final class DefaultRequestDispatcher implements RequestDispatcher {

    private final AuthUseCase authUseCase;
    private final SendMessageUseCase sendMessageUseCase;

    public DefaultRequestDispatcher(AuthUseCase authUseCase, SendMessageUseCase sendMessageUseCase) {
        this.authUseCase = authUseCase;
        this.sendMessageUseCase = sendMessageUseCase;
    }

    @Override
    public ServerResponse dispatch(ConnectionContext context, ClientRequest request) {
        return switch (request) {
            case AuthRequest authRequest -> authUseCase.handle(context, authRequest);
            case SendMessageRequest sendMessageRequest -> sendMessageUseCase.handle(context, sendMessageRequest);
        };
    }
}
