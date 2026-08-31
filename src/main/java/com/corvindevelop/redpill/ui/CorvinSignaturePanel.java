package com.corvindevelop.redpill.ui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Image;

/** Displays the official Corvin Develop logo supplied for the project. */
final class CorvinSignaturePanel extends JPanel {
    CorvinSignaturePanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(320, 104));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));

        Image source = ResourceImages.load("/images/corvin-develop.png");
        Image scaled = source.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        add(new JLabel(new ImageIcon(scaled)));
    }
}
