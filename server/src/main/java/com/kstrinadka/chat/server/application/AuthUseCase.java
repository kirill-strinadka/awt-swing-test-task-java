package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.protocol.AuthRequest;
import com.kstrinadka.chat.server.protocol.ServerResponse;
import com.kstrinadka.chat.server.transport.ConnectionContext;

public final class AuthUseCase {

    private final AuthenticationService authenticationService;
    private final SessionRegistry sessionRegistry;
    private final SessionFactory sessionFactory;

    public AuthUseCase(
            AuthenticationService authenticationService,
            SessionRegistry sessionRegistry,
            SessionFactory sessionFactory
    ) {
        this.authenticationService = authenticationService;
        this.sessionRegistry = sessionRegistry;
        this.sessionFactory = sessionFactory;
    }

    public ServerResponse handle(ConnectionContext context, AuthRequest request) {
        throw new UnsupportedOperationException("not implemented");
    }
}
