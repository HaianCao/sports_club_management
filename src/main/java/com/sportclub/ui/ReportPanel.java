package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;
import com.sportclub.util.TimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Panel for generating and printing reports
 */
public class ReportPanel extends JPanel {

    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> reportTypeCombo;
    private JButton generateBtn, printBtn, exportBtn;
    private JTextArea reportTextArea;

    public ReportPanel() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        // Table setup
        String[] columns = { "STT", "Thông tin", "Chi tiết", "Trạng thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(tableModel);

        // Report type combo
        reportTypeCombo = new JComboBox<>(new String[] {
                "Báo cáo thành viên",
                "Báo cáo môn tập",
                "Báo cáo điểm danh"
        });

        // Text area for detailed reports
        reportTextArea = new JTextArea(10, 40);
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Buttons
        generateBtn = new JButton("Tạo báo cáo");
        printBtn = new JButton("In báo cáo");
        exportBtn = new JButton("Xuất file");

        // Button styling
        styleButton(generateBtn, new Color(51, 122, 183));
        styleButton(printBtn, new Color(92, 184, 92));
        styleButton(exportBtn, new Color(240, 173, 78));

        // Button actions
        generateBtn.addActionListener(this::generateReport);
        printBtn.addActionListener(this::printReport);
        exportBtn.addActionListener(this::exportReport);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("BÁO CÁO VÀ IN FILE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Loại báo cáo:"));
        controlPanel.add(reportTypeCombo);
        controlPanel.add(generateBtn);
        controlPanel.add(printBtn);
        controlPanel.add(exportBtn);

        // Main content
        JTabbedPane tabbedPane = new JTabbedPane();

        // Table view
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        tabbedPane.addTab("Bảng dữ liệu", tablePanel);

        // Text view
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(new JScrollPane(reportTextArea), BorderLayout.CENTER);
        tabbedPane.addTab("Báo cáo chi tiết", textPanel);

        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(controlPanel, BorderLayout.NORTH);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        button.setMargin(new java.awt.Insets(5, 10, 5, 10));
    }

    private void generateReport(ActionEvent e) {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        tableModel.setRowCount(0);
        reportTextArea.setText("");

        try {
            switch (reportType) {
                case "Báo cáo thành viên":
                    generateMemberReport();
                    break;
                case "Báo cáo môn tập":
                    generateSubjectReport();
                    break;
                case "Báo cáo điểm danh":
                    generateAttendanceReport();
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo báo cáo: " + ex.getMessage());
        }
    }

    private void generateMemberReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== BÁO CÁO THÀNH VIÊN ===\n");
        report.append("Thời gian tạo: ").append(TimeUtil.formatTimestamp(TimeUtil.getCurrentTimestamp()))
                .append("\n\n");

        try {
            List<User> users = Query.findAll(User.class);

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                String status = user.isDeleted() ? "Đã xóa" : "Hoạt động";

                Object[] row = {
                        i + 1,
                        user.getName(),
                        "SĐT: " + user.getPhone() + ", Tài khoản: " + user.getAccount(),
                        status
                };
                tableModel.addRow(row);

                report.append(String.format("%-3d %-20s %-15s %-15s %-10s\n",
                        i + 1, user.getName(), user.getPhone(), user.getAccount(), status));
            }

        } catch (Exception e) {
            report.append("Lỗi khi tạo báo cáo: ").append(e.getMessage());
        }

        reportTextArea.setText(report.toString());
    }

    private void generateSubjectReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== BÁO CÁO MÔN TẬP ===\n");
        report.append("Thời gian tạo: ").append(TimeUtil.formatTimestamp(TimeUtil.getCurrentTimestamp()))
                .append("\n\n");

        try {
            List<Subject> subjects = Query.findAll(Subject.class);

            for (int i = 0; i < subjects.size(); i++) {
                Subject subject = subjects.get(i);
                String status = subject.isDeleted() ? "Đã xóa" : "Hoạt động";

                Object[] row = {
                        i + 1,
                        subject.getName(),
                        subject.getDescription() != null ? (subject.getDescription().length() > 30
                                ? subject.getDescription().substring(0, 30) + "..."
                                : subject.getDescription()) : "",
                        status
                };
                tableModel.addRow(row);

                report.append(String.format("%-3d %-25s %-10s\n",
                        i + 1, subject.getName(), status));
                if (subject.getDescription() != null && !subject.getDescription().isEmpty()) {
                    report.append("    Mô tả: ").append(subject.getDescription()).append("\n");
                }
                report.append("\n");
            }

        } catch (Exception e) {
            report.append("Lỗi khi tạo báo cáo: ").append(e.getMessage());
        }

        reportTextArea.setText(report.toString());
    }

    private void generateAttendanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== BÁO CÁO ĐIỂM DANH ===\n");
        report.append("Thời gian tạo: ").append(TimeUtil.formatTimestamp(TimeUtil.getCurrentTimestamp()))
                .append("\n\n");

        try {
            List<Join> joins = CRUDManager.getAll(Join.class);

            for (int i = 0; i < joins.size(); i++) {
                Join join = joins.get(i);
                if (join.getIsDeleted() == 0) {
                    User user = Query.findById(User.class, join.getId().getuId());
                    Subject subject = Query.findById(Subject.class, join.getId().getSubjectId());

                    if (user != null && subject != null) {
                        String participated = join.getParticipated() == 1 ? "Có" : "Không";

                        Object[] row = {
                                i + 1,
                                user.getName(),
                                "Môn: " + subject.getName(),
                                participated
                        };
                        tableModel.addRow(row);

                        report.append(String.format("%-3d %-20s %-20s %-10s\n",
                                i + 1, user.getName(), subject.getName(), participated));

                        if (join.getComment() != null && !join.getComment().isEmpty()) {
                            report.append("    Ghi chú: ").append(join.getComment()).append("\n");
                        }
                    }
                }
            }

        } catch (Exception e) {
            report.append("Lỗi khi tạo báo cáo: ").append(e.getMessage());
        }

        reportTextArea.setText(report.toString());
    }

    private void printReport(ActionEvent e) {
        try {
            if (!reportTextArea.getText().trim().isEmpty()) {
                boolean complete = reportTextArea.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "In báo cáo thành công!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng tạo báo cáo trước khi in!");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi in: " + ex.getMessage());
        }
    }

    private void exportReport(ActionEvent e) {
        try {
            if (!reportTextArea.getText().trim().isEmpty()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("report_" +
                        System.currentTimeMillis() + ".txt"));

                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                        writer.write(reportTextArea.getText());
                        JOptionPane.showMessageDialog(this,
                                "Xuất file thành công: " + fileChooser.getSelectedFile().getName());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng tạo báo cáo trước khi xuất file!");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage());
        }
    }
}