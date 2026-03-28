package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.protocol.ServerResponse;
import com.kstrinadka.chat.server.transport.OutgoingEnvelope;

import java.util.Optional;

public record SendDispatchResult(
        ServerResponse senderResponse,
        Optional<OutgoingEnvelope> recipientResponse
) {
}
