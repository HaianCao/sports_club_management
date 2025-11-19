package com.sportclub;

import com.sportclub.ui.MainWindow;

/**
 * GUI Launcher for Sports Club Management System
 */
public class GuiLauncher {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}