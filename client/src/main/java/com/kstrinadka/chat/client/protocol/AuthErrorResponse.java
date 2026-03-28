package com.kstrinadka.chat.client.protocol;

import java.util.Objects;

public record AuthErrorResponse(
        ProtocolMessageType type,
        String requestId,
        String errorCode,
        String message
) implements ServerResponse {

    public AuthErrorResponse {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(requestId, "requestId must not be null");
        Objects.requireNonNull(errorCode, "errorCode must not be null");
        Objects.requireNonNull(message, "message must not be null");
    }
}
