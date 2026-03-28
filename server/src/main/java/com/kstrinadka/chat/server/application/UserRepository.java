package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
