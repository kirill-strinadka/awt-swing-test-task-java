package com.kstrinadka.chat.server.domain;

public sealed interface DeliveryResult permits DeliveryResult.Success, DeliveryResult.Failure {

    /** Message passed validation and has server-assigned id/time; socket delivery is a separate step. */
    record Success(ChatMessage message) implements DeliveryResult {
    }

    record Failure(DeliveryErrorCode errorCode, String message) implements DeliveryResult {
    }
}
