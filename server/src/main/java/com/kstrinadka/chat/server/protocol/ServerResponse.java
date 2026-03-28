package com.kstrinadka.chat.server.protocol;

public sealed interface ServerResponse permits AuthOkResponse, AuthErrorResponse, AckResponse,
        IncomingMessageResponse, ErrorResponse {

    String type();

    String requestId();
}
