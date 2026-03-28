package com.kstrinadka.chat.server.protocol;

public record AuthErrorResponse(
        String type,
        String requestId,
        String errorCode,
        String message
) implements ServerResponse {
}
