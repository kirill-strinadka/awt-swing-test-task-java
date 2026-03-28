package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.domain.ClientSession;

public interface SessionFactory {

    ClientSession create(String username);
}
