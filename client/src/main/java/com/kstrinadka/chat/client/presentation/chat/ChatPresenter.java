package com.kstrinadka.chat.client.presentation.chat;

import com.kstrinadka.chat.client.app.ConnectionState;
import com.kstrinadka.chat.client.app.session.ClientSession;
import com.kstrinadka.chat.client.domain.chat.Conversation;
import com.kstrinadka.chat.client.domain.chat.ConversationStore;
import com.kstrinadka.chat.client.domain.chat.Message;
import com.kstrinadka.chat.client.protocol.AckResponse;
import com.kstrinadka.chat.client.protocol.ErrorResponse;
import com.kstrinadka.chat.client.protocol.IncomingResponse;
import com.kstrinadka.chat.client.protocol.SendRequest;
import com.kstrinadka.chat.client.protocol.ServerResponse;
import com.kstrinadka.chat.client.ui.chat.ConversationListItemVm;
import com.kstrinadka.chat.client.ui.chat.FakeMessageFactory;
import com.kstrinadka.chat.client.ui.chat.MessageDirection;
import com.kstrinadka.chat.client.ui.chat.MessageStatus;
import com.kstrinadka.chat.client.ui.chat.MessageVm;

import javax.swing.SwingUtilities;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ChatPresenter {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    private final ChatView view;
    private final ClientSession session;
    private final ConversationStore conversationStore = new ConversationStore();

    private String activeConversationUsername;
    private volatile ConnectionState connectionState = ConnectionState.CONNECTED;

    public ChatPresenter(ChatView view, ClientSession session) {
        this.view = view;
        this.session = session;
    }

    public void initialize() {
        ensureConversationExists("alice");
        ensureConversationExists("bob");

        connectionState = ConnectionState.CONNECTED;

        activeConversationUsername = null;
        refreshSidebar();
        view.showChatPlaceholder("Выберите чат слева");
        view.setSendEnabled(false);
    }

    public void onConversationSelected(String conversationUsername) {
        if (conversationUsername == null || conversationUsername.isBlank()) {
            activeConversationUsername = null;
            view.showChatPlaceholder("Выберите чат слева");
            view.setSendEnabled(false);
            refreshSidebar();
            return;
        }

        activeConversationUsername = conversationUsername;
        ensureConversationExists(conversationUsername);
        showActiveConversation();
        refreshSidebar();
        view.showChatContent();
        view.setSendEnabled(true);
    }

    public void onSendClicked(String rawText) {
        if (activeConversationUsername == null || activeConversationUsername.isBlank()) {
            view.showError("Сначала выберите собеседника");
            return;
        }

        if (rawText == null) {
            return;
        }

        String trimmedText = rawText.trim();
        if (trimmedText.isBlank()) {
            return;
        }

        String requestId = UUID.randomUUID().toString();
        String clientMsgId = UUID.randomUUID().toString();

        Message outgoingMessage = new Message(
                trimmedText,
                MessageDirection.OUTGOING,
                Instant.now(),
                clientMsgId,
                null,
                MessageStatus.SENDING,
                session.username(),
                activeConversationUsername
        );

        conversationStore.addOutgoingMessage(activeConversationUsername, outgoingMessage);

        view.appendMessage(toVm(outgoingMessage));
        view.clearInput();
        refreshSidebar();

        SendRequest request = SendRequest.of(
                requestId,
                activeConversationUsername,
                trimmedText,
                clientMsgId
        );

        session.tcpChatClient()
                .sendRequestAwaitResponse(request)
                .whenComplete((response, throwable) -> {
                    if (throwable != null) {
                        handleSendFailure(clientMsgId, "Не удалось отправить сообщение");
                        return;
                    }

                    handleSendResponse(clientMsgId, response);
                });
    }

    public void onIncoming(IncomingResponse incoming) {
        Message message = new Message(
                incoming.text(),
                MessageDirection.INCOMING,
                incoming.createdAt(),
                incoming.clientMsgId(),
                incoming.messageId(),
                MessageStatus.NONE,
                incoming.from(),
                session.username()
        );

        conversationStore.addIncomingMessage(incoming.from(), message);

        SwingUtilities.invokeLater(() -> {
            refreshSidebar();

            if (incoming.from().equals(activeConversationUsername)) {
                showActiveConversation();
            }
        });
    }

    public void onDisconnected(Throwable cause) {
        connectionState = ConnectionState.FAILED;
        SwingUtilities.invokeLater(() -> {
            view.setSendEnabled(false);
            view.showError("Соединение потеряно");
            if (view instanceof com.kstrinadka.chat.client.ui.chat.MainFrame mainFrame) {
                mainFrame.showConnectionState(connectionState);
            }
        });
    }

    private void handleSendResponse(String clientMsgId, ServerResponse response) {
        if (response instanceof AckResponse ackResponse) {
            handleAck(clientMsgId, ackResponse);
            return;
        }

        if (response instanceof ErrorResponse errorResponse) {
            handleError(clientMsgId, errorResponse);
            return;
        }

        handleSendFailure(clientMsgId, "Неожиданный ответ сервера");
    }

    private void handleAck(String clientMsgId, AckResponse ackResponse) {
        Message existing = conversationStore.findByClientMsgId(clientMsgId);
        if (existing == null) {
            return;
        }

        Message updated = existing.withStatusAndServerData(
                MessageStatus.DELIVERED,
                ackResponse.serverMsgId(),
                ackResponse.acceptedAt()
        );

        conversationStore.replaceMessageByClientMsgId(clientMsgId, updated);

        SwingUtilities.invokeLater(() -> {
            if (activeConversationUsername != null) {
                showActiveConversation();
            }
            refreshSidebar();
        });
    }

    private void handleError(String clientMsgId, ErrorResponse errorResponse) {
        Message existing = conversationStore.findByClientMsgId(clientMsgId);
        if (existing != null) {
            Message failed = existing.withStatus(MessageStatus.FAILED);
            conversationStore.replaceMessageByClientMsgId(clientMsgId, failed);
        }

        SwingUtilities.invokeLater(() -> {
            if (activeConversationUsername != null) {
                showActiveConversation();
            }
            refreshSidebar();
            view.showError(mapSendError(errorResponse.errorCode(), errorResponse.message()));
        });
    }

    private void handleSendFailure(String clientMsgId, String userMessage) {
        Message existing = conversationStore.findByClientMsgId(clientMsgId);
        if (existing != null) {
            Message failed = existing.withStatus(MessageStatus.FAILED);
            conversationStore.replaceMessageByClientMsgId(clientMsgId, failed);
        }

        SwingUtilities.invokeLater(() -> {
            if (activeConversationUsername != null) {
                showActiveConversation();
            }
            refreshSidebar();
            view.showError(userMessage);
        });
    }

    private String mapSendError(String errorCode, String message) {
        return switch (errorCode) {
            case "RECIPIENT_OFFLINE" -> "Пользователь сейчас не в сети";
            case "RECIPIENT_NOT_FOUND" -> "Пользователь не найден";
            case "INVALID_TEXT" -> "Некорректный текст сообщения";
            case "SENDER_NOT_AUTHENTICATED" -> "Нужно заново войти в систему";
            default -> "Ошибка отправки: " + message;
        };
    }

    private void ensureConversationExists(String conversationUsername) {
        Conversation conversation = conversationStore.getOrCreateConversation(conversationUsername);
        if (!conversation.isEmpty()) {
            return;
        }

        for (Message message : FakeMessageFactory.createFakeMessages(session.username(), conversationUsername)) {
            if (message.isOutgoing()) {
                conversationStore.addOutgoingMessage(conversationUsername, message);
            } else {
                conversationStore.addIncomingMessage(conversationUsername, message);
            }
        }
    }

    private void showActiveConversation() {
        if (activeConversationUsername == null || activeConversationUsername.isBlank()) {
            view.showChatPlaceholder("Выберите чат слева");
            view.setSendEnabled(false);
            return;
        }

        Conversation conversation = conversationStore.getOrCreateConversation(activeConversationUsername);
        List<MessageVm> messageVms = conversation.getMessages().stream()
                .map(this::toVm)
                .toList();

        view.showChatContent();
        view.showMessages(messageVms);
        view.setSendEnabled(true);
    }

    private void refreshSidebar() {
        List<ConversationListItemVm> items = new ArrayList<>();

        conversationStore.getAllConversations().stream()
                .sorted(Comparator.comparing(this::lastActivityInstant).reversed())
                .forEach(conversation -> items.add(toConversationItemVm(conversation)));

        view.showConversationItems(items, activeConversationUsername);
    }

    private Instant lastActivityInstant(Conversation conversation) {
        Message lastMessage = conversation.getLastMessage();
        return lastMessage != null ? lastMessage.createdAt() : Instant.EPOCH;
    }

    private ConversationListItemVm toConversationItemVm(Conversation conversation) {
        Message lastMessage = conversation.getLastMessage();

        if (lastMessage == null) {
            return new ConversationListItemVm(
                    conversation.getUsername(),
                    "Нет сообщений",
                    ""
            );
        }

        String preview = lastMessage.text();
        if (preview.length() > 32) {
            preview = preview.substring(0, 29) + "...";
        }

        return new ConversationListItemVm(
                conversation.getUsername(),
                preview,
                TIME_FORMATTER.format(lastMessage.createdAt())
        );
    }

    private MessageVm toVm(Message message) {
        return new MessageVm(
                message.text(),
                message.direction(),
                TIME_FORMATTER.format(message.createdAt()),
                message.status()
        );
    }
}
