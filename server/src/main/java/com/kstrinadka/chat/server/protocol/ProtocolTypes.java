package com.kstrinadka.chat.server.protocol;

/**
 * Wire {@code type} values for JSON line protocol.
 */
public final class ProtocolTypes {

    /** Client request: authenticate. */
    public static final String REQUEST_AUTH = "AUTH";
    /** Client request: send chat message. */
    public static final String REQUEST_SEND = "SEND";

    public static final String AUTH_OK = "AUTH_OK";
    public static final String AUTH_ERROR = "AUTH_ERROR";
    public static final String ACK = "ACK";
    public static final String INCOMING = "INCOMING";
    public static final String ERROR = "ERROR";

    private ProtocolTypes() {
    }
}
