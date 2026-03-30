package com.kstrinadka.chat.client.protocol;

import java.util.Objects;

public record PingRequest(
        ProtocolMessageType type,
        String requestId
) implements ClientRequest {

    public PingRequest {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(requestId, "requestId must not be null");
    }

    public static PingRequest of(String requestId) {
        return new PingRequest(ProtocolMessageType.PING, requestId);
    }
}

