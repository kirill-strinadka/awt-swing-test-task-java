package com.kstrinadka.chat.client.protocol;

import java.time.Instant;
import java.util.Objects;

public record IncomingResponse(
        ProtocolMessageType type,
        String requestId,
        String messageId,
        String from,
        String text,
        Instant createdAt,
        String clientMsgId
) implements ServerResponse {

    public IncomingResponse {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(messageId, "messageId must not be null");
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(clientMsgId, "clientMsgId must not be null");
    }
}
