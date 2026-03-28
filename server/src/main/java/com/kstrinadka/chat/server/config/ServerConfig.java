package com.kstrinadka.chat.server.config;

public record ServerConfig(
        String host,
        int port,
        int socketReadTimeoutMillis,
        int maxMessageLength,
        boolean singleSessionPerUser
) {
}
