package com.corvindevelop.redpill.ui;

import com.corvindevelop.redpill.core.ActivityListener;
import com.corvindevelop.redpill.core.HumanActivityMonitor;
import com.corvindevelop.redpill.core.MouseActivityService;
import com.github.kwhat.jnativehook.NativeHookException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;

public final class RedPillWindow implements ActivityListener {
    private static final IntervalOption[] INTERVALS = {
            new IntervalOption("20 seconds", Duration.ofSeconds(20)),
            new IntervalOption("30 seconds", Duration.ofSeconds(30)),
            new IntervalOption("1 minute", Duration.ofMinutes(1)),
            new IntervalOption("2 minutes", Duration.ofMinutes(2)),
            new IntervalOption("3 minutes", Duration.ofMinutes(3)),
            new IntervalOption("5 minutes", Duration.ofMinutes(5))
    };

    private final MouseActivityService activityService;
    private final HumanActivityMonitor humanActivityMonitor;
    private final JFrame frame = new JFrame("RedPill");
    private final JLabel stateDot = new JLabel("●");
    private final JLabel stateLabel = new JLabel("INACTIVE");
    private final JLabel countLabel = new JLabel("0", SwingConstants.CENTER);
    private final JComboBox<IntervalOption> intervalBox = new JComboBox<>(INTERVALS);
    private final RedPillButton toggleButton = new RedPillButton("START", RedPillTheme.RED, RedPillTheme.RED_HOVER);
    private final MovementLogPanel movementLogPanel = new MovementLogPanel();

    private TrayController trayController;
    private boolean exiting;

    public RedPillWindow(MouseActivityService activityService) throws NativeHookException {
        this.activityService = activityService;
        this.humanActivityMonitor = new HumanActivityMonitor(this::onHumanActivityDetected);

        activityService.setListener(this);
        activityService.setHumanActivityMonitor(humanActivityMonitor);
        humanActivityMonitor.start();

        buildWindow();
        installTray();
    }

    public void showWindow() {
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();
    }

