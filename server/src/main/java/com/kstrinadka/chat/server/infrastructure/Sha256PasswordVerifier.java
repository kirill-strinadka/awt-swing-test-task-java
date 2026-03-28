package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.PasswordVerifier;

public final class Sha256PasswordVerifier implements PasswordVerifier {

    @Override
    public boolean matches(String rawPassword, String storedPasswordHash) {
        throw new UnsupportedOperationException("not implemented");
    }
}
