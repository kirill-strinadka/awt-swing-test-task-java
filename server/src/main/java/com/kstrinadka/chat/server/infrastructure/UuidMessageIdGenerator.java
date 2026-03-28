package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.MessageIdGenerator;

public final class UuidMessageIdGenerator implements MessageIdGenerator {

    @Override
    public String nextId() {
        throw new UnsupportedOperationException("not implemented");
    }
}
