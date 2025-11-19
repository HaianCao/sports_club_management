package com.sportclub;

import javax.swing.*;
import java.awt.*;

/**
 * Simple GUI Test to ensure window appears
 */
public class GuiTest {
    public static void main(String[] args) {
        // Force GUI to show on main thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Create simple test window
                JFrame frame = new JFrame("🎯 TEST GUI - SPORTS CLUB MANAGEMENT");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null); // Center on screen

                // Make sure it appears on top
                frame.setAlwaysOnTop(true);
                frame.setExtendedState(JFrame.NORMAL);

                // Add content
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(Color.WHITE);

                JLabel titleLabel = new JLabel("🏆 SPORTS CLUB MANAGEMENT SYSTEM", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                titleLabel.setForeground(new Color(51, 122, 183));
                titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 30, 20));

                JLabel messageLabel = new JLabel("<html><center>" +
                        "GUI Test thành công!<br><br>" +
                        "Nếu bạn thấy cửa sổ này, hệ thống đã hoạt động.<br>" +
                        "Hãy đóng cửa sổ này và chạy lại ứng dụng chính.<br><br>" +
                        "<b>Lệnh chạy:</b> mvn exec:java@gui" +
                        "</center></html>", SwingConstants.CENTER);
                messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JButton closeBtn = new JButton("Đóng và chạy ứng dụng chính");
                closeBtn.addActionListener(e -> {
                    frame.dispose();
                    System.out.println("✅ GUI Test completed! Now run: mvn exec:java@gui");
                });

                panel.add(titleLabel, BorderLayout.NORTH);
                panel.add(messageLabel, BorderLayout.CENTER);
                panel.add(closeBtn, BorderLayout.SOUTH);

                frame.add(panel);
                frame.setVisible(true);

                // Force to front
                frame.toFront();
                frame.requestFocus();

                System.out.println("🚀 GUI Test Window created and should be visible!");
                System.out.println("📍 Window position: " + frame.getLocation());
                System.out.println("📐 Window size: " + frame.getSize());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        });
    }
}