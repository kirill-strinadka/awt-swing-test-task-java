# Chat Client

Desktop chat client on Java 21, Swing and FlatLaf.

## MVP scope

1. Login with username and password
2. Connect to TCP server
3. Send plain text messages
4. Receive plain text messages
5. No server-side history
6. No group chats
7. No attachments
8. No read status
9. No edit/delete
10. Telegram-like dark UI

## Architecture rules

1. UI does not know about `Socket`
2. All Swing UI updates must happen on EDT
3. JSON must not be built manually as strings
4. Incoming messages may arrive at any moment
5. Every outgoing message must have `requestId` and `clientMsgId`

## First technical slice

- Java 21
- Maven
- Swing
- FlatLaf
- Jackson
- SLF4J / Logback

## Package structure

- `app` — application bootstrap and startup
- `ui` — Swing windows, panels, dialogs
- `presentation` — presenters and view contracts
- `domain` — local client models and state
- `protocol` — protocol DTO and codec
- `net` — TCP communication layer
- `util` — shared utilities

## Run

From repository root:

```bash
mvn -pl client exec:java
```

From `client` directory:

```bash
mvn clean compile exec:java
```

## Current status

This step includes only:

- Maven project skeleton
- FlatLaf startup
- Login window
- Logging configuration
- Package responsibilities

Networking, protocol DTO, presenters and message UI will be added in next steps.

Protocol details: see `../common/docs/server-api.md`.
