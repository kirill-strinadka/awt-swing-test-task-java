package com.kstrinadka.chat.client.ui.chat;

import com.kstrinadka.chat.client.domain.chat.Message;

import java.time.Instant;
import java.util.List;

public final class FakeMessageFactory {

    private FakeMessageFactory() {
    }

    public static List<Message> createFakeMessages(String currentUsername, String conversationUsername) {
        return List.of(
                new Message(
                        "Привет! Это тестовое входящее сообщение. Проверяем, как выглядит пузырек слева.",
                        MessageDirection.INCOMING,
                        Instant.now().minusSeconds(360),
                        null,
                        "srv-1",
                        MessageStatus.NONE,
                        conversationUsername,
                        currentUsername
                ),
                new Message(
                        "Привет. Это исходящее сообщение справа. Должно напоминать структуру Telegram-like чата.",
                        MessageDirection.OUTGOING,
                        Instant.now().minusSeconds(300),
                        "cli-1",
                        "srv-2",
                        MessageStatus.DELIVERED,
                        currentUsername,
                        conversationUsername
                ),
                new Message(
                        "Тут еще одно длинное сообщение, чтобы проверить перенос строк внутри пузыря. "
                                + "Важно, чтобы текст не вылезал за границы, а аккуратно переносился на новую строку.",
                        MessageDirection.INCOMING,
                        Instant.now().minusSeconds(240),
                        null,
                        "srv-3",
                        MessageStatus.NONE,
                        conversationUsername,
                        currentUsername
                ),
                new Message(
                        "Ок, вижу. Позже сюда добавим настоящую отправку через TCP и статусы ACK / ERROR.",
                        MessageDirection.OUTGOING,
                        Instant.now().minusSeconds(180),
                        "cli-2",
                        null,
                        MessageStatus.SENDING,
                        currentUsername,
                        conversationUsername
                ),
                new Message(
                        "Это пример сообщения с ошибкой доставки. Его статус должен быть выделен другим цветом.",
                        MessageDirection.OUTGOING,
                        Instant.now().minusSeconds(120),
                        "cli-3",
                        null,
                        MessageStatus.FAILED,
                        currentUsername,
                        conversationUsername
                ),
                new Message(
                        "Отлично. Пока это просто визуальный каркас без сети, но уже можно проверять layout и стили.",
                        MessageDirection.INCOMING,
                        Instant.now().minusSeconds(60),
                        null,
                        "srv-4",
                        MessageStatus.NONE,
                        conversationUsername,
                        currentUsername
                )
        );
    }
}
