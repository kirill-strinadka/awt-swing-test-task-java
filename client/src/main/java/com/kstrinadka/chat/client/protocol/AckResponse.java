package com.kstrinadka.chat.client.protocol;

import java.time.Instant;
import java.util.Objects;

public record AckResponse(
        ProtocolMessageType type,
        String requestId,
        String serverMsgId,
        String clientMsgId,
        Instant acceptedAt,
        String status
) implements ServerResponse {

    public AckResponse {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(requestId, "requestId must not be null");
        Objects.requireNonNull(serverMsgId, "serverMsgId must not be null");
        Objects.requireNonNull(clientMsgId, "clientMsgId must not be null");
        Objects.requireNonNull(acceptedAt, "acceptedAt must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }
}
