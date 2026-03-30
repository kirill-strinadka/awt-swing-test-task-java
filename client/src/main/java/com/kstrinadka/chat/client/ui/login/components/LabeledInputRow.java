package com.kstrinadka.chat.client.ui.login.components;

import javax.swing.*;
import java.awt.*;

public final class LabeledInputRow extends JPanel {

    private final JLabel label;
    private final JComponent field;

    public LabeledInputRow(String labelText, JComponent field) {
        this.label = new JLabel(labelText);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        this.field = field;
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(320, 40));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(label);
        add(field);
    }

    public JLabel getLabel() {
        return label;
    }

    public JComponent getField() {
        return field;
    }
}
