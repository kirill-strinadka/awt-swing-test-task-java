package com.kstrinadka.chat.server.transport;

import com.kstrinadka.chat.server.protocol.ServerResponse;

public record OutgoingEnvelope(ConnectionContext target, ServerResponse response) {
}
