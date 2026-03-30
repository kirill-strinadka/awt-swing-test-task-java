package com.kstrinadka.chat.client.presentation.login;

import com.kstrinadka.chat.client.app.session.ClientSession;

@FunctionalInterface
public interface LoginSuccessHandler {

    void onLoginSuccess(ClientSession session);
}
