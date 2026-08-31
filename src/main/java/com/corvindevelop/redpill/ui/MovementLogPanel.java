package com.corvindevelop.redpill.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;

/** Small visible activity log, inspired by BluePill's terminal output. */
final class MovementLogPanel extends JPanel {
    private final JTextArea logArea = new JTextArea();

    MovementLogPanel() {
        setLayout(new BorderLayout());
        setBackground(RedPillTheme.PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RedPillTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        setPreferredSize(new Dimension(340, 142));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 142));

        logArea.setEditable(false);
        logArea.setFocusable(false);
        logArea.setLineWrap(false);
        logArea.setBackground(RedPillTheme.BACKGROUND);
        logArea.setForeground(RedPillTheme.MATRIX_GREEN);
        logArea.setCaretColor(RedPillTheme.MATRIX_GREEN);
        logArea.setFont(RedPillTheme.LOG);
        logArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane scroll = new JScrollPane(logArea);

        scroll.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(RedPillTheme.BORDER),
                        " MOVEMENT LOG ",
                        0,
                        0,
                        RedPillTheme.LABEL,
                        RedPillTheme.MUTED
                )
        );

        scroll.setBackground(RedPillTheme.BACKGROUND);
        scroll.getViewport().setBackground(RedPillTheme.BACKGROUND);
        scroll.setOpaque(true);
        scroll.getViewport().setOpaque(true);

        add(scroll, BorderLayout.CENTER);
        scroll.getViewport().setBorder(null);

    }

    void append(String message) {
        if (!logArea.getText().isEmpty()) {
            logArea.append(System.lineSeparator());
        }
        logArea.append(message);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    void clear() {
        logArea.setText("");
    }
}
