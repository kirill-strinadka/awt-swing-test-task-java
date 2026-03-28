package com.kstrinadka.chat.client.ui.chat;

import java.util.List;

public final class FakeMessageFactory {

    private FakeMessageFactory() {
    }

    public static List<MessageVm> createFakeMessages() {
        return List.of(
                new MessageVm(
                        "Привет! Это тестовое входящее сообщение. Проверяем, как выглядит пузырек слева.",
                        MessageDirection.INCOMING,
                        "12:01",
                        MessageStatus.NONE
                ),
                new MessageVm(
                        "Привет. Это исходящее сообщение справа. Должно напоминать структуру Telegram-like чата.",
                        MessageDirection.OUTGOING,
                        "12:02",
                        MessageStatus.DELIVERED
                ),
                new MessageVm(
                        "Тут еще одно длинное сообщение, чтобы проверить перенос строк внутри пузыря. "
                                + "Важно, чтобы текст не вылезал за границы, а аккуратно переносился на новую строку.",
                        MessageDirection.INCOMING,
                        "12:03",
                        MessageStatus.NONE
                ),
                new MessageVm(
                        "Ок, вижу. Позже сюда добавим настоящую отправку через TCP и статусы ACK / ERROR.",
                        MessageDirection.OUTGOING,
                        "12:04",
                        MessageStatus.SENDING
                ),
                new MessageVm(
                        "Это пример сообщения с ошибкой доставки. Его статус должен быть выделен другим цветом.",
                        MessageDirection.OUTGOING,
                        "12:05",
                        MessageStatus.FAILED
                ),
                new MessageVm(
                        "Отлично. Пока это просто визуальный каркас без сети, но уже можно проверять layout и стили.",
                        MessageDirection.INCOMING,
                        "12:06",
                        MessageStatus.NONE
                )
        );
    }
}
