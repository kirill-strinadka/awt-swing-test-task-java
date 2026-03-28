package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.SessionFactory;
import com.kstrinadka.chat.server.domain.ClientSession;

public final class DefaultSessionFactory implements SessionFactory {

    @Override
    public ClientSession create(String username) {
        throw new UnsupportedOperationException("not implemented");
    }
}