    public static void showStartupError(Exception exception) {
        JOptionPane.showMessageDialog(
                null,
                "RedPill could not start.\n\n" + exception.getMessage(),
                "RedPill",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void buildWindow() {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(430, 760));
        frame.setSize(455, 810);
        frame.setIconImage(ResourceImages.load("/images/redpill-pill.png"));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(RedPillTheme.BACKGROUND);
        frame.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setBackground(RedPillTheme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(18, 28, 14, 28));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        RedPillLogoPanel title = new RedPillLogoPanel();
        title.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(12));
        content.add(createStatusPanel());
        content.add(Box.createVerticalStrut(14));
        content.add(createIntervalPanel());
        content.add(Box.createVerticalStrut(20));

        styleToggleButton();
        toggleButton.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        toggleButton.addActionListener(event -> toggleActivity());
        content.add(toggleButton);
        content.add(Box.createVerticalStrut(16));

        movementLogPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        content.add(movementLogPanel);
        content.add(Box.createVerticalGlue());

        CorvinSignaturePanel signature = new CorvinSignaturePanel();
        signature.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        content.add(signature);

        frame.add(content, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                handleWindowClose();
            }
        });
    }

    private JPanel createStatusPanel() {
        JPanel panel = cardPanel(142);
        panel.setLayout(new BorderLayout(0, 12));

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        statusRow.setOpaque(false);

        JLabel statusTitle = new JLabel("STATUS");
        statusTitle.setForeground(RedPillTheme.MUTED);
        statusTitle.setFont(RedPillTheme.LABEL);

        stateDot.setForeground(RedPillTheme.INACTIVE);
        stateDot.setFont(RedPillTheme.VALUE);
        stateLabel.setForeground(RedPillTheme.TEXT);
        stateLabel.setFont(RedPillTheme.VALUE);

        statusRow.add(statusTitle);
        statusRow.add(stateDot);
        statusRow.add(stateLabel);

        JPanel actions = new JPanel(new BorderLayout(0, 2));
        actions.setOpaque(false);
        JLabel actionsTitle = new JLabel("ACTIONS", SwingConstants.CENTER);
        actionsTitle.setForeground(RedPillTheme.MUTED);
        actionsTitle.setFont(RedPillTheme.LABEL);
        countLabel.setForeground(RedPillTheme.MATRIX_GREEN);
        countLabel.setFont(RedPillTheme.ACTION_COUNT);
        actions.add(actionsTitle, BorderLayout.NORTH);
        actions.add(countLabel, BorderLayout.CENTER);

        panel.add(statusRow, BorderLayout.NORTH);
        panel.add(actions, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createIntervalPanel() {
        JPanel panel = cardPanel(88);
        panel.setLayout(new BorderLayout(10, 8));

        JLabel label = new JLabel("ACTIVITY INTERVAL", SwingConstants.CENTER);
        label.setForeground(RedPillTheme.MUTED);
        label.setFont(RedPillTheme.LABEL);
        intervalBox.setSelectedIndex(0); // BluePill's original 20-second cadence.
        intervalBox.setToolTipText("Time between automatic activity sequences");

        panel.add(label, BorderLayout.NORTH);
        panel.add(intervalBox, BorderLayout.CENTER);
        return panel;
    }

    private JPanel cardPanel(int height) {
        JPanel panel = new JPanel();
        panel.setBackground(RedPillTheme.PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RedPillTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        panel.setPreferredSize(new Dimension(340, height));
        return panel;
    }

    private void styleToggleButton() {
        toggleButton.setPreferredSize(new Dimension(190, 44));
        toggleButton.setMaximumSize(new Dimension(190, 44));
    }

    private void installTray() {
        try {
            trayController = TrayController.install(
                    this::showWindow,
                    this::toggleActivity,
                    this::exitApplication
            );
        } catch (Exception ignored) {
            trayController = null;
        }
    }

    private void toggleActivity() {
        if (activityService.isActive()) {
            activityService.stop();
            return;
        }

        IntervalOption selected = (IntervalOption) intervalBox.getSelectedItem();
        if (selected != null) {
            movementLogPanel.clear();
            activityService.start(selected.duration());
        }
    }

    private void onHumanActivityDetected() {
        SwingUtilities.invokeLater(() -> {
            if (!activityService.pauseForHumanActivity()) {
                humanActivityMonitor.notificationHandled();
                return;
            }

            HumanActivityDialog.show(
                    frame,
                    () -> {
                        activityService.stop();
                        humanActivityMonitor.ignoreFor(Duration.ofMillis(800));
                        humanActivityMonitor.notificationHandled();
                    },
                    () -> {
                        activityService.resumeAfterHumanActivity();
                        humanActivityMonitor.notificationHandled();
                    }
            );
        });
    }

    private void handleWindowClose() {
        if (trayController != null && !exiting) {
            frame.setVisible(false);
            if (activityService.isActive()) {
                trayController.displayMessage("RedPill", "Still active in the system tray.");
            }
        } else {
            exitApplication();
        }
    }

    private void exitApplication() {
        exiting = true;
        activityService.close();
        humanActivityMonitor.close();
        if (trayController != null) {
            trayController.close();
        }
        frame.dispose();
    }

    @Override
    public void onStateChanged(boolean active) {
        SwingUtilities.invokeLater(() -> {
            stateLabel.setText(active ? "ACTIVE" : "INACTIVE");
            stateDot.setForeground(active ? RedPillTheme.MATRIX_GREEN : RedPillTheme.INACTIVE);
            toggleButton.setText(active ? "STOP" : "START");
            toggleButton.setButtonColors(
                    active ? RedPillTheme.STOP : RedPillTheme.RED,
                    active ? RedPillTheme.STOP_HOVER : RedPillTheme.RED_HOVER
            );
            intervalBox.setEnabled(!active);
            if (trayController != null) {
                trayController.setActive(active);
            }
        });
    }

    @Override
    public void onAction(long actionCount) {
        SwingUtilities.invokeLater(() -> countLabel.setText(Long.toString(actionCount)));
    }

    @Override
    public void onLog(String message) {
        SwingUtilities.invokeLater(() -> movementLogPanel.append(message));
    }

    @Override
    public void onError(Throwable error) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                frame,
                "RedPill stopped because it could not generate activity.\n\n" + error.getMessage(),
                "RedPill",
                JOptionPane.ERROR_MESSAGE
        ));
    }

    private record IntervalOption(String label, Duration duration) {
        @Override
        public String toString() {
            return label;
        }
    }
}
