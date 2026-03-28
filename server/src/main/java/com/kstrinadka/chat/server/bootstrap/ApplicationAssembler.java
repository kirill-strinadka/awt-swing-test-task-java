package com.kstrinadka.chat.server.bootstrap;

import com.kstrinadka.chat.server.application.AuthUseCase;
import com.kstrinadka.chat.server.application.AuthenticationService;
import com.kstrinadka.chat.server.application.MessageDeliveryService;
import com.kstrinadka.chat.server.application.MessageIdGenerator;
import com.kstrinadka.chat.server.application.MessageService;
import com.kstrinadka.chat.server.application.RequestDispatcher;
import com.kstrinadka.chat.server.application.SendMessageUseCase;
import com.kstrinadka.chat.server.application.SessionFactory;
import com.kstrinadka.chat.server.application.SessionRegistry;
import com.kstrinadka.chat.server.application.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kstrinadka.chat.server.config.ServerConfig;
import com.kstrinadka.chat.server.config.TestUsersConfig;
import com.kstrinadka.chat.server.domain.User;
import com.kstrinadka.chat.server.infrastructure.DefaultAuthenticationService;
import com.kstrinadka.chat.server.infrastructure.DefaultClientConnectionFactory;
import com.kstrinadka.chat.server.infrastructure.DefaultMessageDeliveryService;
import com.kstrinadka.chat.server.infrastructure.DefaultMessageService;
import com.kstrinadka.chat.server.infrastructure.DefaultProtocolValidator;
import com.kstrinadka.chat.server.infrastructure.DefaultRequestDispatcher;
import com.kstrinadka.chat.server.infrastructure.DefaultSessionFactory;
import com.kstrinadka.chat.server.infrastructure.InMemorySessionRegistry;
import com.kstrinadka.chat.server.infrastructure.InMemoryUserRepository;
import com.kstrinadka.chat.server.infrastructure.JacksonProtocolMessageCodec;
import com.kstrinadka.chat.server.infrastructure.Sha256PasswordVerifier;
import com.kstrinadka.chat.server.infrastructure.SessionUnregistrationCloseHandler;
import com.kstrinadka.chat.server.infrastructure.UuidMessageIdGenerator;
import com.kstrinadka.chat.server.transport.ChatServer;
import com.kstrinadka.chat.server.transport.DefaultConnectionContextFactory;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ApplicationAssembler {

    /**
     * Builds users from {@link TestUsersConfig}: map values are <strong>plaintext</strong> passwords, stored as SHA-256 hex in {@link User#passwordHash()}.
     */
    public ServerApplicationContext assemble(ServerConfig serverConfig, TestUsersConfig testUsersConfig) {
        Objects.requireNonNull(serverConfig);
        Objects.requireNonNull(testUsersConfig);

        Sha256PasswordVerifier passwordVerifier = new Sha256PasswordVerifier();
        UserRepository userRepository = new InMemoryUserRepository(buildUserMap(testUsersConfig, passwordVerifier));
        SessionRegistry sessionRegistry = new InMemorySessionRegistry();
        AuthenticationService authenticationService = new DefaultAuthenticationService(userRepository, passwordVerifier);

        MessageIdGenerator messageIdGenerator = new UuidMessageIdGenerator();
        Clock clock = Clock.systemUTC();
        MessageService messageService = new DefaultMessageService(
                messageIdGenerator,
                clock,
                serverConfig.maxMessageLength()
        );

        MessageDeliveryService messageDeliveryService = new DefaultMessageDeliveryService();
        SessionFactory sessionFactory = new DefaultSessionFactory(clock);

        AuthUseCase authUseCase = new AuthUseCase(authenticationService, sessionRegistry, sessionFactory);
        SendMessageUseCase sendMessageUseCase = new SendMessageUseCase(
                userRepository,
                sessionRegistry,
                messageService,
                messageDeliveryService
        );
        RequestDispatcher requestDispatcher = new DefaultRequestDispatcher(authUseCase, sendMessageUseCase);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JacksonProtocolMessageCodec codec = new JacksonProtocolMessageCodec(objectMapper);
        DefaultProtocolValidator protocolValidator = new DefaultProtocolValidator(serverConfig.maxMessageLength());
        int maxRawMessageLength = rawMessageLineLimit(serverConfig);
        var clientConnectionFactory = new DefaultClientConnectionFactory(
                codec,
                protocolValidator,
                requestDispatcher,
                new DefaultConnectionContextFactory(),
                new SessionUnregistrationCloseHandler(sessionRegistry),
                maxRawMessageLength
        );
        ChatServer chatServer = new ChatServer(serverConfig, clientConnectionFactory);

        return new ServerApplicationContext(
                serverConfig,
                userRepository,
                sessionRegistry,
                requestDispatcher,
                messageService,
                messageDeliveryService,
                chatServer
        );
    }

    /**
     * Upper bound for one JSON line on the wire (includes framing fields, not only chat text).
     */
    private static int rawMessageLineLimit(ServerConfig config) {
        return Math.max(8192, config.maxMessageLength() + 2048);
    }

    private static Map<String, User> buildUserMap(TestUsersConfig config, Sha256PasswordVerifier verifier) {
        Map<String, User> map = new HashMap<>();
        Map<String, String> raw = config.users();
        if (raw != null) {
            for (Map.Entry<String, String> e : raw.entrySet()) {
                String username = e.getKey();
                String password = e.getValue();
                if (username == null || username.isBlank()) {
                    continue;
                }
                if (password == null) {
                    continue;
                }
                map.put(username.strip(), new User(username.strip(), verifier.hash(password)));
            }
        }
        return Map.copyOf(map);
    }
}
