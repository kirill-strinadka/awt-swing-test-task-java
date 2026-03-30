package com.kstrinadka.chat.client.ui.chat;

import com.kstrinadka.chat.client.app.session.ClientSession;
import com.kstrinadka.chat.client.presentation.chat.ChatPresenter;
import com.kstrinadka.chat.client.presentation.chat.ChatView;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

public class MainFrame extends JFrame implements ChatView {

    private static final String CARD_PLACEHOLDER = "placeholder";
    private static final String CARD_CHAT = "chat";

    private static final Color WINDOW_BG = new Color(24, 33, 42);
    private static final Color SIDEBAR_BG = new Color(30, 40, 50);
    private static final Color CHAT_BG = new Color(18, 27, 34);
    private static final Color INPUT_BG = new Color(38, 49, 58);
    private static final Color HEADER_BG = new Color(29, 39, 49);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 240);
    private static final Color TEXT_SECONDARY = new Color(150, 160, 170);
    private static final Color ERROR_COLOR = new Color(255, 120, 120);

    private final DefaultListModel<ConversationListItemVm> contactsModel;
    private final JList<ConversationListItemVm> contactsList;
    private final JPanel messagesPanel;
    private final JScrollPane messagesScrollPane;
    private final JTextArea inputArea;
    private final JButton sendButton;
    private final JLabel chatTitleLabel;
    private final JLabel chatSubtitleLabel;
    private final JLabel errorLabel;
    private final JLabel placeholderLabel;

    private final JPanel centerPanel;
    private final java.awt.CardLayout centerCards;

    private final ChatPresenter presenter;

    public MainFrame(ClientSession session) {
        super("Chat Client — Main");

        contactsModel = new DefaultListModel<>();
        contactsList = new JList<>(contactsModel);
        messagesPanel = new JPanel();
        messagesScrollPane = new JScrollPane(messagesPanel);
        inputArea = new JTextArea(3, 20);
        sendButton = new JButton("Send");
        chatTitleLabel = new JLabel(" ");
        chatSubtitleLabel = new JLabel(" ");
        errorLabel = new JLabel(" ");
        placeholderLabel = new JLabel("Выберите чат слева", SwingConstants.CENTER);

        centerCards = new java.awt.CardLayout();
        centerPanel = new JPanel(centerCards);

        presenter = new ChatPresenter(this, session);

        initFrame();
        initUi(session.username());
        bindActions();

        session.setIncomingBridge(presenter::onIncoming);
        session.setDisconnectBridge(presenter::onDisconnected);

        presenter.initialize();
    }

    private void initFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 700));
        setSize(1100, 760);
        setLocationRelativeTo(null);
    }

    private void initUi(String currentUsername) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WINDOW_BG);
        setContentPane(root);

        root.add(createSidebarPanel(currentUsername), BorderLayout.WEST);
        root.add(createCenterArea(), BorderLayout.CENTER);
    }

    private JComponent createSidebarPanel(String currentUsername) {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(45, 58, 70)));

        JLabel profileLabel = new JLabel("Logged in as: " + currentUsername);
        profileLabel.setForeground(TEXT_PRIMARY);
        profileLabel.setFont(profileLabel.getFont().deriveFont(Font.BOLD, 14f));

        JLabel sectionLabel = new JLabel("Chats");
        sectionLabel.setForeground(TEXT_SECONDARY);
        sectionLabel.setFont(sectionLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 18, 16, 18));

        profileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(profileLabel);
        topPanel.add(Box.createVerticalStrut(14));
        topPanel.add(sectionLabel);

        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsList.setBackground(SIDEBAR_BG);
        contactsList.setForeground(TEXT_PRIMARY);
        contactsList.setFixedCellHeight(64);
        contactsList.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        contactsList.setCellRenderer(new ConversationListCellRenderer());

        JScrollPane contactsScrollPane = new JScrollPane(contactsList);
        contactsScrollPane.setBorder(null);
        contactsScrollPane.getViewport().setBackground(SIDEBAR_BG);

        sidebar.add(topPanel, BorderLayout.NORTH);
        sidebar.add(contactsScrollPane, BorderLayout.CENTER);

        return sidebar;
    }

    private JComponent createCenterArea() {
        JPanel placeholderPanel = new JPanel(new BorderLayout());
        placeholderPanel.setBackground(CHAT_BG);
        placeholderLabel.setForeground(TEXT_SECONDARY);
        placeholderLabel.setFont(placeholderLabel.getFont().deriveFont(Font.PLAIN, 18f));
        placeholderPanel.add(placeholderLabel, BorderLayout.CENTER);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(CHAT_BG);
        chatPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        chatPanel.add(createMessagesArea(), BorderLayout.CENTER);
        chatPanel.add(createInputPanel(), BorderLayout.SOUTH);

        centerPanel.add(placeholderPanel, CARD_PLACEHOLDER);
        centerPanel.add(chatPanel, CARD_CHAT);

        centerCards.show(centerPanel, CARD_PLACEHOLDER);
        return centerPanel;
    }

    private JComponent createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        chatTitleLabel.setForeground(TEXT_PRIMARY);
        chatTitleLabel.setFont(chatTitleLabel.getFont().deriveFont(Font.BOLD, 16f));

        chatSubtitleLabel.setForeground(TEXT_SECONDARY);
        chatSubtitleLabel.setFont(chatSubtitleLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(chatTitleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(chatSubtitleLabel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(textPanel, BorderLayout.CENTER);

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setBackground(HEADER_BG);
        northWrapper.add(wrapper, BorderLayout.CENTER);
        northWrapper.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);

        return northWrapper;
    }

    private JComponent createMessagesArea() {
        messagesPanel.setOpaque(true);
        messagesPanel.setBackground(CHAT_BG);
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));

        messagesScrollPane.setBorder(null);
        messagesScrollPane.getViewport().setBackground(CHAT_BG);
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return messagesScrollPane;
    }

    private JComponent createInputPanel() {
        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setBackground(HEADER_BG);

        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(4, 16, 0, 16));
        errorLabel.setFont(errorLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(HEADER_BG);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBackground(INPUT_BG);
        inputArea.setForeground(TEXT_PRIMARY);
        inputArea.setCaretColor(TEXT_PRIMARY);
        inputArea.setFont(inputArea.getFont().deriveFont(Font.PLAIN, 14f));
        inputArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setBorder(BorderFactory.createEmptyBorder());
        inputScrollPane.setPreferredSize(new Dimension(0, 78));

        sendButton.setPreferredSize(new Dimension(110, 42));

        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        inputWrapper.add(errorLabel, BorderLayout.NORTH);
        inputWrapper.add(inputPanel, BorderLayout.CENTER);

        return inputWrapper;
    }

    private void bindActions() {
        contactsList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                ConversationListItemVm selected = contactsList.getSelectedValue();
                errorLabel.setText(" ");
                presenter.onConversationSelected(selected != null ? selected.username() : null);
            }
        });

        sendButton.addActionListener(event -> {
            errorLabel.setText(" ");
            presenter.onSendClicked(inputArea.getText());
        });

        getRootPane().setDefaultButton(sendButton);
    }

    @Override
    public void showMessages(List<MessageVm> messages) {
        messagesPanel.removeAll();

        for (MessageVm message : messages) {
            messagesPanel.add(new MessageBubblePanel(message));
        }

        messagesPanel.revalidate();
        messagesPanel.repaint();
        scrollToBottom();
    }

    @Override
    public void appendMessage(MessageVm message) {
        messagesPanel.add(new MessageBubblePanel(message));
        messagesPanel.revalidate();
        messagesPanel.repaint();
        scrollToBottom();
    }

    @Override
    public void clearInput() {
        inputArea.setText("");
        inputArea.requestFocusInWindow();
    }

    @Override
    public void setSendEnabled(boolean enabled) {
        sendButton.setEnabled(enabled);
        inputArea.setEnabled(enabled);
    }

    @Override
    public void showError(String message) {
        errorLabel.setText(message == null || message.isBlank() ? " " : message);
    }

    @Override
    public void showConversationItems(List<ConversationListItemVm> items, String selectedUsername) {
        contactsModel.clear();

        int selectedIndex = -1;
        for (int i = 0; i < items.size(); i++) {
            ConversationListItemVm item = items.get(i);
            contactsModel.addElement(item);
            if (selectedUsername != null && selectedUsername.equals(item.username())) {
                selectedIndex = i;
            }
        }

        if (selectedIndex >= 0) {
            contactsList.setSelectedIndex(selectedIndex);
        } else {
            contactsList.clearSelection();
        }
    }

    @Override
    public void showChatPlaceholder(String text) {
        placeholderLabel.setText(text);
        chatTitleLabel.setText(" ");
        chatSubtitleLabel.setText(" ");
        messagesPanel.removeAll();
        messagesPanel.revalidate();
        messagesPanel.repaint();
        centerCards.show(centerPanel, CARD_PLACEHOLDER);
    }

    @Override
    public void showChatContent() {
        ConversationListItemVm selected = contactsList.getSelectedValue();
        if (selected != null) {
            chatTitleLabel.setText(selected.username());
            chatSubtitleLabel.setText("Conversation");
        }
        centerCards.show(centerPanel, CARD_CHAT);
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() ->
                messagesScrollPane.getVerticalScrollBar().setValue(
                        messagesScrollPane.getVerticalScrollBar().getMaximum()
                )
        );
    }
}
