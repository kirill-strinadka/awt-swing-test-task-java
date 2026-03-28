package com.kstrinadka.chat.client.presentation.chat;

import com.kstrinadka.chat.client.ui.chat.FakeMessageFactory;
import com.kstrinadka.chat.client.ui.chat.MessageDirection;
import com.kstrinadka.chat.client.ui.chat.MessageStatus;
import com.kstrinadka.chat.client.ui.chat.MessageVm;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatPresenter {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ChatView view;
    private final List<MessageVm> localMessages = new ArrayList<>();

    public ChatPresenter(ChatView view) {
        this.view = view;
    }

    public void initialize() {
        localMessages.clear();
        localMessages.addAll(FakeMessageFactory.createFakeMessages());

        view.setSendEnabled(true);
        view.showMessages(List.copyOf(localMessages));
    }

    public void onSendClicked(String rawText) {
        if (rawText == null) {
            return;
        }

        String trimmedText = rawText.trim();
        if (trimmedText.isBlank()) {
            return;
        }

        MessageVm outgoingMessage = new MessageVm(
                trimmedText,
                MessageDirection.OUTGOING,
                LocalTime.now().format(TIME_FORMATTER),
                MessageStatus.SENDING
        );

        localMessages.add(outgoingMessage);
        view.appendMessage(outgoingMessage);
        view.clearInput();
    }
}
