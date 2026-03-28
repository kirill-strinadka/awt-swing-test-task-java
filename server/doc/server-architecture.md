# Архитектура чат-сервера: слои и ответственность

Базовый пакет: **`com.kstrinadka.chat.server`**.

## Идея слоёв

1. **Transport** — сокеты, цикл чтения строк, жизненный цикл соединения, отправка байтов. Не содержит бизнес-правил чата.
2. **Protocol** — DTO запросов/ответов, кодек JSON, проверка формата полей (длина, обязательность). Не знает, залогинен ли пользователь.
3. **Application** — сценарии (use cases), порты (`SessionRegistry`, `UserRepository`, сервисы). Собирает ответ для **текущего** соединения; push получателю — через `MessageDeliveryService`.
4. **Domain** — сущности и типы результатов (пользователь, сообщение, сессия, коды ошибок auth/delivery).
5. **Infrastructure** — реализации портов (in-memory репозитории, Jackson, дефолтные сервисы).
6. **Config / Bootstrap** — настройки и ручная сборка графа зависимостей без Spring.

## Пакеты и роли

### `bootstrap`

| Класс | Роль |
|-------|------|
| `ChatServerApplication` | `main`: старт приложения. |
| `ApplicationAssembler` | Composition root: создаёт `ChatServer` и зависимости. |

### `config`

| Тип | Роль |
|-----|------|
| `ServerConfig` | Хост, порт, таймауты, лимиты сообщения, политика одной сессии на пользователя. |
| `TestUsersConfig` | Предопределённые пользователи для стенда. |

### `domain`

| Тип | Роль |
|-----|------|
| `User`, `ChatMessage`, `SessionId`, `ClientSession` | Модель предметной области. |
| `AuthResult`, `AuthErrorCode` | Результат проверки учётных данных. |
| `DeliveryResult`, `DeliveryErrorCode` | Результат подготовки сообщения к отправке (`prepareMessage`), не запись в сокет. |

### `protocol`

| Тип | Роль |
|-----|------|
| `ClientRequest` / `AuthRequest`, `SendMessageRequest` | Входящие сообщения клиента (sealed). |
| `ServerResponse` / ответы `Auth*`, `Ack`, `Incoming`, `Error` | Исходящие сообщения сервера. |
| `ProtocolMessageCodec` | Строка ↔ DTO. |
| `ProtocolValidator` | Валидация структуры после decode. |
| `ProtocolException` | Ошибки протокола/формата. |

### `application`

| Тип | Роль |
|-----|------|
| `RequestDispatcher` | Маршрутизация `ClientRequest` → нужный use case; возвращает **один** `ServerResponse` для **этого** сокета. |
| `AuthUseCase` | Сценарий `AUTH`: проверка, регистрация в `SessionRegistry`, отметка контекста. |
| `SendMessageUseCase` | Сценарий `SEND`: проверки, `MessageService.prepareMessage`, доставка `INCOMING` через `MessageDeliveryService`, возврат `ACK` или `ERROR` отправителю. |
| `AuthenticationService` | Проверка логина/пароля. |
| `MessageService` | Подготовка доменного `ChatMessage` и правил на уровне сообщения (без записи в чужой сокет). |
| `MessageDeliveryService` | Push `IncomingMessageResponse` в **другой** `ConnectionContext` (должен быть thread-safe вместе с `OutboundChannel`). |
| `SessionRegistry` | Онлайн-пользователи: регистрация, поиск соединения по username, unregister. |
| `UserRepository` | Справочник известных пользователей. |
| `SessionFactory` | Создание `ClientSession` при успешном входе. |
| `PasswordVerifier`, `MessageIdGenerator` | Порты для паролей и id сообщений. |

### `transport`

| Тип | Роль |
|-----|------|
| `ChatServer` | `ServerSocket`, accept loop, виртуальные потоки на соединение (план). |
| `ClientConnection` / `SocketClientConnection` | Один клиент: read line → codec → validator → dispatcher → отправка ответа; cleanup. |
| `ClientConnectionFactory`, `ConnectionContextFactory` | Фабрики для тестов и сборки. |
| `ConnectionContext` | Состояние одного соединения (`ConnectionState`), username, ссылка на `ClientConnection`, **`OutboundChannel`**. |
| `ConnectionState` | `CONNECTED` / `AUTHENTICATED` / `CLOSED`. |
| `OutboundChannel` | Потокобезопасная запись `ServerResponse` в конкретный сокет. |

### `infrastructure`

Реализации: Jackson-кодек, валидатор, in-memory репозитории и реестр, дефолтные auth/message/delivery/dispatcher/session factory, SHA-256 verifier (или замена), UUID id generator.

## Зависимости между слоями (целевое правило)

- Transport зависит от protocol и application (dispatcher, codec).
- Application может ссылаться на `ConnectionContext` (реестр сессий по username → контекст).
- Domain не зависит от Jackson и сокетов.
- Infrastructure реализует интерфейсы application/protocol.

## Состояние реализации

На этапе каркаса часть методов намеренно бросает `UnsupportedOperationException` или не заполнена — см. исходники. Документ описывает **целевую** раскладку ответственности.
