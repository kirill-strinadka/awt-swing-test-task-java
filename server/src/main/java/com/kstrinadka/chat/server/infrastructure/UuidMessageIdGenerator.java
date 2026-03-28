package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.MessageIdGenerator;

import java.util.UUID;

public final class UuidMessageIdGenerator implements MessageIdGenerator {

    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
