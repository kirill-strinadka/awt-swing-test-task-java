package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.domain.DeliveryResult;

public interface MessageService {

    /**
     * Validates send rules and builds a {@link com.kstrinadka.chat.server.domain.ChatMessage} with server ids/time.
     * Does not write to sockets; delivery is orchestrated in {@link SendMessageUseCase}.
     */
    DeliveryResult prepareMessage(String from, String to, String text, String clientMessageId);
}
