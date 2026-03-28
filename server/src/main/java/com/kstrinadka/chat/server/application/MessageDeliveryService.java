package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.protocol.IncomingMessageResponse;
import com.kstrinadka.chat.server.transport.ConnectionContext;

/**
 * Delivers a push notification ({@code INCOMING}) to another user's connection.
 * Implementation must be thread-safe on the recipient's {@link com.kstrinadka.chat.server.transport.OutboundChannel}.
 */
public interface MessageDeliveryService {

    boolean deliver(ConnectionContext recipientConnection, IncomingMessageResponse incoming);
}
