package com.corvindevelop.redpill.ui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Image;

/** Displays the official RedPill artwork as the in-app title. */
final class RedPillLogoPanel extends JPanel {
    RedPillLogoPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(350, 180));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        Image source = ResourceImages.load("/images/redpill-icon.png");
        int maxWidth = 330;
        int maxHeight = 175;
        double scale = Math.min((double) maxWidth / source.getWidth(null), (double) maxHeight / source.getHeight(null));
        int targetWidth = Math.max(1, (int) Math.round(source.getWidth(null) * scale));
        int targetHeight = Math.max(1, (int) Math.round(source.getHeight(null) * scale));
        Image scaled = source.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        add(new JLabel(new ImageIcon(scaled)));
    }
}
