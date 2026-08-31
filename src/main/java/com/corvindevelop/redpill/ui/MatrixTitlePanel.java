package com.corvindevelop.redpill.ui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

/** Lightweight Matrix-inspired title rendered without external image assets. */
final class MatrixTitlePanel extends JPanel {
    private static final String GLYPHS = "01ZXCVBNMASDFGHJKLQWERTY";
    private final char[] glyphs;
    private final int[] yOffsets;

    MatrixTitlePanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(340, 88));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));

        Random random = new Random(21L);
        glyphs = new char[34];
        yOffsets = new int[glyphs.length];
        for (int i = 0; i < glyphs.length; i++) {
            glyphs[i] = GLYPHS.charAt(random.nextInt(GLYPHS.length()));
            yOffsets[i] = 8 + random.nextInt(66);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();

            g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            g2.setColor(RedPillTheme.MATRIX_GREEN_DIM);
            for (int i = 0; i < glyphs.length; i++) {
                int x = 5 + i * Math.max(8, (w - 10) / glyphs.length);
                g2.drawString(String.valueOf(glyphs[i]), x, yOffsets[i]);
            }

            String text = "REDPILL";
            Font font = new Font(Font.MONOSPACED, Font.BOLD, 36);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (w - textWidth) / 2;
            int y = 55;

            // Subtle black plate keeps the title readable over the code rain.
            g2.setColor(new java.awt.Color(5, 7, 6, 220));
            g2.fill(new RoundRectangle2D.Double(x - 16, 19, textWidth + 32, 49, 18, 18));

            g2.setColor(RedPillTheme.RED_DARK);
            g2.drawString(text, x + 2, y + 2);
            g2.setColor(RedPillTheme.RED);
            g2.drawString(text, x, y);

            // Red pill capsule beneath the wordmark.
            int pillW = 42;
            int pillH = 12;
            int px = (w - pillW) / 2;
            int py = 68;
            g2.setColor(RedPillTheme.RED);
            g2.fillRoundRect(px, py, pillW, pillH, pillH, pillH);
            g2.setColor(RedPillTheme.RED_DARK);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(px + pillW / 2, py + 1, px + pillW / 2, py + pillH - 1);
        } finally {
            g2.dispose();
        }
    }
}
