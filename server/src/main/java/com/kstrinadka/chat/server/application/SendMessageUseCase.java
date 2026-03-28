package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.domain.ChatMessage;
import com.kstrinadka.chat.server.domain.DeliveryResult;
import com.kstrinadka.chat.server.protocol.AckResponse;
import com.kstrinadka.chat.server.protocol.ErrorResponse;
import com.kstrinadka.chat.server.protocol.IncomingMessageResponse;
import com.kstrinadka.chat.server.protocol.ProtocolTypes;
import com.kstrinadka.chat.server.protocol.SendMessageRequest;
import com.kstrinadka.chat.server.protocol.ServerResponse;
import com.kstrinadka.chat.server.transport.ConnectionContext;

import java.util.Optional;

public final class SendMessageUseCase {

    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;
    private final MessageService messageService;
    private final MessageDeliveryService messageDeliveryService;

    public SendMessageUseCase(
            UserRepository userRepository,
            SessionRegistry sessionRegistry,
            MessageService messageService,
            MessageDeliveryService messageDeliveryService
    ) {
        this.userRepository = userRepository;
        this.sessionRegistry = sessionRegistry;
        this.messageService = messageService;
        this.messageDeliveryService = messageDeliveryService;
    }

    /**
     * @return response for the sender's connection ({@code ACK} or {@code ERROR}). Recipient {@code INCOMING} is sent via {@link MessageDeliveryService}.
     */
    public ServerResponse handle(ConnectionContext senderContext, SendMessageRequest request) {
        if (!senderContext.isAuthenticated()) {
            return new ErrorResponse(
                    ProtocolTypes.ERROR,
                    request.requestId(),
                    "SENDER_NOT_AUTHENTICATED",
                    "You must authenticate before sending messages"
            );
        }

        Optional<String> senderOpt = senderContext.authenticatedUsername();
        if (senderOpt.isEmpty()) {
            return new ErrorResponse(
                    ProtocolTypes.ERROR,
                    request.requestId(),
                    "SENDER_NOT_AUTHENTICATED",
                    "You must authenticate before sending messages"
            );
        }

        String sender = senderOpt.get();
        String recipient = request.to() == null ? "" : request.to().strip();

        if (!userRepository.existsByUsername(recipient)) {
            return new ErrorResponse(
                    ProtocolTypes.ERROR,
                    request.requestId(),
                    "RECIPIENT_NOT_FOUND",
                    "Recipient does not exist"
            );
        }

        Optional<ConnectionContext> recipientContextOpt = sessionRegistry.findConnectionByUsername(recipient);
        if (recipientContextOpt.isEmpty()) {
            return new ErrorResponse(
                    ProtocolTypes.ERROR,
                    request.requestId(),
                    "RECIPIENT_OFFLINE",
                    "Recipient is offline"
            );
        }

        DeliveryResult prepared = messageService.prepareMessage(sender, recipient, request.text(), request.clientMsgId());
        if (prepared instanceof DeliveryResult.Failure failure) {
            return new ErrorResponse(
                    ProtocolTypes.ERROR,
                    request.requestId(),
                    failure.errorCode().name(),
                    failure.message()
            );
        }

        ChatMessage message = ((DeliveryResult.Success) prepared).message();

        IncomingMessageResponse incoming = new IncomingMessageResponse(
                ProtocolTypes.INCOMING,
                null,
                message.messageId(),
                message.from(),
                message.text(),
                message.createdAt(),
                message.clientMessageId()
        );

        boolean delivered = messageDeliveryService.deliver(recipientContextOpt.get(), incoming);
        if (!delivered) {
            return new ErrorResponse(
                    ProtocolTypes.ERROR,
                    request.requestId(),
                    "DELIVERY_FAILED",
                    "Failed to deliver message to recipient"
            );
        }

        return new AckResponse(
                ProtocolTypes.ACK,
                request.requestId(),
                message.messageId(),
                request.clientMsgId(),
                message.createdAt(),
                "DELIVERED"
        );
    }
}
