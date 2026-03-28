package com.kstrinadka.chat.server.protocol;

public record AuthOkResponse(
        String type,
        String requestId,
        String username
) implements ServerResponse {
}
