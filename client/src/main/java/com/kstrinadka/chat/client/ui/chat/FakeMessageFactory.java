package com.kstrinadka.chat.client.ui.chat;

import java.util.List;

public final class FakeMessageFactory {

    private FakeMessageFactory() {
    }

    public static List<MessageBubblePanel> createFakeMessages() {
        return List.of(
                new MessageBubblePanel(
                        "Привет! Это тестовое входящее сообщение. Проверяем, как выглядит пузырек слева.",
                        "12:01",
                        "",
                        MessageDirection.INCOMING
                ),
                new MessageBubblePanel(
                        "Привет. Это исходящее сообщение справа. Должно напоминать структуру Telegram-like чата.",
                        "12:02",
                        "Delivered",
                        MessageDirection.OUTGOING
                ),
                new MessageBubblePanel(
                        "Тут еще одно длинное сообщение, чтобы проверить перенос строк внутри пузыря. "
                                + "Важно, чтобы текст не вылезал за границы, а аккуратно переносился на новую строку.",
                        "12:03",
                        "",
                        MessageDirection.INCOMING
                ),
                new MessageBubblePanel(
                        "Ок, вижу. Позже сюда добавим настоящую отправку через TCP и статусы ACK / ERROR.",
                        "12:04",
                        "Sending...",
                        MessageDirection.OUTGOING
                ),
                new MessageBubblePanel(
                        "Отлично. Пока это просто визуальный каркас без сети, но уже можно проверять layout и стили.",
                        "12:05",
                        "",
                        MessageDirection.INCOMING
                )
        );
    }
}
