package com.kstrinadka.chat.client.protocol;

import java.util.Objects;

public record AuthRequest(
        ProtocolMessageType type,
        String requestId,
        String username,
        String password
) implements ClientRequest {

    public AuthRequest {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(requestId, "requestId must not be null");
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(password, "password must not be null");
    }

    public static AuthRequest of(String requestId, String username, String password) {
        return new AuthRequest(ProtocolMessageType.AUTH, requestId, username, password);
    }
}
