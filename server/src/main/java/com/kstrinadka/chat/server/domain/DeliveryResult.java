package com.kstrinadka.chat.server.domain;

public sealed interface DeliveryResult permits DeliveryResult.Success, DeliveryResult.Failure {

    record Success(ChatMessage message, boolean deliveredOnline) implements DeliveryResult {
    }

    record Failure(DeliveryErrorCode errorCode, String message) implements DeliveryResult {
    }
}
