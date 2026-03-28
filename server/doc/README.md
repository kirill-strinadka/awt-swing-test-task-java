# Документация модуля `server`

Тексты в этой папке описывают **TCP чат-сервер** (JSON по строкам) и контекст всего репозитория.

## Онбординг для ИИ / нового разработчика

Рекомендуемый порядок чтения, чтобы быстро восстановить картину:

1. [project-overview.md](project-overview.md) — много-модульный Maven-проект, стек, сборка, что где лежит.
2. [server-api.md](server-api.md) — **контракт по сети**: формат строк, поля JSON, коды ошибок, примеры (нужен любому клиенту или интеграции).
3. [server-architecture.md](server-architecture.md) — слои, пакеты, кто за что отвечает в коде.
4. [request-flow.md](request-flow.md) — как запрос проходит от сокета до ответа и push `INCOMING`.
5. [server-module-tree.md](server-module-tree.md) — актуальный список файлов исходников и тестов.

Точка входа процесса: `com.kstrinadka.chat.server.bootstrap.ChatServerApplication#main`.

## Оглавление

| Файл | Содержание |
|------|------------|
| [server-module-tree.md](server-module-tree.md) | Структура каталогов модуля `server` (main + test) |
| [project-overview.md](project-overview.md) | Весь Maven-проект: модули, стек, сборка |
| [server-api.md](server-api.md) | Спецификация протокола (line-delimited JSON) |
| [server-architecture.md](server-architecture.md) | Слои, пакеты, ответственность классов |
| [request-flow.md](request-flow.md) | Сценарии обработки от сокета до ответа |
