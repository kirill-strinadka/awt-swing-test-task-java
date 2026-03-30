package com.kstrinadka.chat.client.ui.chat;

import com.kstrinadka.chat.client.protocol.IncomingResponse;

public interface ChatClientRuntimeListener {

    void onIncoming(IncomingResponse incomingResponse);

    void onDisconnected(Throwable cause);
}

