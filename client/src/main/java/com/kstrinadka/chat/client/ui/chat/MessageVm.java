package com.kstrinadka.chat.client.ui.chat;

import java.util.Objects;

public record MessageVm(
        String text,
        MessageDirection direction,
        String timeText,
        MessageStatus status
) {

    public MessageVm {
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(direction, "direction must not be null");
        Objects.requireNonNull(timeText, "timeText must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    public boolean isIncoming() {
        return direction == MessageDirection.INCOMING;
    }

    public boolean isOutgoing() {
        return direction == MessageDirection.OUTGOING;
    }
}
