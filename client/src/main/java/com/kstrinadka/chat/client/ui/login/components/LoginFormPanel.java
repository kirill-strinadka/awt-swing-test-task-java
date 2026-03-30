package com.kstrinadka.chat.client.ui.login.components;

import javax.swing.*;
import java.awt.*;

public final class LoginFormPanel extends JPanel {

    public LoginFormPanel(
            JTextField hostField,
            JFormattedTextField portField,
            JTextField usernameField,
            JPasswordField passwordField,
            JButton loginButton) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));

        add(new LabeledInputRow("Host", hostField));
        add(Box.createVerticalStrut(14));

        add(new LabeledInputRow("Port", portField));
        add(Box.createVerticalStrut(14));

        add(new LabeledInputRow("Username", usernameField));
        add(Box.createVerticalStrut(14));

        add(new LabeledInputRow("Password", passwordField));
        add(Box.createVerticalStrut(22));

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.setPreferredSize(new Dimension(160, 42));

        add(loginButton);
    }
}
