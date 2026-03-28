package com.kstrinadka.chat.client.protocol;

import java.util.Objects;

public record AuthOkResponse(
        ProtocolMessageType type,
        String requestId,
        String username
) implements ServerResponse {

    public AuthOkResponse {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(requestId, "requestId must not be null");
        Objects.requireNonNull(username, "username must not be null");
    }
}
