package com.kstrinadka.chat.server.protocol;

public sealed interface ClientRequest permits AuthRequest, SendMessageRequest {

    String type();

    String requestId();
}
