package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.AuthenticationService;
import com.kstrinadka.chat.server.application.PasswordVerifier;
import com.kstrinadka.chat.server.application.SessionRegistry;
import com.kstrinadka.chat.server.application.UserRepository;
import com.kstrinadka.chat.server.domain.AuthResult;

public final class DefaultAuthenticationService implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;
    private final SessionRegistry sessionRegistry;

    public DefaultAuthenticationService(
            UserRepository userRepository,
            PasswordVerifier passwordVerifier,
            SessionRegistry sessionRegistry
    ) {
        this.userRepository = userRepository;
        this.passwordVerifier = passwordVerifier;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public AuthResult authenticate(String username, String password) {
        throw new UnsupportedOperationException("not implemented");
    }
}
