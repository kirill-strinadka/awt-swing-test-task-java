package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.transport.OutgoingEnvelope;

import java.util.List;

public record DispatchResult(List<OutgoingEnvelope> outgoingEnvelopes) {
}
