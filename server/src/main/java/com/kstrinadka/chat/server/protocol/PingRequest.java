package com.kstrinadka.chat.server.protocol;

/**
 * Lightweight keep-alive request. Used by clients to prevent idle socket timeouts.
 */
public record PingRequest(
        String type,
        String requestId
) implements ClientRequest {
}

