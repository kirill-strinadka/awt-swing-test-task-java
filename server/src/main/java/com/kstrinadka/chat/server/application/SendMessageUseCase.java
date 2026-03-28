package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.protocol.SendMessageRequest;
import com.kstrinadka.chat.server.protocol.ServerResponse;
import com.kstrinadka.chat.server.transport.ConnectionContext;

public final class SendMessageUseCase {

    private final MessageService messageService;
    private final SessionRegistry sessionRegistry;
    private final MessageDeliveryService messageDeliveryService;

    public SendMessageUseCase(
            MessageService messageService,
            SessionRegistry sessionRegistry,
            MessageDeliveryService messageDeliveryService
    ) {
        this.messageService = messageService;
        this.sessionRegistry = sessionRegistry;
        this.messageDeliveryService = messageDeliveryService;
    }

    /**
     * @return response for the sender's connection ({@code ACK} or {@code ERROR}). Recipient {@code INCOMING} is sent via {@link MessageDeliveryService}.
     */
    public ServerResponse handle(ConnectionContext senderContext, SendMessageRequest request) {
        throw new UnsupportedOperationException("not implemented");
    }
}
