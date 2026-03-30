## Chat Client

Desktop chat client on Java 21, Swing and FlatLaf.

This is an **MVP desktop chat client** that talks to the server over a simple **TCP protocol with JSON Lines** (one JSON object per line).  
The UI is built with **Java Swing + FlatLaf** and styled as a **Telegram‑like dark interface**.

## MVP scope

1. Login with username and password
2. Connect to TCP chat server over TCP + JSON Lines
3. Send plain text messages
4. Receive plain text messages (server push)
5. Telegram-like dark UI
6. No server-side history
7. No group chats
8. No attachments
9. No read status / read receipts
10. No edit/delete

## Architecture

- **Passive View + Presenter**
  - Swing windows (`LoginFrame`, `MainFrame`) implement small `*View` interfaces (`LoginView`, `ChatView`) and do not know about networking primitives like `Socket`.
  - Presenters (`LoginPresenter`, `ChatPresenter`) own the client-side state, orchestrate networking and domain logic, and push ready-to-render view models (`MessageVm`, `ConversationListItemVm`) back into the views.

- **Threading model**
  - All Swing UI updates happen on the **EDT** using `SwingUtilities.invokeLater(...)`.
  - `TcpChatClient` runs a dedicated **reader thread** to process lines from the TCP socket.
  - Login flow runs on a background `ExecutorService` in `LoginPresenter` so that UI stays responsive while connecting and authenticating.

- **Request correlation (`requestId`)**
  - Every outgoing client request (`AuthRequest`, `SendRequest`) has a `requestId` (and for messages also a `clientMsgId`).
  - `TcpChatClient.sendRequestAwaitResponse`:
    - encodes and sends the request,
    - puts a `CompletableFuture<ServerResponse>` into an internal `pendingRequests` map keyed by `requestId`,
    - when a response with the same `requestId` arrives, the private `dispatch(...)` method completes the matching future and removes it from the map.

- **Push incoming events**
  - Server push messages of type `INCOMING` come without a `requestId` and are treated as asynchronous events.
  - `TcpChatClient` delivers them into `TcpChatClientListener.onIncoming(...)`.
  - In runtime, `LoginToRuntimeBridgeListener` forwards them into `ClientSession`, which forwards to `ChatPresenter.onIncoming(...)`.
  - `ChatPresenter` appends the incoming message to the local `ConversationStore` and refreshes sidebar + chat UI on the EDT.

## Limitations (by design)

This client is a consciously small MVP:

- **No server-side history** — messages live only in the in-memory `ConversationStore` for the lifetime of the client session.
- **No presence** — no online/offline indicators, no “typing” status.
- **No group chats** — only 1:1 conversations are supported.
- **No read receipts / read status** — only simple delivery state (SENDING / DELIVERED / FAILED) on the client side.
- **No attachments, editing or deleting messages** — only plain text messages.

## Layout and UI choices

The chat interface is built with plain `JPanel` + `BoxLayout` and simple custom painting instead of a more complex, virtualized component.

- **Why this is a good fit for this project**
  - Much faster to implement for a test task / MVP.
  - Easier to iterate on visuals for the Telegram-like dark theme (colors, spacing, message bubbles) without fighting a heavy component.
  - Keeps the code readable: each message bubble is just a small `JPanel` with its own renderer.

- **Trade-offs**
  - Does not scale perfectly to very large histories: each message bubble is a Swing component, so a very long chat will create a lot of components.
  - For a production-scale client with thousands of messages, a virtualized list (custom `ListModel`/`ListCellRenderer` or another toolkit) would be preferable.

These trade-offs are acceptable for an MVP and are documented here intentionally.

## First technical slice

- Java 21
- Maven
- Swing
- FlatLaf
- Jackson
- SLF4J / Logback
- JUnit 5

## Package structure

- `app` — application bootstrap and startup (main method, FlatLaf setup)
- `ui` — Swing windows, panels, dialogs
- `presentation` — presenters and view contracts (`*View` interfaces)
- `domain` — local client models and state (`Conversation`, `Message`, `ConversationStore`)
- `protocol` — protocol DTO and Jackson-based codec
- `net` — TCP communication layer (`TcpChatClient` and listener bridge)
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

The client currently includes:

- FlatLaf dark startup and base theme
- Login window with TCP AUTH against the chat server
- Main chat window with Telegram-like dark layout (sidebar + chat area + input)
- TCP networking client with JSON Lines protocol
- Protocol DTOs and Jackson codec
- Basic presenters (`LoginPresenter`, `ChatPresenter`) and local conversation store
- Minimal tests for codec, request correlation and presenter behavior

Protocol details: see `../common/docs/server-api.md`.
