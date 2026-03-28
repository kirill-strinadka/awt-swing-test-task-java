package com.kstrinadka.chat.client.domain.chat;

import com.kstrinadka.chat.client.ui.chat.MessageDirection;
import com.kstrinadka.chat.client.ui.chat.MessageStatus;

import java.time.Instant;
import java.util.Objects;

public record Message(
        String text,
        MessageDirection direction,
        Instant createdAt,
        String clientMsgId,
        String serverMsgId,
        MessageStatus status,
        String sender,
        String recipient
) {

    public Message {
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(direction, "direction must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(sender, "sender must not be null");
        Objects.requireNonNull(recipient, "recipient must not be null");
    }

    public boolean isIncoming() {
        return direction == MessageDirection.INCOMING;
    }

    public boolean isOutgoing() {
        return direction == MessageDirection.OUTGOING;
    }

    public Message withStatus(MessageStatus newStatus) {
        return new Message(
                text,
                direction,
                createdAt,
                clientMsgId,
                serverMsgId,
                newStatus,
                sender,
                recipient
        );
    }

    public Message withServerMsgId(String newServerMsgId) {
        return new Message(
                text,
                direction,
                createdAt,
                clientMsgId,
                newServerMsgId,
                status,
                sender,
                recipient
        );
    }
}
