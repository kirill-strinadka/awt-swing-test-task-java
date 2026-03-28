package com.kstrinadka.chat.server.protocol;

import java.time.Instant;

public record IncomingMessageResponse(
        String type,
        String requestId,
        String messageId,
        String from,
        String text,
        Instant createdAt,
        String clientMsgId
) implements ServerResponse {
}
