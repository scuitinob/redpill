package com.corvindevelop.redpill;

import com.corvindevelop.redpill.core.MouseActivityService;
import com.corvindevelop.redpill.ui.RedPillWindow;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class RedPillApplication {
    private RedPillApplication() {
    }

    public static void main(String[] args) {
        configureLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            try {
                MouseActivityService activityService = new MouseActivityService();
                RedPillWindow window = new RedPillWindow(activityService);
                window.showWindow();
            } catch (Exception exception) {
                RedPillWindow.showStartupError(exception);
            }
        });
    }

    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing's default look and feel is a safe fallback.
        }
    }
}
