package com.kstrinadka.chat.client.domain.chat;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ConversationStore {

    private final Map<String, Conversation> conversations = new LinkedHashMap<>();

    public Conversation getOrCreateConversation(String username) {
        Objects.requireNonNull(username, "username must not be null");
        return conversations.computeIfAbsent(username, Conversation::new);
    }

    public Conversation getConversation(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return conversations.get(username);
    }

    public Collection<Conversation> getAllConversations() {
        return conversations.values();
    }

    public void addOutgoingMessage(String to, Message message) {
        Objects.requireNonNull(to, "to must not be null");
        Objects.requireNonNull(message, "message must not be null");

        Conversation conversation = getOrCreateConversation(to);
        conversation.addMessage(message);
    }

    public void addIncomingMessage(String from, Message message) {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(message, "message must not be null");

        Conversation conversation = getOrCreateConversation(from);
        conversation.addMessage(message);
    }

    public Message findByClientMsgId(String clientMsgId) {
        if (clientMsgId == null || clientMsgId.isBlank()) {
            return null;
        }

        for (Conversation conversation : conversations.values()) {
            Message message = conversation.findByClientMsgId(clientMsgId);
            if (message != null) {
                return message;
            }
        }

        return null;
    }

    public boolean replaceMessageByClientMsgId(String clientMsgId, Message updatedMessage) {
        if (clientMsgId == null || clientMsgId.isBlank() || updatedMessage == null) {
            return false;
        }

        for (Conversation conversation : conversations.values()) {
            if (conversation.replaceMessageByClientMsgId(clientMsgId, updatedMessage)) {
                return true;
            }
        }

        return false;
    }
}
