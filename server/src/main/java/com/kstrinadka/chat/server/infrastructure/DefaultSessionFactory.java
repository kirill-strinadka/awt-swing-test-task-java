package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.SessionFactory;
import com.kstrinadka.chat.server.domain.ClientSession;
import com.kstrinadka.chat.server.domain.SessionId;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

public final class DefaultSessionFactory implements SessionFactory {

    private final Clock clock;

    public DefaultSessionFactory() {
        this(Clock.systemUTC());
    }

    public DefaultSessionFactory(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public ClientSession create(String username) {
        return new ClientSession(
                new SessionId(UUID.randomUUID().toString()),
                username,
                clock.instant()
        );
    }
}
