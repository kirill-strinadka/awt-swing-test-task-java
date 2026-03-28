package com.kstrinadka.chat.server.protocol;

public record ErrorResponse(
        String type,
        String requestId,
        String errorCode,
        String message
) implements ServerResponse {
}
