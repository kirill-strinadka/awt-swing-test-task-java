package com.kstrinadka.chat.server.infrastructure;

import com.kstrinadka.chat.server.application.MessageDeliveryService;
import com.kstrinadka.chat.server.protocol.IncomingMessageResponse;
import com.kstrinadka.chat.server.transport.ConnectionContext;

public final class DefaultMessageDeliveryService implements MessageDeliveryService {

    @Override
    public boolean deliver(ConnectionContext recipientConnection, IncomingMessageResponse incoming) {
        if (recipientConnection == null || incoming == null) {
            return false;
        }
        if (!recipientConnection.isAuthenticated()) {
            return false;
        }
        try {
            recipientConnection.outboundChannel().send(incoming);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
