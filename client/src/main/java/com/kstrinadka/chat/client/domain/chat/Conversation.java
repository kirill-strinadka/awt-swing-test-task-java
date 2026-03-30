package com.kstrinadka.chat.client.domain.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Conversation {

    private final String username;
    private final List<Message> messages = new ArrayList<>();

    public Conversation(String username) {
        this.username = Objects.requireNonNull(username, "username must not be null");
    }

    public String getUsername() {
        return username;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public void addMessage(Message message) {
        messages.add(Objects.requireNonNull(message, "message must not be null"));
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    public Message findByClientMsgId(String clientMsgId) {
        if (clientMsgId == null || clientMsgId.isBlank()) {
            return null;
        }

        for (Message message : messages) {
            if (clientMsgId.equals(message.clientMsgId())) {
                return message;
            }
        }
        return null;
    }

    public boolean replaceMessageByClientMsgId(String clientMsgId, Message updatedMessage) {
        if (clientMsgId == null || clientMsgId.isBlank() || updatedMessage == null) {
            return false;
        }

        for (int i = 0; i < messages.size(); i++) {
            Message current = messages.get(i);
            if (clientMsgId.equals(current.clientMsgId())) {
                messages.set(i, updatedMessage);
                return true;
            }
        }

        return false;
    }
}
