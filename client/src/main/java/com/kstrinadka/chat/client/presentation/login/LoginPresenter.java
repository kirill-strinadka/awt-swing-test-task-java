package com.kstrinadka.chat.client.presentation.login;

import com.kstrinadka.chat.client.net.TcpChatClient;
import com.kstrinadka.chat.client.net.TcpChatClientListener;
import com.kstrinadka.chat.client.protocol.AuthErrorResponse;
import com.kstrinadka.chat.client.protocol.AuthOkResponse;
import com.kstrinadka.chat.client.protocol.AuthRequest;
import com.kstrinadka.chat.client.protocol.ErrorResponse;
import com.kstrinadka.chat.client.protocol.IncomingResponse;
import com.kstrinadka.chat.client.protocol.ServerResponse;

import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginPresenter {

    private final LoginView view;
    private final LoginSuccessHandler successHandler;
    private final ExecutorService loginExecutor;

    public LoginPresenter(LoginView view, LoginSuccessHandler successHandler) {
        this.view = Objects.requireNonNull(view, "view must not be null");
        this.successHandler = Objects.requireNonNull(successHandler, "successHandler must not be null");
        this.loginExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "login-presenter");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void onLoginClicked() {
        String host = view.getHost();
        int port = view.getPort();
        String username = view.getUsername();
        char[] passwordChars = view.getPassword();

        if (host == null || host.isBlank()) {
            view.showError("Host is required");
            return;
        }

        if (port <= 0 || port > 65535) {
            view.showError("Port must be between 1 and 65535");
            return;
        }

        if (username == null || username.isBlank()) {
            view.showError("Username is required");
            return;
        }

        if (passwordChars == null || passwordChars.length == 0) {
            view.showError("Password is required");
            return;
        }

        view.setLoading(true);

        loginExecutor.submit(() -> {
            String password = new String(passwordChars);
            Arrays.fill(passwordChars, '\0');

            // Kept open after AUTH_OK until ClientSession wires the same client into chat (next step).
            @SuppressWarnings("resource")
            TcpChatClient client = new TcpChatClient(new LoginClientListener());

            try {
                client.connect(host.trim(), port);

                AuthRequest authRequest = AuthRequest.of(
                        UUID.randomUUID().toString(),
                        username.trim(),
                        password
                );

                ServerResponse response = client.sendRequestAwaitResponse(authRequest).join();

                if (!(response instanceof AuthOkResponse)) {
                    client.disconnect();
                }

                SwingUtilities.invokeLater(() -> handleLoginResponse(response, username.trim()));
            } catch (Exception ex) {
                client.disconnect();
                Throwable report = unwrapCompletion(ex);
                SwingUtilities.invokeLater(() -> {
                    view.setLoading(false);
                    view.showError(mapConnectionError(report));
                });
            }
        });
    }

    private static Throwable unwrapCompletion(Throwable ex) {
        if (ex instanceof CompletionException && ex.getCause() != null) {
            return ex.getCause();
        }
        return ex;
    }

    private void handleLoginResponse(ServerResponse response, String username) {
        if (response instanceof AuthOkResponse) {
            view.setLoading(false);
            view.close();
            successHandler.onLoginSuccess(username);
            return;
        }

        if (response instanceof AuthErrorResponse authErrorResponse) {
            view.setLoading(false);
            view.showError(mapAuthError(authErrorResponse.errorCode()));
            return;
        }

        if (response instanceof ErrorResponse errorResponse) {
            view.setLoading(false);
            view.showError(mapGenericServerError(errorResponse.errorCode(), errorResponse.message()));
            return;
        }

        view.setLoading(false);
        view.showError("Unexpected server response during login");
    }

    private String mapAuthError(String errorCode) {
        return switch (errorCode) {
            case "INVALID_CREDENTIALS" -> "Неверный логин или пароль";
            case "USER_ALREADY_LOGGED_IN" -> "Этот пользователь уже вошел с другого клиента";
            case "ALREADY_AUTHENTICATED" -> "Соединение уже авторизовано";
            default -> "Ошибка авторизации: " + errorCode;
        };
    }

    private String mapGenericServerError(String errorCode, String message) {
        return switch (errorCode) {
            case "PROTOCOL_ERROR" -> "Ошибка протокола: " + message;
            case "INTERNAL_ERROR" -> "Внутренняя ошибка сервера";
            default -> "Ошибка сервера: " + message;
        };
    }

    private String mapConnectionError(Throwable ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return "Не удалось подключиться к серверу";
        }
        return "Не удалось подключиться: " + message;
    }

    private static final class LoginClientListener implements TcpChatClientListener {

        @Override
        public void onIncoming(IncomingResponse incomingResponse) {
            // During login, chat pushes are not expected.
        }

        @Override
        public void onDisconnected(Throwable cause) {
            // Login flow failures surface via the CompletableFuture or connect errors.
        }

        @Override
        public void onProtocolError(String rawLine, Throwable cause) {
            // No separate UI handling at this stage.
        }
    }
}
