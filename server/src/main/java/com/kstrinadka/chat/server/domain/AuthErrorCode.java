package com.kstrinadka.chat.server.domain;

public enum AuthErrorCode {
    INVALID_CREDENTIALS,
    USER_ALREADY_LOGGED_IN,
    INVALID_REQUEST,
    /** Repeated AUTH on a connection that is already authenticated. */
    ALREADY_AUTHENTICATED
}
