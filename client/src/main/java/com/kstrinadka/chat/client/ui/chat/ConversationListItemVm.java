package com.kstrinadka.chat.client.ui.chat;

import java.util.Objects;

public record ConversationListItemVm(
        String username,
        String lastMessagePreview,
        String timeText
) {

    public ConversationListItemVm {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(lastMessagePreview, "lastMessagePreview must not be null");
        Objects.requireNonNull(timeText, "timeText must not be null");
    }
}

