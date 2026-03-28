package com.kstrinadka.chat.client.net;

import com.kstrinadka.chat.client.protocol.IncomingResponse;

public interface TcpChatClientListener {

    void onIncoming(IncomingResponse incomingResponse);

    void onDisconnected(Throwable cause);

    void onProtocolError(String rawLine, Throwable cause);
}
