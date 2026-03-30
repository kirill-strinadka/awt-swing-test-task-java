package com.kstrinadka.chat.client.presentation.chat;

import com.kstrinadka.chat.client.app.session.ClientSession;
import com.kstrinadka.chat.client.domain.chat.Conversation;
import com.kstrinadka.chat.client.domain.chat.ConversationStore;
import com.kstrinadka.chat.client.domain.chat.Message;
import com.kstrinadka.chat.client.net.TcpChatClient;
import com.kstrinadka.chat.client.net.TcpChatClientListener;
import com.kstrinadka.chat.client.protocol.AckResponse;
import com.kstrinadka.chat.client.protocol.ClientRequest;
import com.kstrinadka.chat.client.protocol.ProtocolCodec;
import com.kstrinadka.chat.client.protocol.ProtocolMessageType;
import com.kstrinadka.chat.client.protocol.ServerResponse;
import com.kstrinadka.chat.client.ui.chat.ConversationListItemVm;
import com.kstrinadka.chat.client.ui.chat.MessageStatus;
import com.kstrinadka.chat.client.ui.chat.MessageVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatPresenterTest {

    private TestChatView view;
    private FakeTcpChatClient tcpClient;
    private ClientSession session;
    private ChatPresenter presenter;

    @BeforeEach
    void setUp() {
        view = new TestChatView();
        tcpClient = new FakeTcpChatClient();
        session = new ClientSession("me", tcpClient);
        presenter = new ChatPresenter(view, session);
        presenter.initialize();

        // Select a conversation so that send is enabled.
        presenter.onConversationSelected("alice");
        view.reset();
    }

    @Test
    void emptyMessageIsNotSent() {
        presenter.onSendClicked("   ");

        assertNull(tcpClient.lastRequest.get(), "No network request should be sent for empty text");
        assertTrue(view.appendedMessages.isEmpty(), "No local message bubble should be appended");
    }

    @Test
    void sendCreatesLocalMessageWithSendingStatus() throws Exception {
        presenter.onSendClicked("Hello there");

        // One local message must be appended to the view.
        assertEquals(1, view.appendedMessages.size(), "Exactly one outgoing message bubble is expected");
        MessageVm vm = view.appendedMessages.getFirst();
        assertEquals("Hello there", vm.text());
        assertEquals(MessageStatus.SENDING, vm.status());

        // Conversation store must contain the same message with SENDING status.
        ConversationStore store = extractConversationStore(presenter);
        Conversation conversation = store.getConversation("alice");
        assertNotNull(conversation, "Conversation for 'alice' must exist");
        Message lastMessage = conversation.getLastMessage();
        assertNotNull(lastMessage, "Last message must be present");
        assertEquals("Hello there", lastMessage.text());
        assertEquals(MessageStatus.SENDING, lastMessage.status());
    }

    @Test
    void ackChangesStatusToDelivered() throws Exception {
        presenter.onSendClicked("Hi with ack");

        ConversationStore store = extractConversationStore(presenter);
        Conversation conversation = store.getConversation("alice");
        assertNotNull(conversation, "Conversation for 'alice' must exist after send");

        Message initial = conversation.getLastMessage();
        assertNotNull(initial, "Initial message must be stored");
        String clientMsgId = initial.clientMsgId();

        // Simulate ACK arriving for the same clientMsgId
        AckResponse ackResponse = new AckResponse(
                ProtocolMessageType.ACK,
                tcpClient.lastRequest.get().requestId(),
                "server-msg-42",
                clientMsgId,
                Instant.parse("2026-03-30T10:10:00Z"),
                "DELIVERED"
        );

        tcpClient.completeResponse(ackResponse);

        // After completion, the message in the conversation store must be updated to DELIVERED.
        Message updated = store.getConversation("alice").getLastMessage();
        assertEquals(MessageStatus.DELIVERED, updated.status(), "Message status must become DELIVERED after ACK");
        assertEquals("server-msg-42", updated.serverMsgId(), "Server message id must be stored from ACK");
    }

    private static ConversationStore extractConversationStore(ChatPresenter presenter) throws Exception {
        var field = ChatPresenter.class.getDeclaredField("conversationStore");
        field.setAccessible(true);
        return (ConversationStore) field.get(presenter);
    }

    private static final class TestChatView implements ChatView {

        private final List<MessageVm> appendedMessages = new ArrayList<>();

        @Override
        public void showMessages(List<MessageVm> messages) {
            // no-op for tests
        }

        @Override
        public void appendMessage(MessageVm message) {
            appendedMessages.add(message);
        }

        @Override
        public void clearInput() {
            // no-op
        }

        @Override
        public void setSendEnabled(boolean enabled) {
            // no-op
        }

        @Override
        public void showError(String message) {
            // no-op
        }

        @Override
        public void showConversationItems(List<ConversationListItemVm> items, String selectedUsername) {
            // no-op
        }

        @Override
        public void showChatPlaceholder(String text) {
            // no-op
        }

        @Override
        public void showChatContent() {
            // no-op
        }

        void reset() {
            appendedMessages.clear();
        }
    }

    private static final class FakeTcpChatClient extends TcpChatClient {

        private final AtomicReference<ClientRequest> lastRequest = new AtomicReference<>();
        private final CompletableFuture<ServerResponse> responseFuture = new CompletableFuture<>();

        FakeTcpChatClient() {
            super(new NoOpProtocolCodec(), new NoOpListener());
        }

        @Override
        public CompletableFuture<ServerResponse> sendRequestAwaitResponse(ClientRequest request) {
            lastRequest.set(request);
            return responseFuture;
        }

        void completeResponse(ServerResponse response) {
            responseFuture.complete(response);
        }
    }

    private static final class NoOpProtocolCodec implements ProtocolCodec {
        @Override
        public String encode(ClientRequest request) {
            return "";
        }

        @Override
        public ServerResponse decode(String json) {
            throw new UnsupportedOperationException("Not used in tests");
        }
    }

    private static final class NoOpListener implements TcpChatClientListener {
        @Override
        public void onIncoming(com.kstrinadka.chat.client.protocol.IncomingResponse incomingResponse) {
        }

        @Override
        public void onDisconnected(Throwable cause) {
        }

        @Override
        public void onProtocolError(String rawLine, Throwable cause) {
        }
    }
}

