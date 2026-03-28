package com.kstrinadka.chat.client.presentation.chat;

import com.kstrinadka.chat.client.domain.chat.Conversation;
import com.kstrinadka.chat.client.domain.chat.ConversationStore;
import com.kstrinadka.chat.client.domain.chat.Message;
import com.kstrinadka.chat.client.ui.chat.FakeMessageFactory;
import com.kstrinadka.chat.client.ui.chat.MessageDirection;
import com.kstrinadka.chat.client.ui.chat.MessageStatus;
import com.kstrinadka.chat.client.ui.chat.MessageVm;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class ChatPresenter {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    private final ChatView view;
    private final ConversationStore conversationStore = new ConversationStore();

    private final String currentUsername;
    private String activeConversationUsername = "alice";

    public ChatPresenter(ChatView view, String currentUsername) {
        this.view = view;
        this.currentUsername = currentUsername;
    }

    public void initialize() {
        loadFakeConversation(activeConversationUsername);
        showActiveConversation();
        view.setSendEnabled(true);
    }

    public void onSendClicked(String rawText) {
        if (rawText == null) {
            return;
        }

        String trimmedText = rawText.trim();
        if (trimmedText.isBlank()) {
            return;
        }

        Message outgoingMessage = new Message(
                trimmedText,
                MessageDirection.OUTGOING,
                Instant.now(),
                UUID.randomUUID().toString(),
                null,
                MessageStatus.SENDING,
                currentUsername,
                activeConversationUsername
        );

        conversationStore.addOutgoingMessage(activeConversationUsername, outgoingMessage);

        view.appendMessage(toVm(outgoingMessage));
        view.clearInput();
    }

    private void loadFakeConversation(String conversationUsername) {
        Conversation conversation = conversationStore.getOrCreateConversation(conversationUsername);
        if (!conversation.getMessages().isEmpty()) {
            return;
        }

        for (Message message : FakeMessageFactory.createFakeMessages(currentUsername, conversationUsername)) {
            if (message.isOutgoing()) {
                conversationStore.addOutgoingMessage(conversationUsername, message);
            } else {
                conversationStore.addIncomingMessage(conversationUsername, message);
            }
        }
    }

    private void showActiveConversation() {
        Conversation conversation = conversationStore.getOrCreateConversation(activeConversationUsername);
        List<MessageVm> messageVms = conversation.getMessages().stream()
                .map(this::toVm)
                .toList();

        view.showMessages(messageVms);
    }

    private MessageVm toVm(Message message) {
        return new MessageVm(
                message.text(),
                message.direction(),
                TIME_FORMATTER.format(message.createdAt()),
                message.status()
        );
    }
}
