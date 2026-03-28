package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.AuthUseCase;
import com.kstrinadka.chat.server.application.DispatchResult;
import com.kstrinadka.chat.server.application.RequestDispatcher;
import com.kstrinadka.chat.server.application.SendMessageUseCase;
import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.transport.ConnectionContext;

public final class DefaultRequestDispatcher implements RequestDispatcher {

    private final AuthUseCase authUseCase;
    private final SendMessageUseCase sendMessageUseCase;

    public DefaultRequestDispatcher(AuthUseCase authUseCase, SendMessageUseCase sendMessageUseCase) {
        this.authUseCase = authUseCase;
        this.sendMessageUseCase = sendMessageUseCase;
    }

    @Override
    public DispatchResult dispatch(ConnectionContext context, ClientRequest request) {
        throw new UnsupportedOperationException("not implemented");
    }
}
