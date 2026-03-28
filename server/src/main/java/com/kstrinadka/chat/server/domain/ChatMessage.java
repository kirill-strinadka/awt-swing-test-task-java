package com.kstrinadka.chat.server.domain;

import java.time.Instant;

public record ChatMessage(
        String messageId,
        String from,
        String to,
        String text,
        Instant createdAt,
        String clientMessageId
) {
}
