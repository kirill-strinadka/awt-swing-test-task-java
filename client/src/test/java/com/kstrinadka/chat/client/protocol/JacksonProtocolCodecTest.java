package com.kstrinadka.chat.client.protocol;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonProtocolCodecTest {

    private final ProtocolCodec codec = new JacksonProtocolCodec();

    @Test
    void shouldEncodeAuthRequest() {
        AuthRequest request = AuthRequest.of("r1", "alice", "alicepwd");

        String json = codec.encode(request);

        assertTrue(json.contains("\"type\":\"AUTH\""));
        assertTrue(json.contains("\"requestId\":\"r1\""));
        assertTrue(json.contains("\"username\":\"alice\""));
        assertTrue(json.contains("\"password\":\"alicepwd\""));
    }

    @Test
    void shouldEncodeSendRequest() {
        SendRequest request = SendRequest.of("r3", "bob", "Привет!", "cli-uuid-001");

        String json = codec.encode(request);

        assertTrue(json.contains("\"type\":\"SEND\""));
        assertTrue(json.contains("\"requestId\":\"r3\""));
        assertTrue(json.contains("\"to\":\"bob\""));
        assertTrue(json.contains("\"text\":\"Привет!\""));
        assertTrue(json.contains("\"clientMsgId\":\"cli-uuid-001\""));
    }

    @Test
    void shouldDecodeAuthOkResponse() {
        String json = """
                {"type":"AUTH_OK","requestId":"r1","username":"alice"}
                """;

        ServerResponse response = codec.decode(json);

        AuthOkResponse authOk = assertInstanceOf(AuthOkResponse.class, response);
        assertEquals(ProtocolMessageType.AUTH_OK, authOk.type());
        assertEquals("r1", authOk.requestId());
        assertEquals("alice", authOk.username());
    }

    @Test
    void shouldDecodeAuthErrorResponse() {
        String json = """
                {"type":"AUTH_ERROR","requestId":"r2","errorCode":"INVALID_CREDENTIALS","message":"Invalid username or password"}
                """;

        ServerResponse response = codec.decode(json);

        AuthErrorResponse error = assertInstanceOf(AuthErrorResponse.class, response);
        assertEquals(ProtocolMessageType.AUTH_ERROR, error.type());
        assertEquals("r2", error.requestId());
        assertEquals("INVALID_CREDENTIALS", error.errorCode());
        assertEquals("Invalid username or password", error.message());
    }

    @Test
    void shouldDecodeAckResponse() {
        String json = """
                {"type":"ACK","requestId":"r3","serverMsgId":"uuid-123","clientMsgId":"c1","acceptedAt":"2026-03-28T10:00:00Z","status":"DELIVERED"}
                """;

        ServerResponse response = codec.decode(json);

        AckResponse ack = assertInstanceOf(AckResponse.class, response);
        assertEquals(ProtocolMessageType.ACK, ack.type());
        assertEquals("r3", ack.requestId());
        assertEquals("uuid-123", ack.serverMsgId());
        assertEquals("c1", ack.clientMsgId());
        assertEquals(Instant.parse("2026-03-28T10:00:00Z"), ack.acceptedAt());
        assertEquals("DELIVERED", ack.status());
    }

    @Test
    void shouldDecodeIncomingResponseWithNullRequestId() {
        String json = """
                {"type":"INCOMING","requestId":null,"messageId":"uuid-123","from":"alice","text":"Привет, Bob","createdAt":"2026-03-28T10:00:00Z","clientMsgId":"c1"}
                """;

        ServerResponse response = codec.decode(json);

        IncomingResponse incoming = assertInstanceOf(IncomingResponse.class, response);
        assertEquals(ProtocolMessageType.INCOMING, incoming.type());
        assertNull(incoming.requestId());
        assertEquals("uuid-123", incoming.messageId());
        assertEquals("alice", incoming.from());
        assertEquals("Привет, Bob", incoming.text());
        assertEquals(Instant.parse("2026-03-28T10:00:00Z"), incoming.createdAt());
        assertEquals("c1", incoming.clientMsgId());
    }

    @Test
    void shouldDecodeErrorResponseWithRequestId() {
        String json = """
                {"type":"ERROR","requestId":"r4","errorCode":"SENDER_NOT_AUTHENTICATED","message":"You must authenticate before sending messages"}
                """;

        ServerResponse response = codec.decode(json);

        ErrorResponse error = assertInstanceOf(ErrorResponse.class, response);
        assertEquals(ProtocolMessageType.ERROR, error.type());
        assertEquals("r4", error.requestId());
        assertEquals("SENDER_NOT_AUTHENTICATED", error.errorCode());
        assertEquals("You must authenticate before sending messages", error.message());
    }

    @Test
    void shouldDecodeProtocolErrorWithNullRequestId() {
        String json = """
                {"type":"ERROR","requestId":null,"errorCode":"PROTOCOL_ERROR","message":"Malformed JSON request"}
                """;

        ServerResponse response = codec.decode(json);

        ErrorResponse error = assertInstanceOf(ErrorResponse.class, response);
        assertEquals(ProtocolMessageType.ERROR, error.type());
        assertNull(error.requestId());
        assertEquals("PROTOCOL_ERROR", error.errorCode());
        assertEquals("Malformed JSON request", error.message());
    }
}
