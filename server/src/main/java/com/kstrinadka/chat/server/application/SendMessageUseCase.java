package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.protocol.SendMessageRequest;
import com.kstrinadka.chat.server.transport.ConnectionContext;

public final class SendMessageUseCase {

    private final MessageService messageService;
    private final SessionRegistry sessionRegistry;

    public SendMessageUseCase(MessageService messageService, SessionRegistry sessionRegistry) {
        this.messageService = messageService;
        this.sessionRegistry = sessionRegistry;
    }

    public SendDispatchResult handle(ConnectionContext senderContext, SendMessageRequest request) {
        throw new UnsupportedOperationException("not implemented");
    }
}
