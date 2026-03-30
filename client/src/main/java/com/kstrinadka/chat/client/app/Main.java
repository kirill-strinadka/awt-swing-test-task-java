package com.kstrinadka.chat.client.app;

import com.formdev.flatlaf.FlatDarkLaf;
import com.kstrinadka.chat.client.ui.chat.MainFrame;
import com.kstrinadka.chat.client.ui.login.LoginFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        configureLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            log.info("Starting chat client UI");

            LoginFrame loginFrame = new LoginFrame(username -> {
                MainFrame mainFrame = new MainFrame(username);
                mainFrame.setVisible(true);
            });

            loginFrame.setVisible(true);
        });
    }

    private static void configureLookAndFeel() {
        try {
            FlatDarkLaf.setup();

            UIManager.put("defaultFont", UIManager.getFont("Label.font"));
            UIManager.put("Button.arc", 16);
            UIManager.put("Component.arc", 14);
            UIManager.put("TextComponent.arc", 14);

            log.info("FlatLaf dark theme configured");
        } catch (Exception ex) {
            log.error("Failed to initialize FlatLaf", ex);
            throw new IllegalStateException("Cannot initialize UI look and feel", ex);
        }
    }
}
