package com.kstrinadka.chat.server.protocol;

public record AuthRequest(
        String type,
        String requestId,
        String username,
        String password
) implements ClientRequest {
}
