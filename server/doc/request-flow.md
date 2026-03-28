# Потоки обработки (кратко)

Ниже — логическая цепочка вызовов, а не дословный код.

## Жизненный цикл TCP-соединения

1. `ChatServer` принимает `Socket`.
2. Создаются `ConnectionContext` (с `OutboundChannel`) и `ClientConnection` (например `SocketClientConnection`).
3. Запускается обработка (план: virtual thread): цикл чтения строк UTF-8, одна строка = один JSON-запрос.
4. На каждую строку: **decode** (`ProtocolMessageCodec`) → **validate** (`ProtocolValidator`) → **dispatch** (`RequestDispatcher`).
5. Результат `dispatch` — один `ServerResponse` для **этого** клиента → encode → запись через `OutboundChannel` / соединение.
6. При обрыве: если был залогинен — `SessionRegistry.unregister`, закрытие ресурсов, `ConnectionState.CLOSED`.

## AUTH (успех)

`SocketClientConnection` → codec → validator → `RequestDispatcher` → `AuthUseCase.handle`.

Внутри: `AuthenticationService` → при успехе `SessionFactory` + `SessionRegistry.register` → `ConnectionContext.markAuthenticated` → ответ `AUTH_OK` (или `AUTH_ERROR` при ошибке).

Ответ уходит в тот же сокет.

## SEND (успех с доставкой)

`RequestDispatcher` → `SendMessageUseCase.handle`.

Последовательность (целевая):

1. Проверка, что отправитель аутентифицирован (`ConnectionContext`).
2. Существование получателя (`UserRepository`).
3. Поиск онлайн-соединения получателя (`SessionRegistry`).
4. `MessageService.prepareMessage` → доменный `ChatMessage` или `DeliveryResult.Failure`.
5. Сборка `IncomingMessageResponse` для получателя.
6. `MessageDeliveryService.deliver(recipientContext, incoming)` — внутри вызов `recipientContext.outboundChannel().send(...)`.
7. При успешной доставке — `ACK` отправителю; при сбое — `ERROR` (например offline / ошибка записи).

Важно: **получатель** не получает ответ через возвращаемое значение `dispatch`; он получает push через `MessageDeliveryService`.

## SEND (получатель offline или не найден)

Use case не вызывает успешную доставку; в сокет отправителя уходит `ERROR` с соответствующим кодом.

## Протокольные ошибки

Зависит от политики MVP: часто `ERROR` в текущий сокет; при фатально битом JSON возможно закрытие соединения после ответа или сразу.

## Конкурентность

- Один поток (или virtual thread) **читает** свой сокет последовательно.
- В **один** сокет могут **писать** разные потоки (свой handler и чужой `MessageDeliveryService`), поэтому `OutboundChannel.send` должен быть синхронизирован.
