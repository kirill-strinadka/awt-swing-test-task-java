# Потоки обработки (кратко)

Ниже — логическая цепочка вызовов, а не дословный код.

## Жизненный цикл TCP-соединения

1. `ChatServer` принимает `Socket`, выставляет `SoTimeout` из `ServerConfig`, создаёт `ClientConnection` через `ClientConnectionFactory`.
2. Задача **`connection.run()`** выполняется в **виртуальном потоке** (пул `newVirtualThreadPerTaskExecutor`).
3. Создаются `BufferedReader` / `BufferedWriter` (UTF-8), `OutboundChannel`, `ConnectionContext` через `ConnectionContextFactory`.
4. Цикл: чтение строки (`readLine`) — одна непустая логическая строка = один JSON-запрос (разделитель `\n`).
5. На каждую непустую строку: проверка лимита длины сырой строки → **decode** (`ProtocolMessageCodec`) → **validate** (`ProtocolValidator`) → **dispatch** (`RequestDispatcher`).
6. Результат `dispatch` (если не `null`) — один `ServerResponse` для **этого** клиента → encode → запись в сокет под `writeLock`, завершение строки `\n`.
7. При завершении цикла или ошибке: `ConnectionCloseHandler.onConnectionClosed` (снятие сессии при необходимости), закрытие сокета, `ConnectionState.CLOSED`.

## AUTH (успех)

`SocketClientConnection` → codec → validator → `RequestDispatcher` → `AuthUseCase.handle`.

Внутри: `AuthenticationService` → при успехе `SessionFactory` + `SessionRegistry.register` → `ConnectionContext.markAuthenticated` → `AUTH_OK` (или `AUTH_ERROR`).

Ответ уходит в тот же сокет.

## SEND (успех с доставкой)

`RequestDispatcher` → `SendMessageUseCase.handle`.

1. Отправитель аутентифицирован (`ConnectionContext`).
2. Получатель существует в `UserRepository` (имя `to` после `strip()`).
3. Получатель онлайн: `SessionRegistry.findConnectionByUsername`.
4. `MessageService.prepareMessage` → `ChatMessage` или `DeliveryResult.Failure`.
5. Сборка `IncomingMessageResponse`.
6. `MessageDeliveryService.deliver(recipientContext, incoming)` → запись в сокет получателя через его `OutboundChannel` (там тоже используется синхронизация записи).
7. Успех → `ACK` отправителю; сбой доставки → `ERROR` с `DELIVERY_FAILED`.

Получатель **не** получает ответ через возврат из `dispatch`; только push через доставку.

## SEND (получатель offline или не найден)

В сокет отправителя уходит `ERROR` с соответствующим кодом (`RECIPIENT_OFFLINE`, `RECIPIENT_NOT_FOUND`, …).

## Протокольные и транспортные ошибки

- Пустая/пробельная строка, слишком длинная строка, битый JSON, ошибки валидации: в тот же сокет уходит строка `ERROR` с кодами вроде `EMPTY_REQUEST`, `REQUEST_TOO_LARGE`, `PROTOCOL_ERROR`; соединение **остаётся открытым**, цикл чтения продолжается.
- Необработанное исключение в обработчике: `INTERNAL_ERROR`, соединение снова остаётся открытым до следующей строки или обрыва.
- Исключения записи в сокет: соединение закрывается.

## Конкурентность

- Каждое соединение читает свой сокет **последовательно** в своём виртуальном потоке.
- В **один** сокет могут писать разные потоки (свой цикл и чужой `MessageDeliveryService`), поэтому запись в `SocketClientConnection` сериализуется (`writeLock`).
