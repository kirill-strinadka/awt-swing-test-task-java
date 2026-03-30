package com.kstrinadka.chat.client.net;

import com.kstrinadka.chat.client.protocol.ClientRequest;
import com.kstrinadka.chat.client.protocol.ProtocolMessageType;
import com.kstrinadka.chat.client.protocol.ProtocolCodec;
import com.kstrinadka.chat.client.protocol.ServerResponse;
import com.kstrinadka.chat.client.protocol.AckResponse;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TcpChatClientCorrelationTest {

    @Test
    void requestIsPutIntoPendingAndResponseCompletesSameFuture() throws Exception {
        // Arrange: dummy codec that produces some JSON, but we do not care about its contents.
        ProtocolCodec codec = new ProtocolCodec() {
            @Override
            public String encode(ClientRequest request) {
                return "{\"type\":\"AUTH\",\"requestId\":\"" + request.requestId() + "\"}";
            }

            @Override
            public ServerResponse decode(String json) {
                throw new UnsupportedOperationException("decode is not used in this test");
            }
        };

        TcpChatClientListener listener = new TcpChatClientListener() {
            @Override
            public void onIncoming(com.kstrinadka.chat.client.protocol.IncomingResponse incomingResponse) {
            }

            @Override
            public void onDisconnected(Throwable cause) {
            }

            @Override
            public void onProtocolError(String rawLine, Throwable cause) {
            }
        };

        TcpChatClient client = new TcpChatClient(codec, listener);

        // Make the client appear "connected" and provide a dummy writer so sendRequest() succeeds.
        Field connectedField = TcpChatClient.class.getDeclaredField("connected");
        connectedField.setAccessible(true);
        connectedField.set(client, true);

        Field writerField = TcpChatClient.class.getDeclaredField("writer");
        writerField.setAccessible(true);
        writerField.set(client, new BufferedWriter(new StringWriter()));

        // Build a minimal ClientRequest
        ClientRequest request = new ClientRequest() {
            @Override
            public ProtocolMessageType type() {
                return ProtocolMessageType.AUTH;
            }

            @Override
            public String requestId() {
                return "req-123";
            }
        };

        // Act: send request and capture the returned future
        CompletableFuture<ServerResponse> future = client.sendRequestAwaitResponse(request);

        // Assert part 1: request was put into pendingRequests
        Field pendingField = TcpChatClient.class.getDeclaredField("pendingRequests");
        pendingField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, CompletableFuture<ServerResponse>> pending =
                (Map<String, CompletableFuture<ServerResponse>>) pendingField.get(client);

        CompletableFuture<ServerResponse> storedFuture = pending.get("req-123");
        assertNotNull(storedFuture, "Request must be present in pendingRequests");
        assertEquals(storedFuture, future, "Stored future must be the same instance that was returned");

        // Act: manually dispatch an ACK response with the same requestId
        AckResponse ackResponse = new AckResponse(
                ProtocolMessageType.ACK,
                "req-123",
                "server-msg-1",
                "client-msg-1",
                Instant.parse("2026-03-30T10:00:00Z"),
                "DELIVERED"
        );

        Method dispatchMethod = TcpChatClient.class.getDeclaredMethod("dispatch", com.kstrinadka.chat.client.protocol.ServerResponse.class);
        dispatchMethod.setAccessible(true);
        dispatchMethod.invoke(client, ackResponse);

        // Assert part 2: the same future is completed with our response and removed from pending
        assertTrue(future.isDone(), "Future must be completed after dispatch");
        ServerResponse completed = future.get();
        assertEquals(ackResponse, completed, "Future must complete with the dispatched response");

        assertFalse(pending.containsKey("req-123"), "Pending map must no longer contain the request after completion");
    }
}
