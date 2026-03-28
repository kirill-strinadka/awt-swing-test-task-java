package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.protocol.AuthRequest;
import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.ProtocolException;
import com.kstrinadka.chat.server.protocol.ProtocolValidator;
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

        throw new ProtocolException("Unsupported request type: " + request.getClass().getSimpleName());
    }

    private void validateAuth(AuthRequest request) throws ProtocolException {
        if (isBlank(request.type())) {
            throw new ProtocolException("AUTH.type must not be blank");
        }
        if (isBlank(request.requestId())) {
            throw new ProtocolException("AUTH.requestId must not be blank");
        }
        if (isBlank(request.username())) {
            throw new ProtocolException("AUTH.username must not be blank");
        }
        if (isBlank(request.password())) {
            throw new ProtocolException("AUTH.password must not be blank");
        }
    }

    private void validateSend(SendMessageRequest request) throws ProtocolException {
        if (isBlank(request.type())) {
            throw new ProtocolException("SEND.type must not be blank");
        }
        if (isBlank(request.requestId())) {
            throw new ProtocolException("SEND.requestId must not be blank");
        }
        if (isBlank(request.clientMsgId())) {
            throw new ProtocolException("SEND.clientMsgId must not be blank");
        }
        if (isBlank(request.to())) {
            throw new ProtocolException("SEND.to must not be blank");
        }
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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
