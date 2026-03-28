package com.kstrinadka.chat.server.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.ProtocolException;
import com.kstrinadka.chat.server.protocol.ProtocolMessageCodec;
import com.kstrinadka.chat.server.protocol.ServerResponse;

public final class JacksonProtocolMessageCodec implements ProtocolMessageCodec {

    private final ObjectMapper objectMapper;

    public JacksonProtocolMessageCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ClientRequest decodeRequest(String rawMessage) throws ProtocolException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String encodeResponse(ServerResponse response) throws ProtocolException {
        throw new UnsupportedOperationException("not implemented");
    }
}
