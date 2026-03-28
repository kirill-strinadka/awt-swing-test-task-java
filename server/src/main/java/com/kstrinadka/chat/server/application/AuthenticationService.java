package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.domain.AuthResult;

public interface AuthenticationService {

    AuthResult authenticate(String username, String password);
}
