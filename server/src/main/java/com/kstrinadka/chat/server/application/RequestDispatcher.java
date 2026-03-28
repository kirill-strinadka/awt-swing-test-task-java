package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.protocol.ServerResponse;
import com.kstrinadka.chat.server.transport.ConnectionContext;

/**
 * Routes a decoded request to a use case. Returns the response for <em>this</em> connection only
 * (e.g. {@code AUTH_*}, {@code ACK}, {@code ERROR}). Push to other connections uses {@link MessageDeliveryService}.
 */
public interface RequestDispatcher {

    ServerResponse dispatch(ConnectionContext context, ClientRequest request);
}
