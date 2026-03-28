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
    private static final Color INCOMING_COLOR = new Color(38, 50, 56);
    private static final Color OUTGOING_COLOR = new Color(35, 92, 76);
    private static final Color META_COLOR = new Color(170, 180, 190);

    public MessageBubblePanel(
            String text,
            String timeText,
            String statusText,
            MessageDirection direction
    ) {
        setOpaque(false);
        setLayout(new BorderLayout());

        BubbleContentPanel bubbleContent = new BubbleContentPanel(direction);
        bubbleContent.setLayout(new BoxLayout(bubbleContent, BoxLayout.Y_AXIS));
        bubbleContent.setBorder(BorderFactory.createEmptyBorder(10, 12, 8, 12));

        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setFocusable(false);
        textArea.setBorder(null);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN, 14f));
        textArea.setAlignmentX(LEFT_ALIGNMENT);
        textArea.setMaximumSize(new Dimension(MAX_BUBBLE_WIDTH, Integer.MAX_VALUE));

        JLabel metaLabel = new JLabel(buildMetaText(timeText, statusText));
        metaLabel.setForeground(META_COLOR);
        metaLabel.setFont(metaLabel.getFont().deriveFont(Font.PLAIN, 11f));
        metaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        metaLabel.setAlignmentX(RIGHT_ALIGNMENT);
        metaLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        bubbleContent.add(textArea);
        bubbleContent.add(metaLabel);

        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(false);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        if (direction == MessageDirection.INCOMING) {
            rowPanel.add(bubbleContent, BorderLayout.WEST);
        } else {
            rowPanel.add(bubbleContent, BorderLayout.EAST);
        }

        add(rowPanel, BorderLayout.CENTER);
    }

    private String buildMetaText(String timeText, String statusText) {
        if (statusText == null || statusText.isBlank()) {
            return timeText;
        }
        return timeText + "   " + statusText;
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
                g2.setColor(direction == MessageDirection.INCOMING ? INCOMING_COLOR : OUTGOING_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            } finally {
                g2.dispose();
            }

            super.paintComponent(graphics);
        }
    }
}
