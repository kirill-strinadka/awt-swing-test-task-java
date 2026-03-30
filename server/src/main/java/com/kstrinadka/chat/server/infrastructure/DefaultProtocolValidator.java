package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.protocol.AuthRequest;
import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.ProtocolException;
import com.kstrinadka.chat.server.protocol.ProtocolTypes;
import com.kstrinadka.chat.server.protocol.ProtocolValidator;
import com.kstrinadka.chat.server.protocol.PingRequest;
import com.kstrinadka.chat.server.protocol.SendMessageRequest;

public final class DefaultProtocolValidator implements ProtocolValidator {

    private final int maxMessageLength;

    public DefaultProtocolValidator(int maxMessageLength) {
        if (maxMessageLength <= 0) {
            throw new IllegalArgumentException("maxMessageLength must be positive");
        }
        this.maxMessageLength = maxMessageLength;
    }

    @Override
    public void validate(ClientRequest request) throws ProtocolException {
        if (request == null) {
            throw new ProtocolException("Request must not be null");
        }

        if (request instanceof AuthRequest authRequest) {
            validateAuth(authRequest);
            return;
        }

        if (request instanceof SendMessageRequest sendMessageRequest) {
            validateSend(sendMessageRequest);
            return;
        }

        if (request instanceof PingRequest pingRequest) {
            validatePing(pingRequest);
            return;
        }

        throw new ProtocolException("Unsupported request class: " + request.getClass().getName());
    }

    private void validateAuth(AuthRequest request) throws ProtocolException {
        if (!ProtocolTypes.REQUEST_AUTH.equals(request.type())) {
            throw new ProtocolException("Invalid AUTH.type value: " + request.type());
        }
        requireNotBlank(request.requestId(), "AUTH.requestId");
        requireNotBlank(request.username(), "AUTH.username");
        requireNotBlank(request.password(), "AUTH.password");
    }

    private void validateSend(SendMessageRequest request) throws ProtocolException {
        if (!ProtocolTypes.REQUEST_SEND.equals(request.type())) {
            throw new ProtocolException("Invalid SEND.type value: " + request.type());
        }
        requireNotBlank(request.requestId(), "SEND.requestId");
        requireNotBlank(request.to(), "SEND.to");
        requireNotBlank(request.clientMsgId(), "SEND.clientMsgId");

        if (request.text() == null) {
            throw new ProtocolException("SEND.text must not be null");
        }
        if (request.text().isBlank()) {
            throw new ProtocolException("SEND.text must not be blank");
        }
        if (request.text().length() > maxMessageLength) {
            throw new ProtocolException("SEND.text exceeds maxMessageLength=" + maxMessageLength);
        }
    }

    private void validatePing(PingRequest request) throws ProtocolException {
        if (!ProtocolTypes.REQUEST_PING.equals(request.type())) {
            throw new ProtocolException("Invalid PING.type value: " + request.type());
        }
        requireNotBlank(request.requestId(), "PING.requestId");
    }

    private static void requireNotBlank(String value, String fieldName) throws ProtocolException {
        if (value == null || value.isBlank()) {
            throw new ProtocolException(fieldName + " must not be blank");
        }
    }
}
