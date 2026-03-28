package com.kstrinadka.chat.server.application;

public interface PasswordVerifier {

    boolean matches(String rawPassword, String storedPasswordHash);
}
