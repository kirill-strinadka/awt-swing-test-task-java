# Дерево модуля `server`

Актуально для исходников под `src/main/java` и корня модуля. Каталог `target/` после сборки не перечисляется.

```text
server/
├── doc/
│   ├── README.md
│   ├── project-overview.md
│   ├── request-flow.md
│   ├── server-architecture.md
│   └── server-module-tree.md
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/kstrinadka/chat/server/
                ├── bootstrap/
                │   ├── ApplicationAssembler.java
                │   ├── ChatServerApplication.java
                │   └── ServerApplicationContext.java
                ├── config/
                │   ├── ServerConfig.java
                │   └── TestUsersConfig.java
                ├── domain/
                │   ├── AuthErrorCode.java
                │   ├── AuthResult.java
                │   ├── ChatMessage.java
                │   ├── ClientSession.java
                │   ├── DeliveryErrorCode.java
                │   ├── DeliveryResult.java
                │   ├── SessionId.java
                │   └── User.java
                ├── protocol/
                │   ├── AckResponse.java
                │   ├── AuthErrorResponse.java
                │   ├── AuthOkResponse.java
                │   ├── AuthRequest.java
                │   ├── ClientRequest.java
                │   ├── ErrorResponse.java
                │   ├── IncomingMessageResponse.java
                │   ├── ProtocolException.java
                │   ├── ProtocolMessageCodec.java
                │   ├── ProtocolTypes.java
                │   ├── ProtocolValidator.java
                │   ├── SendMessageRequest.java
                │   └── ServerResponse.java
                ├── application/
                │   ├── AuthenticationService.java
                │   ├── AuthUseCase.java
                │   ├── MessageDeliveryService.java
                │   ├── MessageIdGenerator.java
                │   ├── MessageService.java
                │   ├── PasswordVerifier.java
                │   ├── RequestDispatcher.java
                │   ├── SendMessageUseCase.java
                │   ├── SessionFactory.java
                │   ├── SessionRegistry.java
                │   └── UserRepository.java
                ├── transport/
                │   ├── ChatServer.java
                │   ├── ClientConnection.java
                │   ├── ClientConnectionFactory.java
                │   ├── ConnectionContext.java
                │   ├── ConnectionContextFactory.java
                │   ├── ConnectionState.java
                │   ├── OutboundChannel.java
                │   └── SocketClientConnection.java
                └── infrastructure/
                    ├── DefaultAuthenticationService.java
                    ├── DefaultMessageDeliveryService.java
                    ├── DefaultMessageService.java
                    ├── DefaultProtocolValidator.java
                    ├── DefaultRequestDispatcher.java
                    ├── DefaultSessionFactory.java
                    ├── InMemorySessionRegistry.java
                    ├── InMemoryUserRepository.java
                    ├── JacksonProtocolMessageCodec.java
                    ├── Sha256PasswordVerifier.java
                    └── UuidMessageIdGenerator.java
```

Папки `src/test/java` в модуле пока нет.
