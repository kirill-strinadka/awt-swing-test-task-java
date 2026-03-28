package com.kstrinadka.chat.server.application;

import com.kstrinadka.chat.server.protocol.ClientRequest;
import com.kstrinadka.chat.server.transport.ConnectionContext;

public interface RequestDispatcher {

    DispatchResult dispatch(ConnectionContext context, ClientRequest request);
}
