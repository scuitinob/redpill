package com.corvindevelop.redpill.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

final class TrayController implements AutoCloseable {
    private final SystemTray tray;
    private final TrayIcon icon;

    private TrayController(SystemTray tray, TrayIcon icon) {
        this.tray = tray;
        this.icon = icon;
    }

    static TrayController install(Runnable showWindow, Runnable toggleActive, Runnable exit) throws AWTException {
        if (!SystemTray.isSupported()) {
            return null;
        }

        PopupMenu popup = new PopupMenu();
        MenuItem showItem = new MenuItem("Open RedPill");
        showItem.addActionListener(event -> showWindow.run());

        MenuItem toggleItem = new MenuItem("Start / Stop");
        toggleItem.addActionListener(event -> toggleActive.run());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(event -> exit.run());

        popup.add(showItem);
        popup.add(toggleItem);
        popup.addSeparator();
        popup.add(exitItem);

        Image source = ResourceImages.load("/images/redpill-pill.png");
        Image trayImage = source.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        TrayIcon trayIcon = new TrayIcon(trayImage, "RedPill", popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(event -> showWindow.run());

        SystemTray systemTray = SystemTray.getSystemTray();
        systemTray.add(trayIcon);
        return new TrayController(systemTray, trayIcon);
    }

    void setActive(boolean active) {
        icon.setToolTip(active ? "RedPill - ACTIVE" : "RedPill - INACTIVE");
    }

    void displayMessage(String title, String message) {
        icon.displayMessage(title, message, TrayIcon.MessageType.NONE);
    }

    @Override
    public void close() {
        tray.remove(icon);
    }
}
