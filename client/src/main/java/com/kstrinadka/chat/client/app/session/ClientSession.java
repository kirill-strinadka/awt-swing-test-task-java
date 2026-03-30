package com.kstrinadka.chat.client.app.session;

import com.kstrinadka.chat.client.net.TcpChatClient;
import com.kstrinadka.chat.client.protocol.IncomingResponse;

import java.util.Objects;
import java.util.function.Consumer;

public final class ClientSession {

    private final String username;
    private final TcpChatClient tcpChatClient;

    private volatile Consumer<IncomingResponse> incomingBridge;
    private volatile Consumer<Throwable> disconnectBridge;

    public ClientSession(String username, TcpChatClient tcpChatClient) {
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.tcpChatClient = Objects.requireNonNull(tcpChatClient, "tcpChatClient must not be null");
    }

    public String username() {
        return username;
    }

    public TcpChatClient tcpChatClient() {
        return tcpChatClient;
    }

    public void setIncomingBridge(Consumer<IncomingResponse> incomingBridge) {
        this.incomingBridge = incomingBridge;
    }

    public void setDisconnectBridge(Consumer<Throwable> disconnectBridge) {
        this.disconnectBridge = disconnectBridge;
    }

    public Consumer<IncomingResponse> incomingBridge() {
        return incomingBridge;
    }

    public Consumer<Throwable> disconnectBridge() {
        return disconnectBridge;
    }
}

