package com.kstrinadka.chat.server.protocol;

import java.time.Instant;

public record AckResponse(
        String type,
        String requestId,
        String serverMsgId,
        String clientMsgId,
        Instant acceptedAt,
        String status
) implements ServerResponse {
}
