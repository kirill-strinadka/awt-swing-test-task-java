package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.MessageIdGenerator;
import com.kstrinadka.chat.server.application.MessageService;
import com.kstrinadka.chat.server.domain.ChatMessage;
import com.kstrinadka.chat.server.domain.DeliveryErrorCode;
import com.kstrinadka.chat.server.domain.DeliveryResult;

import java.time.Clock;
import java.util.Objects;

public final class DefaultMessageService implements MessageService {

    private final MessageIdGenerator messageIdGenerator;
    private final Clock clock;
    private final int maxMessageLength;

    public DefaultMessageService(MessageIdGenerator messageIdGenerator, Clock clock, int maxMessageLength) {
        this.messageIdGenerator = Objects.requireNonNull(messageIdGenerator);
        this.clock = Objects.requireNonNull(clock);
        this.maxMessageLength = maxMessageLength;
    }

    @Override
    public DeliveryResult prepareMessage(String from, String to, String text, String clientMessageId) {
        if (from == null || from.isBlank()) {
            return new DeliveryResult.Failure(DeliveryErrorCode.INVALID_REQUEST, "Sender is required");
        }
        if (to == null || to.isBlank()) {
            return new DeliveryResult.Failure(DeliveryErrorCode.RECIPIENT_NOT_FOUND, "Recipient is required");
        }
        if (text == null || text.isBlank()) {
            return new DeliveryResult.Failure(DeliveryErrorCode.INVALID_TEXT, "Message text is required");
        }
        if (clientMessageId == null || clientMessageId.isBlank()) {
            return new DeliveryResult.Failure(DeliveryErrorCode.INVALID_REQUEST, "clientMsgId is required");
        }
        if (text.length() > maxMessageLength) {
            return new DeliveryResult.Failure(
                    DeliveryErrorCode.INVALID_TEXT,
                    "Message exceeds max length of " + maxMessageLength
            );
        }

        ChatMessage message = new ChatMessage(
                messageIdGenerator.nextId(),
                from.strip(),
                to.strip(),
                text,
                clock.instant(),
                clientMessageId
        );
        return new DeliveryResult.Success(message);
    }
}
