package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.UserRepository;
import com.kstrinadka.chat.server.domain.User;

import java.util.Map;
import java.util.Optional;

public final class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users;

    public InMemoryUserRepository(Map<String, User> users) {
        this.users = users;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean existsByUsername(String username) {
        throw new UnsupportedOperationException("not implemented");
    }
}
