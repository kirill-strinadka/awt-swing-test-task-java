package com.kstrinadka.chat.server.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kstrinadka.chat.server.protocol.AuthRequest;
import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.ProtocolException;
import com.kstrinadka.chat.server.protocol.ProtocolMessageCodec;
import com.kstrinadka.chat.server.protocol.ProtocolTypes;
import com.kstrinadka.chat.server.protocol.SendMessageRequest;
import com.kstrinadka.chat.server.protocol.ServerResponse;

import java.util.Objects;

public final class JacksonProtocolMessageCodec implements ProtocolMessageCodec {

    private final ObjectMapper objectMapper;

    public JacksonProtocolMessageCodec(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public ClientRequest decodeRequest(String rawMessage) throws ProtocolException {
        if (rawMessage == null || rawMessage.isBlank()) {
            throw new ProtocolException("Raw request must not be blank");
        }

        try {
            JsonNode root = objectMapper.readTree(rawMessage);
            JsonNode typeNode = root.get("type");
            if (typeNode == null || typeNode.asText().isBlank()) {
                throw new ProtocolException("Missing request field: type");
            }

            String type = typeNode.asText();
            return switch (type) {
                case ProtocolTypes.REQUEST_AUTH -> objectMapper.treeToValue(root, AuthRequest.class);
                case ProtocolTypes.REQUEST_SEND -> objectMapper.treeToValue(root, SendMessageRequest.class);
                default -> throw new ProtocolException("Unsupported request type: " + type);
            };
        } catch (JsonProcessingException e) {
            throw new ProtocolException("Malformed JSON request", e);
        }
    }

    @Override
    public String encodeResponse(ServerResponse response) throws ProtocolException {
        if (response == null) {
            throw new ProtocolException("Response must not be null");
        }

        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new ProtocolException("Failed to encode response", e);
        }
    }
}
