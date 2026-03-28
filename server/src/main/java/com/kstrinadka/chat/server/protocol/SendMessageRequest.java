package com.kstrinadka.chat.server.protocol;

public record SendMessageRequest(
        String type,
        String requestId,
        String to,
        String text,
        String clientMsgId
) implements ClientRequest {
}
