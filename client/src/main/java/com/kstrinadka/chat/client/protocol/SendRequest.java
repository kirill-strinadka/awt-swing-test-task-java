package com.kstrinadka.chat.client.protocol;

import java.util.Objects;

public record SendRequest(
        ProtocolMessageType type,
        String requestId,
        String to,
        String text,
        String clientMsgId
) implements ClientRequest {

    public SendRequest {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(requestId, "requestId must not be null");
        Objects.requireNonNull(to, "to must not be null");
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(clientMsgId, "clientMsgId must not be null");
    }

    public static SendRequest of(String requestId, String to, String text, String clientMsgId) {
        return new SendRequest(ProtocolMessageType.SEND, requestId, to, text, clientMsgId);
    }
}
