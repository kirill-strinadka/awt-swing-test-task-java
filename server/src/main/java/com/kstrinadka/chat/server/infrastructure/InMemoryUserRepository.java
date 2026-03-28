package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.UserRepository;
import com.kstrinadka.chat.server.domain.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryUserRepository implements UserRepository {

    private final ConcurrentHashMap<String, User> usersByUsername;

    public InMemoryUserRepository(Map<String, User> users) {
        this.usersByUsername = new ConcurrentHashMap<>(users);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(usersByUsername.get(username));
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return usersByUsername.containsKey(username);
    }
}
