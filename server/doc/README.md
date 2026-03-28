# Документация модуля `server`

Тексты в этой папке описывают **чат-сервер** и место репозитория в целом.

| Файл | Содержание |
|------|------------|
| [server-module-tree.md](server-module-tree.md) | Древовидная структура каталогов модуля `server` |
| [project-overview.md](project-overview.md) | Весь Maven-проект: модули, стек, что уже есть и что планируется |
| [server-architecture.md](server-architecture.md) | Слои сервера, пакеты и ответственность кода |
| [request-flow.md](request-flow.md) | Упрощённые сценарии: от сокета до ответа клиенту |

Точка входа сервера в коде: `com.kstrinadka.chat.server.bootstrap.ChatServerApplication`.
