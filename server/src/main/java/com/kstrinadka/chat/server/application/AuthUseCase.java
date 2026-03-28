package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.domain.AuthErrorCode;
import com.kstrinadka.chat.server.domain.AuthResult;
import com.kstrinadka.chat.server.domain.ClientSession;
import com.kstrinadka.chat.server.protocol.AuthErrorResponse;
import com.kstrinadka.chat.server.protocol.AuthOkResponse;
import com.kstrinadka.chat.server.protocol.AuthRequest;
import com.kstrinadka.chat.server.protocol.ProtocolTypes;
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
        if (context.isAuthenticated()) {
            return new AuthErrorResponse(
                    ProtocolTypes.AUTH_ERROR,
                    request.requestId(),
                    AuthErrorCode.ALREADY_AUTHENTICATED.name(),
                    "Connection is already authenticated"
            );
        }

        AuthResult authResult = authenticationService.authenticate(request.username(), request.password());
        if (authResult instanceof AuthResult.Failure failure) {
            return new AuthErrorResponse(
                    ProtocolTypes.AUTH_ERROR,
                    request.requestId(),
                    failure.errorCode().name(),
                    failure.message()
            );
        }

        AuthResult.Success success = (AuthResult.Success) authResult;
        String username = success.user().username();

        ClientSession session = sessionFactory.create(username);
        boolean registered = sessionRegistry.register(username, context, session);
        if (!registered) {
            return new AuthErrorResponse(
                    ProtocolTypes.AUTH_ERROR,
                    request.requestId(),
                    AuthErrorCode.USER_ALREADY_LOGGED_IN.name(),
                    "User is already logged in"
            );
        }

        context.markAuthenticated(username);
        return new AuthOkResponse(ProtocolTypes.AUTH_OK, request.requestId(), username);
    }
}
