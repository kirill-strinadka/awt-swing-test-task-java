package com.kstrinadka.chat.server.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kstrinadka.chat.server.protocol.AckResponse;
import com.kstrinadka.chat.server.protocol.AuthRequest;
import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.ProtocolException;
import com.kstrinadka.chat.server.protocol.ProtocolTypes;
import com.kstrinadka.chat.server.protocol.SendMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JacksonProtocolMessageCodecTest {

    private ObjectMapper objectMapper;
    private JacksonProtocolMessageCodec codec;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        codec = new JacksonProtocolMessageCodec(objectMapper);
    }

    @Test
    void decodeRequest_shouldDecodeAuthRequest() {
        String raw = """
                {"type":"AUTH","requestId":"req-1","username":"alice","password":"alicepwd"}
                """;

        ClientRequest request = codec.decodeRequest(raw);

        assertThat(request).isInstanceOf(AuthRequest.class);
        AuthRequest auth = (AuthRequest) request;
        assertThat(auth.type()).isEqualTo(ProtocolTypes.REQUEST_AUTH);
        assertThat(auth.requestId()).isEqualTo("req-1");
        assertThat(auth.username()).isEqualTo("alice");
        assertThat(auth.password()).isEqualTo("alicepwd");
    }

    @Test
    void decodeRequest_shouldDecodeSendMessageRequest() {
        String raw = """
                {"type":"SEND","requestId":"req-2","to":"bob","text":"hello","clientMsgId":"c-1"}
                """;

        ClientRequest request = codec.decodeRequest(raw);

        assertThat(request).isInstanceOf(SendMessageRequest.class);
        SendMessageRequest send = (SendMessageRequest) request;
        assertThat(send.type()).isEqualTo(ProtocolTypes.REQUEST_SEND);
        assertThat(send.requestId()).isEqualTo("req-2");
        assertThat(send.to()).isEqualTo("bob");
        assertThat(send.text()).isEqualTo("hello");
        assertThat(send.clientMsgId()).isEqualTo("c-1");
    }

    @Test
    void decodeRequest_shouldThrowOnUnsupportedType() {
        String raw = """
                {"type":"PING","requestId":"req-3"}
                """;

        assertThatThrownBy(() -> codec.decodeRequest(raw))
                .isInstanceOf(ProtocolException.class)
                .hasMessageContaining("Unsupported request type");
    }

    @Test
    void encodeResponse_shouldSerializeInstantAsIsoString() throws Exception {
        AckResponse response = new AckResponse(
                ProtocolTypes.ACK,
                "req-4",
                "server-msg-1",
                "client-msg-1",
                Instant.parse("2026-03-28T12:34:56Z"),
                "DELIVERED"
        );

        String json = codec.encodeResponse(response);
        JsonNode root = objectMapper.readTree(json);

        assertThat(root.get("type").asText()).isEqualTo("ACK");
        assertThat(root.get("requestId").asText()).isEqualTo("req-4");
        assertThat(root.get("serverMsgId").asText()).isEqualTo("server-msg-1");
        assertThat(root.get("clientMsgId").asText()).isEqualTo("client-msg-1");
        assertThat(root.get("acceptedAt").isTextual()).isTrue();
        assertThat(root.get("acceptedAt").asText()).isEqualTo("2026-03-28T12:34:56Z");
        assertThat(root.get("status").asText()).isEqualTo("DELIVERED");
    }
}
