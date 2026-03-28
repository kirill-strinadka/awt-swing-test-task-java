package com.kstrinadka.chat.server.transport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kstrinadka.chat.server.bootstrap.ApplicationAssembler;
import com.kstrinadka.chat.server.bootstrap.ServerApplicationContext;
import com.kstrinadka.chat.server.config.ServerConfig;
import com.kstrinadka.chat.server.config.TestUsersConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class ChatServerSmokeTest {

    private ServerApplicationContext applicationContext;
    private ExecutorService serverExecutor;
    private Future<?> serverFuture;

    @AfterEach
    void tearDown() {
        if (applicationContext != null) {
            applicationContext.chatServer().stop();
        }
        if (serverFuture != null) {
            serverFuture.cancel(true);
        }
        if (serverExecutor != null) {
            serverExecutor.shutdownNow();
        }
        applicationContext = null;
        serverExecutor = null;
        serverFuture = null;
    }

    @Test
    void shouldAuthenticateTwoUsersAndDeliverMessage() throws Exception {
        int port = findFreePort();

        ServerConfig serverConfig = new ServerConfig(
                "127.0.0.1",
                port,
                10_000,
                4096,
                false
        );

        TestUsersConfig usersConfig = new TestUsersConfig(
                Map.of(
                        "alice", "alicepwd",
                        "bob", "bobpwd"
                )
        );

        applicationContext = new ApplicationAssembler().assemble(serverConfig, usersConfig);

        serverExecutor = Executors.newSingleThreadExecutor();
        serverFuture = serverExecutor.submit(() -> {
            try {
                applicationContext.chatServer().start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        waitUntilServerAcceptsConnections(port);

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try (
                Socket aliceSocket = new Socket("127.0.0.1", port);
                Socket bobSocket = new Socket("127.0.0.1", port);
                BufferedReader aliceReader = new BufferedReader(
                        new InputStreamReader(aliceSocket.getInputStream(), StandardCharsets.UTF_8)
                );
                BufferedWriter aliceWriter = new BufferedWriter(
                        new OutputStreamWriter(aliceSocket.getOutputStream(), StandardCharsets.UTF_8)
                );
                BufferedReader bobReader = new BufferedReader(
                        new InputStreamReader(bobSocket.getInputStream(), StandardCharsets.UTF_8)
                );
                BufferedWriter bobWriter = new BufferedWriter(
                        new OutputStreamWriter(bobSocket.getOutputStream(), StandardCharsets.UTF_8)
                )
        ) {
            aliceSocket.setSoTimeout(5_000);
            bobSocket.setSoTimeout(5_000);

            writeLine(aliceWriter, """
                    {"type":"AUTH","requestId":"auth-a","username":"alice","password":"alicepwd"}
                    """);
            JsonNode aliceAuth = mapper.readTree(aliceReader.readLine());
            assertThat(aliceAuth.get("type").asText()).isEqualTo("AUTH_OK");
            assertThat(aliceAuth.get("requestId").asText()).isEqualTo("auth-a");
            assertThat(aliceAuth.get("username").asText()).isEqualTo("alice");

            writeLine(bobWriter, """
                    {"type":"AUTH","requestId":"auth-b","username":"bob","password":"bobpwd"}
                    """);
            JsonNode bobAuth = mapper.readTree(bobReader.readLine());
            assertThat(bobAuth.get("type").asText()).isEqualTo("AUTH_OK");
            assertThat(bobAuth.get("requestId").asText()).isEqualTo("auth-b");
            assertThat(bobAuth.get("username").asText()).isEqualTo("bob");

            writeLine(aliceWriter, """
                    {"type":"SEND","requestId":"send-1","to":"bob","text":"hello bob","clientMsgId":"c-1"}
                    """);

            JsonNode ack = mapper.readTree(aliceReader.readLine());
            assertThat(ack.get("type").asText()).isEqualTo("ACK");
            assertThat(ack.get("requestId").asText()).isEqualTo("send-1");
            assertThat(ack.get("clientMsgId").asText()).isEqualTo("c-1");
            assertThat(ack.get("serverMsgId").asText()).isNotBlank();
            assertThat(ack.get("acceptedAt").asText()).isNotBlank();
            assertThat(ack.get("status").asText()).isEqualTo("DELIVERED");

            JsonNode incoming = mapper.readTree(bobReader.readLine());
            assertThat(incoming.get("type").asText()).isEqualTo("INCOMING");
            assertThat(incoming.get("messageId").asText()).isNotBlank();
            assertThat(incoming.get("from").asText()).isEqualTo("alice");
            assertThat(incoming.get("text").asText()).isEqualTo("hello bob");
            assertThat(incoming.get("clientMsgId").asText()).isEqualTo("c-1");
            assertThat(incoming.get("createdAt").asText()).isNotBlank();
        }
    }

    @Test
    void sendToOfflineRecipient_shouldReturnRecipientOfflineError() throws Exception {
        int port = findFreePort();

        ServerConfig serverConfig = new ServerConfig(
                "127.0.0.1",
                port,
                10_000,
                4096,
                false
        );

        TestUsersConfig usersConfig = new TestUsersConfig(
                Map.of(
                        "alice", "alicepwd",
                        "bob", "bobpwd"
                )
        );

        applicationContext = new ApplicationAssembler().assemble(serverConfig, usersConfig);

        serverExecutor = Executors.newSingleThreadExecutor();
        serverFuture = serverExecutor.submit(() -> {
            try {
                applicationContext.chatServer().start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        waitUntilServerAcceptsConnections(port);

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try (
                Socket aliceSocket = new Socket("127.0.0.1", port);
                BufferedReader aliceReader = new BufferedReader(
                        new InputStreamReader(aliceSocket.getInputStream(), StandardCharsets.UTF_8)
                );
                BufferedWriter aliceWriter = new BufferedWriter(
                        new OutputStreamWriter(aliceSocket.getOutputStream(), StandardCharsets.UTF_8)
                )
        ) {
            aliceSocket.setSoTimeout(5_000);

            writeLine(aliceWriter, """
                    {"type":"AUTH","requestId":"auth-a","username":"alice","password":"alicepwd"}
                    """);
            JsonNode aliceAuth = mapper.readTree(aliceReader.readLine());
            assertThat(aliceAuth.get("type").asText()).isEqualTo("AUTH_OK");

            writeLine(aliceWriter, """
                    {"type":"SEND","requestId":"send-offline","to":"bob","text":"hi","clientMsgId":"c-off"}
                    """);

            JsonNode error = mapper.readTree(aliceReader.readLine());
            assertThat(error.get("type").asText()).isEqualTo("ERROR");
            assertThat(error.get("requestId").asText()).isEqualTo("send-offline");
            assertThat(error.get("errorCode").asText()).isEqualTo("RECIPIENT_OFFLINE");
        }
    }

    private static void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line.strip());
        writer.write('\n');
        writer.flush();
    }

    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void waitUntilServerAcceptsConnections(int port) throws Exception {
        long deadline = System.nanoTime() + Duration.ofSeconds(3).toNanos();
        while (System.nanoTime() < deadline) {
            try (Socket ignored = new Socket("127.0.0.1", port)) {
                return;
            } catch (IOException e) {
                Thread.sleep(50);
            }
        }
        throw new IllegalStateException("Server did not start listening in time");
    }
}
