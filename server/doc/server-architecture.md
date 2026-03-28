# Архитектура чат-сервера: слои и ответственность

Базовый пакет: **`com.kstrinadka.chat.server`**.

## Идея слоёв

1. **Transport** — сокеты, accept-loop, **виртуальный поток на каждое соединение**, построчное чтение UTF-8, вызов codec/validator/dispatcher, запись ответов. Без правил предметной области чата.
2. **Protocol** — DTO запросов/ответов, кодек JSON, проверка формата полей. Не знает, залогинен ли пользователь.
3. **Application** — сценарии (use cases), порты (`SessionRegistry`, `UserRepository`, сервисы). Возвращает **один** `ServerResponse` для **текущего** соединения; push получателю — через `MessageDeliveryService`.
4. **Domain** — сущности и типы результатов (пользователь, сообщение, сессия, коды ошибок auth/delivery).
5. **Infrastructure** — реализации портов (in-memory репозитории, Jackson, дефолтные сервисы, фабрика сокет-соединений).
6. **Config / Bootstrap** — настройки и ручная сборка графа зависимостей без Spring.

## Пакеты и роли

### `bootstrap`

| Класс | Роль |
|-------|------|
| `ChatServerApplication` | `main`: `ServerConfig` + `TestUsersConfig`, `ApplicationAssembler.assemble(...)`, `ChatServer.start()`, shutdown-hook. |
| `ApplicationAssembler` | Composition root: репозитории, use cases, `RequestDispatcher`, `ObjectMapper` + `JacksonProtocolMessageCodec`, `DefaultProtocolValidator`, `DefaultClientConnectionFactory`, `ChatServer`. |
| `ServerApplicationContext` | Record: экспонированные для тестов/расширений части ядра плюс `ChatServer`. |

### `config`

| Тип | Роль |
|-----|------|
| `ServerConfig` | `host`, `port`, `socketReadTimeoutMillis`, `maxMessageLength`, `singleSessionPerUser`. Последнее поле **зарезервировано**: на текущей версии логика входа опирается на `SessionRegistry.register` (`putIfAbsent` → один активный логин = одна сессия), флаг из конфига нигде не читается. |
| `TestUsersConfig` | Предопределённые пользователи: в конфиге пароли **в открытом виде**, при сборке в `User` кладётся SHA-256 hex (см. `ApplicationAssembler.buildUserMap`). |

### `domain`

| Тип | Роль |
|-----|------|
| `User`, `ChatMessage`, `SessionId`, `ClientSession` | Модель предметной области. |
| `AuthResult`, `AuthErrorCode` | Результат проверки учётных данных. |
| `DeliveryResult`, `DeliveryErrorCode` | Результат `prepareMessage`, не запись в сокет. |

### `protocol`

| Тип | Роль |
|-----|------|
| `ClientRequest` / `AuthRequest`, `SendMessageRequest` | Входящие сообщения клиента (sealed). |
| `ServerResponse` / `AuthOkResponse`, `AuthErrorResponse`, `AckResponse`, `IncomingMessageResponse`, `ErrorResponse` | Исходящие сообщения сервера. |
| `ProtocolMessageCodec` | Строка ↔ DTO. |
| `ProtocolTypes` | Константы поля `type` в JSON. |
| `ProtocolValidator` | Валидация структуры после decode. |
| `ProtocolException` | Ошибки протокола/формата. |

### `application`

| Тип | Роль |
|-----|------|
| `RequestDispatcher` | Маршрутизация `ClientRequest` → use case; один `ServerResponse` на запрос для **этого** сокета. |
| `AuthUseCase` | `AUTH`: `AuthenticationService` → `SessionRegistry.register` → `ConnectionContext.markAuthenticated` → `AUTH_OK` / `AUTH_ERROR`. |
| `SendMessageUseCase` | `SEND`: проверка сессии, `UserRepository`, онлайн получатель, `MessageService.prepareMessage`, `MessageDeliveryService.deliver` → `ACK` / `ERROR` отправителю. |
| `AuthenticationService`, `MessageService`, `MessageDeliveryService` | Порты: вход, подготовка сообщения, доставка в чужой `OutboundChannel`. |
| `SessionRegistry`, `UserRepository`, `SessionFactory` | Онлайн-сессии, справочник пользователей, создание `ClientSession`. |
| `PasswordVerifier`, `MessageIdGenerator` | Пароли и id сообщений. |

### `transport`

| Тип | Роль |
|-----|------|
| `ChatServer` | `ServerSocket`, цикл `accept`, на каждый `Socket` — задача на **виртуальном потоке** (`connection::run`). |
| `SocketClientConnection` | Цикл: `readLine` → лимит длины сырой строки → decode → validate → dispatch → encode → запись; синхронизация записи в сокет (`writeLock`); при закрытии — `ConnectionCloseHandler`. |
| `ClientConnection` | Интерфейс соединения (`run`, `send`, `close`, `isOpen`). |
| `ClientConnectionFactory` | Создание `ClientConnection` из `Socket` (реализация в infrastructure). |
| `ConnectionContextFactory` / `DefaultConnectionContextFactory` | Создание `ConnectionContext` для сокета. |
| `ConnectionContext` | Состояние соединения, имя пользователя после входа, `OutboundChannel`. |
| `ConnectionState` | `CONNECTED` / `AUTHENTICATED` / `CLOSED`. |
| `OutboundChannel` | Отправка `ServerResponse` в конкретный сокет (реализация — метод записи соединения). |
| `ConnectionCloseHandler` | Callback при завершении соединения (например снятие с регистрации в `SessionRegistry`). |

### `infrastructure`

| Тип | Роль |
|-----|------|
| `JacksonProtocolMessageCodec` | Реализация `ProtocolMessageCodec`. |
| `DefaultProtocolValidator` | Реализация `ProtocolValidator`. |
| `InMemoryUserRepository`, `InMemorySessionRegistry` | In-memory хранилища. |
| `DefaultAuthenticationService`, `DefaultMessageService`, `DefaultMessageDeliveryService`, `DefaultRequestDispatcher`, `DefaultSessionFactory` | Дефолтные сценарии и разбор запросов. |
| `Sha256PasswordVerifier`, `UuidMessageIdGenerator` | Хеш пароля и id сообщений. |
| `DefaultClientConnectionFactory` | Собирает `SocketClientConnection` с codec, validator, dispatcher, фабрикой контекста, close-handler, лимитом строки. |
| `SessionUnregistrationCloseHandler` | При закрытии сокета снимает пользователя из `SessionRegistry`, если был аутентифицирован. |

## Зависимости между слоями

- Transport зависит от protocol и application (dispatcher, codec через фабрику).
- Application использует `ConnectionContext` (сессии, отправитель/получатель).
- Domain не зависит от Jackson и сокетов.
- Infrastructure реализует интерфейсы application/protocol и подключает transport-фабрики.

## Состояние реализации (end-to-end)

Сборка `ApplicationAssembler.assemble(ServerConfig, TestUsersConfig)` возвращает `ServerApplicationContext` с готовым `ChatServer`. На каждое входящее соединение создаётся `SocketClientConnection` с `JacksonProtocolMessageCodec`, `DefaultProtocolValidator` и `DefaultRequestDispatcher`. Запуск: `ChatServerApplication.main`.
