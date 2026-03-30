package com.kstrinadka.chat.client.presentation.login;

public interface LoginView {

    String getHost();

    int getPort();

    String getUsername();

    char[] getPassword();

    void setLoading(boolean loading);

    void showError(String message);

    void close();
}
