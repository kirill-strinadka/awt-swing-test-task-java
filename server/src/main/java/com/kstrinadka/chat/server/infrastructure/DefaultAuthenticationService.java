package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.AuthenticationService;
import com.kstrinadka.chat.server.application.PasswordVerifier;
import com.kstrinadka.chat.server.application.UserRepository;
import com.kstrinadka.chat.server.domain.AuthErrorCode;
import com.kstrinadka.chat.server.domain.AuthResult;
import com.kstrinadka.chat.server.domain.User;

import java.util.Optional;

public final class DefaultAuthenticationService implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;

    public DefaultAuthenticationService(UserRepository userRepository, PasswordVerifier passwordVerifier) {
        this.userRepository = userRepository;
        this.passwordVerifier = passwordVerifier;
    }

    @Override
    public AuthResult authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return new AuthResult.Failure(AuthErrorCode.INVALID_CREDENTIALS, "Username and password are required");
        }

        Optional<User> userOpt = userRepository.findByUsername(username.strip());
        if (userOpt.isEmpty()) {
            return new AuthResult.Failure(AuthErrorCode.INVALID_CREDENTIALS, "Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordVerifier.matches(password, user.passwordHash())) {
            return new AuthResult.Failure(AuthErrorCode.INVALID_CREDENTIALS, "Invalid username or password");
        }

        return new AuthResult.Success(user);
    }
}
