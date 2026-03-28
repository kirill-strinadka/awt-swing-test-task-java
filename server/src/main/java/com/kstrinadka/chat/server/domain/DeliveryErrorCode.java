package com.kstrinadka.chat.server.domain;

public enum DeliveryErrorCode {
    RECIPIENT_NOT_FOUND,
    RECIPIENT_OFFLINE,
    SENDER_NOT_AUTHENTICATED,
    INVALID_TEXT,
    INVALID_REQUEST
}
