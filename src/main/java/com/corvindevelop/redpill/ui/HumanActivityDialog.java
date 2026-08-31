package com.corvindevelop.redpill.ui;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

final class HumanActivityDialog {
    private static final int TIMEOUT_SECONDS = 10;

    private HumanActivityDialog() {
    }

    static void show(JFrame owner, Runnable stopAction, Runnable continueAction) {
        JDialog dialog = new JDialog(owner, "RedPill", false);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setAlwaysOnTop(true);
        dialog.setIconImage(ResourceImages.load("/images/redpill-pill.png"));

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(RedPillTheme.BACKGROUND);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RedPillTheme.MATRIX_GREEN_DIM),
                BorderFactory.createEmptyBorder(18, 20, 16, 20)
        ));

        JLabel message = new JLabel(
                "<html><div style='text-align:center'>Movimiento humano detectado.<br>¿Desea detener el proceso?</div></html>",
                SwingConstants.CENTER
        );
        message.setForeground(RedPillTheme.TEXT);
        message.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        JLabel countdown = new JLabel("Continuando automáticamente en 10 s", SwingConstants.CENTER);
        countdown.setForeground(RedPillTheme.MATRIX_GREEN);
        countdown.setFont(RedPillTheme.LABEL);

        RedPillButton yes = new RedPillButton("SÍ, DETENER", RedPillTheme.RED, RedPillTheme.RED_HOVER);
        RedPillButton no = new RedPillButton("NO, CONTINUAR", RedPillTheme.STOP, RedPillTheme.STOP_HOVER);
        yes.setPreferredSize(new Dimension(150, 40));
        no.setPreferredSize(new Dimension(160, 40));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.setOpaque(false);
        buttons.add(yes);
        buttons.add(no);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(message, BorderLayout.CENTER);
        center.add(countdown, BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setPreferredSize(new Dimension(410, 190));
        dialog.pack();
        dialog.setLocationRelativeTo(owner);

        AtomicBoolean resolved = new AtomicBoolean(false);
        final int[] remaining = {TIMEOUT_SECONDS};
        Timer timer = new Timer(1000, null);

        Runnable continueAndClose = () -> {
            if (!resolved.compareAndSet(false, true)) return;
            timer.stop();
            dialog.dispose();
            continueAction.run();
        };

        Runnable stopAndClose = () -> {
            if (!resolved.compareAndSet(false, true)) return;
            timer.stop();
            dialog.dispose();
            stopAction.run();
        };

        timer.addActionListener(event -> {
            remaining[0]--;
            if (remaining[0] <= 0) {
                continueAndClose.run();
            } else {
                countdown.setText("Continuando automáticamente en " + remaining[0] + " s");
            }
        });

        yes.addActionListener(event -> stopAndClose.run());
        no.addActionListener(event -> continueAndClose.run());
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                continueAndClose.run();
            }
        });

        dialog.setVisible(true);
        timer.start();
    }
}
