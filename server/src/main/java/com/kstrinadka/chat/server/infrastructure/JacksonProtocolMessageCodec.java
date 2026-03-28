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
        try {
            JsonNode root = objectMapper.readTree(rawMessage);
            JsonNode typeNode = root.get("type");
            if (typeNode == null || typeNode.asText().isBlank()) {
                throw new ProtocolException("Missing request type");
            }

            String type = typeNode.asText();
            if (ProtocolTypes.REQUEST_AUTH.equals(type)) {
                return objectMapper.treeToValue(root, AuthRequest.class);
            }
            if (ProtocolTypes.REQUEST_SEND.equals(type)) {
                return objectMapper.treeToValue(root, SendMessageRequest.class);
            }
            throw new ProtocolException("Unsupported request type: " + type);
        } catch (JsonProcessingException e) {
            throw new ProtocolException("Failed to decode client request", e);
        }
    }

    @Override
    public String encodeResponse(ServerResponse response) throws ProtocolException {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new ProtocolException("Failed to encode server response", e);
        }
    }
}
