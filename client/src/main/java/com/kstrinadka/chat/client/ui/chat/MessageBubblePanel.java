package com.kstrinadka.chat.client.ui.chat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class MessageBubblePanel extends JPanel {

    private static final int MAX_BUBBLE_WIDTH = 440;

    private static final Color INCOMING_BUBBLE_COLOR = new Color(38, 50, 56);
    private static final Color OUTGOING_BUBBLE_COLOR = new Color(35, 92, 76);

    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color META_COLOR = new Color(170, 180, 190);
    private static final Color FAILED_COLOR = new Color(255, 120, 120);

    public MessageBubblePanel(MessageVm messageVm) {
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel messageRowPanel = new JPanel(new BorderLayout());
        messageRowPanel.setOpaque(false);
        messageRowPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        BubbleContentPanel bubbleContentPanel = createBubbleContentPanel(messageVm);

        if (messageVm.isIncoming()) {
            messageRowPanel.add(bubbleContentPanel, BorderLayout.WEST);
        } else {
            messageRowPanel.add(bubbleContentPanel, BorderLayout.EAST);
        }

        add(messageRowPanel, BorderLayout.CENTER);
    }

    private BubbleContentPanel createBubbleContentPanel(MessageVm messageVm) {
        BubbleContentPanel bubblePanel = new BubbleContentPanel(messageVm.direction());
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.Y_AXIS));
        bubblePanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 8, 12));

        JTextArea textArea = new JTextArea(messageVm.text());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setFocusable(false);
        textArea.setBorder(null);
        textArea.setForeground(TEXT_COLOR);
        textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN, 14f));
        textArea.setAlignmentX(LEFT_ALIGNMENT);
        textArea.setMaximumSize(new Dimension(MAX_BUBBLE_WIDTH, Integer.MAX_VALUE));

        JLabel metaLabel = new JLabel(buildMetaText(messageVm), SwingConstants.RIGHT);
        metaLabel.setFont(metaLabel.getFont().deriveFont(Font.PLAIN, 11f));
        metaLabel.setAlignmentX(RIGHT_ALIGNMENT);
        metaLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        metaLabel.setForeground(resolveMetaColor(messageVm.status()));

        bubblePanel.add(textArea);
        bubblePanel.add(metaLabel);

        return bubblePanel;
    }

    private String buildMetaText(MessageVm messageVm) {
        String statusText = switch (messageVm.status()) {
            case SENDING -> "Sending...";
            case DELIVERED -> "Delivered";
            case FAILED -> "Failed";
            case NONE -> "";
        };

        if (statusText.isBlank()) {
            return messageVm.timeText();
        }

        return messageVm.timeText() + "   " + statusText;
    }

    private Color resolveMetaColor(MessageStatus status) {
        if (status == MessageStatus.FAILED) {
            return FAILED_COLOR;
        }
        return META_COLOR;
    }

    private static final class BubbleContentPanel extends JPanel {

        private final MessageDirection direction;

        private BubbleContentPanel(MessageDirection direction) {
            this.direction = direction;
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension preferredSize = super.getPreferredSize();
            int width = Math.min(preferredSize.width, MAX_BUBBLE_WIDTH);
            return new Dimension(width, preferredSize.height);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(direction == MessageDirection.INCOMING
                        ? INCOMING_BUBBLE_COLOR
                        : OUTGOING_BUBBLE_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            } finally {
                g2.dispose();
            }

            super.paintComponent(graphics);
        }
    }
}
