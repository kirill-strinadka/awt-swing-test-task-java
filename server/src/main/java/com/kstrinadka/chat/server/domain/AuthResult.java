package com.kstrinadka.chat.server.domain;

public sealed interface AuthResult permits AuthResult.Success, AuthResult.Failure {

    record Success(User user) implements AuthResult {
    }

    record Failure(AuthErrorCode errorCode, String message) implements AuthResult {
    }
}
