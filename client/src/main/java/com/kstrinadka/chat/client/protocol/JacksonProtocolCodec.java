package com.kstrinadka.chat.client.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

public class JacksonProtocolCodec implements ProtocolCodec {

    private final ObjectMapper objectMapper;

    public JacksonProtocolCodec() {
        this(ProtocolObjectMapperFactory.create());
    }

    public JacksonProtocolCodec(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    @Override
    public String encode(ClientRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to encode client request", ex);
        }
    }

    @Override
    public ServerResponse decode(String line) {
        Objects.requireNonNull(line, "line must not be null");

        try {
            return objectMapper.readValue(line, ServerResponse.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to decode server response", ex);
        }
    }
}
