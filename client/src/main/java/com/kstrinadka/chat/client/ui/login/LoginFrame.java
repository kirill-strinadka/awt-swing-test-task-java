package com.kstrinadka.chat.client.ui.login;

import com.kstrinadka.chat.client.presentation.login.LoginPresenter;
import com.kstrinadka.chat.client.presentation.login.LoginSuccessHandler;
import com.kstrinadka.chat.client.presentation.login.LoginView;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;

public class LoginFrame extends JFrame implements LoginView {

    private final JTextField hostField;
    private final JFormattedTextField portField;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JLabel statusLabel;

    private final LoginPresenter presenter;

    public LoginFrame(LoginSuccessHandler successHandler) {
        super("Chat Client — Login");

        hostField = new JTextField("127.0.0.1");
        portField = createPortField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        statusLabel = new JLabel("Enter credentials to continue", SwingConstants.CENTER);

        presenter = new LoginPresenter(this, successHandler);

        initFrame();
        initUi();
        bindActions();
    }

    private void initFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 460));
        setSize(460, 520);
        setLocationRelativeTo(null);
    }

    private void initUi() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setContentPane(root);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));
        footerPanel.add(statusLabel, BorderLayout.CENTER);

        root.add(createHeaderPanel(), BorderLayout.NORTH);
        root.add(createFormPanel(), BorderLayout.CENTER);
        root.add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JLabel titleLabel = new JLabel("Telegram-like Chat Client", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));

        JLabel subtitleLabel = new JLabel("Тестовое задание для ООО Лог Кэпитал", SwingConstants.CENTER);
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 13f));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 24, 8));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 24, 8));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));

        formPanel.add(createInputFieldBlock("Host", hostField));
        formPanel.add(Box.createVerticalStrut(14));

        formPanel.add(createInputFieldBlock("Port", portField));
        formPanel.add(Box.createVerticalStrut(14));

        formPanel.add(createInputFieldBlock("Username", usernameField));
        formPanel.add(Box.createVerticalStrut(14));

        formPanel.add(createInputFieldBlock("Password", passwordField));
        formPanel.add(Box.createVerticalStrut(22));

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.setPreferredSize(new Dimension(160, 42));

        formPanel.add(loginButton);
        return formPanel;
    }

    private void bindActions() {
        loginButton.addActionListener(e -> presenter.onLoginClicked());
        getRootPane().setDefaultButton(loginButton);
    }

    private JPanel createInputFieldBlock(String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(320, 40));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(field);

        return panel;
    }

    private JFormattedTextField createPortField() {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        numberFormat.setGroupingUsed(false);

        JFormattedTextField field = new JFormattedTextField(numberFormat);
        field.setValue(9000);
        return field;
    }

    @Override
    public String getHost() {
        return hostField.getText().trim();
    }

    @Override
    public int getPort() {
        Object value = portField.getValue();
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 9000;
    }

    @Override
    public String getUsername() {
        return usernameField.getText().trim();
    }

    @Override
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    @Override
    public void setLoading(boolean loading) {
        loginButton.setEnabled(!loading);
        hostField.setEnabled(!loading);
        portField.setEnabled(!loading);
        usernameField.setEnabled(!loading);
        passwordField.setEnabled(!loading);

        if (loading) {
            statusLabel.setText("Connecting and authorizing...");
        } else if (statusLabel.getText() == null || statusLabel.getText().isBlank()) {
            statusLabel.setText(" ");
        }
    }

    @Override
    public void showError(String message) {
        statusLabel.setText(message == null || message.isBlank() ? "Unknown error" : message);
    }

    @Override
    public void close() {
        dispose();
    }
}
