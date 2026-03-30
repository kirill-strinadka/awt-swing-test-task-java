package com.kstrinadka.chat.client.ui.chat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public class ConversationListCellRenderer implements ListCellRenderer<ConversationListItemVm> {

    private static final Color SIDEBAR_BG = new Color(30, 40, 50);
    private static final Color SELECTED_BG = new Color(42, 58, 72);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 240);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 170);

    @Override
    public Component getListCellRendererComponent(
            JList<? extends ConversationListItemVm> list,
            ConversationListItemVm value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(true);
        root.setBackground(isSelected ? SELECTED_BG : SIDEBAR_BG);
        root.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel usernameLabel = new JLabel(value.username());
        usernameLabel.setForeground(TEXT_PRIMARY);
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD, 14f));

        JLabel timeLabel = new JLabel(value.timeText());
        timeLabel.setForeground(TEXT_SECONDARY);
        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.PLAIN, 11f));

        JPanel topLine = new JPanel(new BorderLayout());
        topLine.setOpaque(false);
        topLine.add(usernameLabel, BorderLayout.WEST);
        topLine.add(timeLabel, BorderLayout.EAST);

        JLabel lastMessageLabel = new JLabel(value.lastMessagePreview());
        lastMessageLabel.setForeground(TEXT_SECONDARY);
        lastMessageLabel.setFont(lastMessageLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(topLine);
        content.add(Box.createVerticalStrut(6));
        content.add(lastMessageLabel);

        root.add(content, BorderLayout.CENTER);
        return root;
    }
}

