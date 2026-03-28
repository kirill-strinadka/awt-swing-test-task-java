package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.ProtocolException;
import com.kstrinadka.chat.server.protocol.ProtocolValidator;

public final class DefaultProtocolValidator implements ProtocolValidator {

    private final int maxMessageLength;

    public DefaultProtocolValidator(int maxMessageLength) {
        this.maxMessageLength = maxMessageLength;
    }

    @Override
    public void validate(ClientRequest request) throws ProtocolException {
        throw new UnsupportedOperationException("not implemented");
    }
}
