package com.corvindevelop.redpill.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Button whose rendering is controlled by RedPill instead of the host OS look-and-feel. */
final class RedPillButton extends JButton {
    private Color normalBackground;
    private Color hoverBackground;
    private boolean hovering;

    RedPillButton(String text, Color normalBackground, Color hoverBackground) {
        super(text);
        this.normalBackground = normalBackground;
        this.hoverBackground = hoverBackground;

        setUI(new BasicButtonUI());
        setFont(RedPillTheme.BUTTON);
        setForeground(Color.WHITE);
        setBackground(normalBackground);
        setOpaque(true);
        setContentAreaFilled(true);
        setBorderPainted(true);
        setFocusPainted(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RedPillTheme.RED_BRIGHT, 1),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
        setPreferredSize(new Dimension(190, 44));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                refreshBackground();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                refreshBackground();
            }
        });
    }

    void setButtonColors(Color normalBackground, Color hoverBackground) {
        this.normalBackground = normalBackground;
        this.hoverBackground = hoverBackground;
        refreshBackground();
    }

    private void refreshBackground() {
        setBackground(hovering && isEnabled() ? hoverBackground : normalBackground);
        repaint();
    }
}
