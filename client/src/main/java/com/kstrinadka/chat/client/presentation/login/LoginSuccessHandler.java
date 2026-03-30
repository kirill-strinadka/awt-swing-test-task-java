package com.kstrinadka.chat.client.presentation.login;

@FunctionalInterface
public interface LoginSuccessHandler {

    void onLoginSuccess(String username);
}
