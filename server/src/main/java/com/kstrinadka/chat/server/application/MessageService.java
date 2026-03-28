package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.domain.DeliveryResult;

public interface MessageService {

    DeliveryResult sendMessage(String from, String to, String text, String clientMessageId);
}
