package com.kstrinadka.chat.server.protocol;

public sealed interface ClientRequest permits AuthRequest, SendMessageRequest, PingRequest {

    String type();

    String requestId();
}
