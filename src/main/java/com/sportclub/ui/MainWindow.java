package com.sportclub.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main application window for Sports Club Management
 */
public class MainWindow extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Panel components
    private MemberManagementPanel memberPanel;
    private SubjectManagementPanel subjectPanel;
    private ScheduleManagementPanel schedulePanel;
    private AttendancePanel attendancePanel;
    private ReportPanel reportPanel;

    public MainWindow() {
        initializeComponents();
        setupLayout();
        setDefaultProperties();
    }

    private void initializeComponents() {
        // Initialize CardLayout for switching panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Initialize management panels
        memberPanel = new MemberManagementPanel();
        subjectPanel = new SubjectManagementPanel();
        schedulePanel = new ScheduleManagementPanel();
        attendancePanel = new AttendancePanel();
        reportPanel = new ReportPanel();

        // Add panels to CardLayout
        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(memberPanel, "MEMBERS");
        contentPanel.add(subjectPanel, "SUBJECTS");
        contentPanel.add(schedulePanel, "SCHEDULE");
        contentPanel.add(attendancePanel, "ATTENDANCE");
        contentPanel.add(reportPanel, "REPORTS");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create and add menu bar
        add(createMenuBar(), BorderLayout.NORTH);

        // Add content panel to center
        add(contentPanel, BorderLayout.CENTER);

        // Add status bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(51, 122, 183));

        // Home button
        JButton homeBtn = createMenuButton("🏠 Trang Chủ", "HOME");
        menuBar.add(homeBtn);

        menuBar.add(Box.createHorizontalStrut(10));

        // Members button
        JButton membersBtn = createMenuButton("👥 Thành Viên", "MEMBERS");
        menuBar.add(membersBtn);

        menuBar.add(Box.createHorizontalStrut(10));

        // Subjects button
        JButton subjectsBtn = createMenuButton("🏃 Môn Tập", "SUBJECTS");
        menuBar.add(subjectsBtn);

        menuBar.add(Box.createHorizontalStrut(10));

        // Schedule button
        JButton scheduleBtn = createMenuButton("📅 Lịch Tập", "SCHEDULE");
        menuBar.add(scheduleBtn);

        menuBar.add(Box.createHorizontalStrut(10));

        // Attendance button
        JButton attendanceBtn = createMenuButton("✓ Điểm Danh", "ATTENDANCE");
        menuBar.add(attendanceBtn);

        menuBar.add(Box.createHorizontalStrut(10));

        // Reports button
        JButton reportsBtn = createMenuButton("📄 In File", "REPORTS");
        menuBar.add(reportsBtn);

        // Add glue to push everything to the left
        menuBar.add(Box.createHorizontalGlue());

        return menuBar;
    }

    private JButton createMenuButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 122, 183));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40, 96, 144));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 122, 183));
            }
        });

        button.addActionListener(e -> cardLayout.show(contentPanel, panelName));

        return button;
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ CÂU LẠC BỘ THỂ THAO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 122, 183));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 30, 20));

        // Welcome message
        JPanel welcomePanel = new JPanel(new GridLayout(3, 2, 20, 20));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        welcomePanel.setBackground(Color.WHITE);

        // Quick access buttons
        welcomePanel
                .add(createQuickAccessButton("Quản lý Thành viên", "Xem và quản lý thông tin thành viên", "MEMBERS"));
        welcomePanel.add(createQuickAccessButton("Quản lý Môn tập", "Thêm, sửa, xóa các môn thể thao", "SUBJECTS"));
        welcomePanel
                .add(createQuickAccessButton("Quản lý Lịch tập", "Tạo và quản lý lịch trình tập luyện", "SCHEDULE"));
        welcomePanel.add(createQuickAccessButton("Điểm danh", "Ghi nhận sự tham gia của thành viên", "ATTENDANCE"));
        welcomePanel.add(createQuickAccessButton("In file", "Xuất báo cáo và in tài liệu", "REPORTS"));
        // Empty panel to maintain grid layout
        welcomePanel.add(new JPanel());

        homePanel.add(titleLabel, BorderLayout.NORTH);
        homePanel.add(welcomePanel, BorderLayout.CENTER);

        return homePanel;
    }

    private JPanel createQuickAccessButton(String title, String description, String panelName) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(51, 122, 183));

        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(Color.GRAY);

        buttonPanel.add(titleLabel, BorderLayout.NORTH);
        buttonPanel.add(descLabel, BorderLayout.CENTER);

        // Click handler
        buttonPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(contentPanel, panelName);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buttonPanel.setBackground(new Color(245, 245, 245));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                buttonPanel.setBackground(Color.WHITE);
            }
        });

        return buttonPanel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(245, 245, 245));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel statusLabel = new JLabel("Sẵn sàng | Múi giờ: GMT+7 | " + new java.util.Date());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        statusLabel.setForeground(Color.GRAY);

        statusBar.add(statusLabel);
        return statusBar;
    }

    private void setDefaultProperties() {
        setTitle("Hệ thống Quản lý Câu lạc bộ Thể thao");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        // Set application icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Icon file not found, use default
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}