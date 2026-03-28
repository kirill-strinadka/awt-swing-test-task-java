# Обзор всего приложения (репозиторий)

Корневой артефакт Maven: **`com.kstrinadka:awt-swing-test-task-java:1.0-SNAPSHOT`**, упаковка `pom`, Java **21**.

## Модули

| Модуль | Артефакт | Назначение |
|--------|-----------|------------|
| **server** | `server` | Рабочий TCP чат-сервер: **line-delimited JSON**, виртуальные потоки на соединение (`Executors.newVirtualThreadPerTaskExecutor()`), in-memory пользователи и сессии. Документация в `server/doc/`. |
| **client** | `client` | Заготовка под клиент (точка входа `com.kstrinadka.Main`). План: UI (например Swing) и подключение к серверу по протоколу из [server-api.md](server-api.md). |
| **common** | `common` | Заготовка общего кода (`com.kstrinadka.Main`). План: вынести общие DTO протокола или утилиты, если появятся. |

Между модулями **нет** Maven-зависимостей: `client` и `common` не подключают `server` как библиотеку; каждый модуль собирается отдельно.

## Продукт (MVP)

- Сервер принимает TCP, аутентификация логин/пароль, команды `AUTH` и `SEND`, доставка сообщений **только онлайн-пользователям**.
- Нет истории сообщений, БД, регистрации и групповых чатов.

## Технологии (модуль `server`)

- **Jackson** (`jackson-databind`, `jackson-datatype-jsr310`) — JSON протокола, даты в ISO-8601 строках.
- **SLF4J + Logback** — логирование.
- **JUnit 5, AssertJ, Mockito** — тесты (есть unit/smoke под `server/src/test/java`).

Spring Boot не используется; зависимости собираются вручную в `ApplicationAssembler`.

## Сборка и тесты

Из корня репозитория:

```text
mvn compile
```

Только сервер:

```text
mvn -pl server compile
```

Тесты модуля `server`:

```text
mvn -pl server test
```

## Запуск сервера

Класс с `main`: `com.kstrinadka.chat.server.bootstrap.ChatServerApplication` (модуль `server`). Параметры по умолчанию (хост, порт, пользователи) заданы в коде `main`; подробная таблица — в [server-api.md](server-api.md), раздел «Параметры сервера по умолчанию».

## Где искать код

- Сервер: `server/src/main/java/com/kstrinadka/chat/server/`.
- Клиент и common: `client/src/main/java/`, `common/src/main/java/` — минимальные заглушки.

Слои и пакеты сервера: [server-architecture.md](server-architecture.md). Контракт сокета: [server-api.md](server-api.md).
