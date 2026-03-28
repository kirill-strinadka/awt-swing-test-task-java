package com.kstrinadka.chat.client.presentation.chat;

import com.kstrinadka.chat.client.ui.chat.MessageVm;

import java.util.List;

public interface ChatView {

    void showMessages(List<MessageVm> messages);

    void appendMessage(MessageVm message);

    void clearInput();

    void setSendEnabled(boolean enabled);

    void showError(String message);
}
