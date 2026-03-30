package com.kstrinadka.chat.client.protocol;

public enum ProtocolMessageType {
    AUTH,
    SEND,
    PING,

    AUTH_OK,
    AUTH_ERROR,
    ACK,
    INCOMING,
    ERROR
}
