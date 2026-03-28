package com.kstrinadka.chat.server.bootstrap;

import com.kstrinadka.chat.server.application.MessageDeliveryService;
import com.kstrinadka.chat.server.application.MessageService;
import com.kstrinadka.chat.server.application.RequestDispatcher;
import com.kstrinadka.chat.server.application.SessionRegistry;
import com.kstrinadka.chat.server.application.UserRepository;
import com.kstrinadka.chat.server.config.ServerConfig;
import com.kstrinadka.chat.server.transport.ChatServer;

/**
 * Composition root output: application core plus TCP {@link ChatServer}.
 */
public record ServerApplicationContext(
        ServerConfig serverConfig,
        UserRepository userRepository,
        SessionRegistry sessionRegistry,
        RequestDispatcher requestDispatcher,
        MessageService messageService,
        MessageDeliveryService messageDeliveryService,
        ChatServer chatServer
) {
}
