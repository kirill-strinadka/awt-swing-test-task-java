package com.kstrinadka.chat.client.ui.login;

import com.kstrinadka.chat.client.ui.chat.MainFrame;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;

public class LoginFrame extends JFrame {

    private final JTextField hostField;
    private final JFormattedTextField portField;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JLabel statusLabel;

    public LoginFrame() {
        super("Chat Client — Login");

        hostField = new JTextField("127.0.0.1");
        portField = createPortField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        statusLabel = new JLabel("Enter credentials to continue", SwingConstants.CENTER);

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

        JLabel titleLabel = new JLabel("Telegram-like Chat Client", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));

        JLabel subtitleLabel = new JLabel("Stage 2 — UI skeleton without network", SwingConstants.CENTER);
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 13f));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 24, 8));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));

        formPanel.add(createFieldBlock("Host", hostField));
        formPanel.add(Box.createVerticalStrut(14));

        formPanel.add(createFieldBlock("Port", portField));
        formPanel.add(Box.createVerticalStrut(14));

        formPanel.add(createFieldBlock("Username", usernameField));
        formPanel.add(Box.createVerticalStrut(14));

        formPanel.add(createFieldBlock("Password", passwordField));
        formPanel.add(Box.createVerticalStrut(22));

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.setPreferredSize(new Dimension(160, 42));

        formPanel.add(loginButton);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(18, 8, 8, 8));
        footerPanel.add(statusLabel, BorderLayout.CENTER);

        root.add(headerPanel, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);
        root.add(footerPanel, BorderLayout.SOUTH);
    }

    private void bindActions() {
        loginButton.addActionListener(e -> {
            String username = getUsername();
            if (username.isBlank()) {
                statusLabel.setText("Username is required");
                return;
            }

            statusLabel.setText("Opening main chat window...");
            MainFrame mainFrame = new MainFrame(username);
            mainFrame.setVisible(true);
            dispose();
        });

        getRootPane().setDefaultButton(loginButton);
    }

    private JPanel createFieldBlock(String labelText, javax.swing.JComponent field) {
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

    public String getHost() {
        return hostField.getText().trim();
    }

    public int getPort() {
        Object value = portField.getValue();
        if (value instanceof Number number) {
            return number.intValue();
        }
        return 9000;
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    public void setLoginEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
        hostField.setEnabled(enabled);
        portField.setEnabled(enabled);
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
    }
}
