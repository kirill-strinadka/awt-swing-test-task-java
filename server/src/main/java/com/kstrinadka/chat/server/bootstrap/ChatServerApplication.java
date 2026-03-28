package com.kstrinadka.chat.server.bootstrap;

import com.kstrinadka.chat.server.config.ServerConfig;
import com.kstrinadka.chat.server.config.TestUsersConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public final class ChatServerApplication {

    private static final Logger log = LoggerFactory.getLogger(ChatServerApplication.class);

    public static void main(String[] args) {
        ServerConfig serverConfig = new ServerConfig(
                "0.0.0.0",
                9000,
                60_000,
                4096,
                false
        );
        TestUsersConfig testUsers = new TestUsersConfig(
                Map.of(
                        "alice", "alicepwd",
                        "bob", "bobpwd"
                )
        );

        ApplicationAssembler assembler = new ApplicationAssembler();
        ServerApplicationContext context = assembler.assemble(serverConfig, testUsers);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> context.chatServer().stop(), "chat-server-shutdown"));

        try {
            log.info("Starting chat server on {}:{}", serverConfig.host(), serverConfig.port());
            context.chatServer().start();
        } catch (IOException e) {
            log.error("Server failed", e);
            System.exit(1);
        }
    }
}
