package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Panel for generating various reports
 */
public class ReportPanel extends JPanel {

    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> reportTypeCombo, subjectCombo, memberCombo;
    private JTextField startDateField, endDateField;
    private JButton generateBtn, exportBtn;
    private JTextArea summaryArea;

    public ReportPanel() {
        initializeComponents();
        setupLayout();
        loadComboData();
    }

    private void initializeComponents() {
        // Report type selection
        reportTypeCombo = new JComboBox<>(new String[]{
            "Báo cáo thành viên",
            "Báo cáo điểm danh",
            "Báo cáo môn tập",
            "Báo cáo lịch học",
            "Thống kê tổng quan"
        });

        subjectCombo = new JComboBox<>();
        memberCombo = new JComboBox<>();
        
        // Date fields
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date today = new java.util.Date();
        java.util.Date weekAgo = new java.util.Date(today.getTime() - 7 * 24 * 60 * 60 * 1000L);
        
        startDateField = new JTextField(sdf.format(weekAgo), 10);
        endDateField = new JTextField(sdf.format(today), 10);

        // Table setup
        String[] columns = {"STT", "Thông tin 1", "Thông tin 2", "Thông tin 3", "Thông tin 4"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(tableModel);

        // Summary area
        summaryArea = new JTextArea(5, 30);
        summaryArea.setEditable(false);
        summaryArea.setBorder(BorderFactory.createTitledBorder("Tóm tắt báo cáo"));

        // Buttons
        generateBtn = new JButton("Tạo báo cáo");
        generateBtn.setPreferredSize(new Dimension(120, 35));

        exportBtn = new JButton("Xuất file CSV");
        exportBtn.setPreferredSize(new Dimension(120, 35));
        exportBtn.setEnabled(false);

        // Action listeners
        generateBtn.addActionListener(this::generateReport);
        exportBtn.addActionListener(this::exportReport);
        reportTypeCombo.addActionListener(e -> updateFormFields());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Filter panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Tùy chọn báo cáo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Loại báo cáo:"), gbc);
        gbc.gridx = 1;
        filterPanel.add(reportTypeCombo, gbc);
        gbc.gridx = 2;
        filterPanel.add(new JLabel("Từ ngày:"), gbc);
        gbc.gridx = 3;
        filterPanel.add(startDateField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        filterPanel.add(new JLabel("Môn tập:"), gbc);
        gbc.gridx = 1;
        filterPanel.add(subjectCombo, gbc);
        gbc.gridx = 2;
        filterPanel.add(new JLabel("Đến ngày:"), gbc);
        gbc.gridx = 3;
        filterPanel.add(endDateField, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        filterPanel.add(new JLabel("Thành viên:"), gbc);
        gbc.gridx = 1;
        filterPanel.add(memberCombo, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(generateBtn);
        buttonPanel.add(exportBtn);

        // Main panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        centerPanel.add(new JScrollPane(summaryArea), BorderLayout.SOUTH);

        // Layout
        add(filterPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateFormFields();
    }

    private void loadComboData() {
        // Load subjects
        subjectCombo.removeAllItems();
        subjectCombo.addItem("Tất cả môn tập");
        List<Subject> subjects = Query.findActiveSubjects();
        if (subjects != null) {
            for (Subject subject : subjects) {
                subjectCombo.addItem(subject.getSubjId() + " - " + subject.getName());
            }
        }

        // Load members
        memberCombo.removeAllItems();
        memberCombo.addItem("Tất cả thành viên");
        List<Member> members = Query.findActiveMembers();
        if (members != null) {
            for (Member member : members) {
                memberCombo.addItem(member.getMemId() + " - " + member.getName());
            }
        }
    }

    private void updateFormFields() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        
        // Enable/disable fields based on report type
        boolean needSubject = reportType.contains("điểm danh") || reportType.contains("môn tập") || reportType.contains("lịch học");
        boolean needMember = reportType.contains("điểm danh") || reportType.contains("thành viên");
        boolean needDateRange = reportType.contains("điểm danh") || reportType.contains("thống kê");

        subjectCombo.setEnabled(needSubject);
        memberCombo.setEnabled(needMember);
        startDateField.setEnabled(needDateRange);
        endDateField.setEnabled(needDateRange);
    }

    private void generateReport(ActionEvent e) {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        
        try {
            switch (reportType) {
                case "Báo cáo thành viên":
                    generateMemberReport();
                    break;
                case "Báo cáo điểm danh":
                    generateAttendanceReport();
                    break;
                case "Báo cáo môn tập":
                    generateSubjectReport();
                    break;
                case "Báo cáo lịch học":
                    generateScheduleReport();
                    break;
                case "Thống kê tổng quan":
                    generateOverviewReport();
                    break;
            }
            exportBtn.setEnabled(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateMemberReport() {
        String[] columns = {"STT", "Mã TV", "Tên", "Ngày sinh", "Số điện thoại"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        List<Member> members = Query.findActiveMembers();
        if (members != null) {
            int index = 1;
            for (Member member : members) {
                Object[] row = {
                    index++,
                    member.getMemId(),
                    member.getName(),
                    member.getBirth(),
                    member.getPhone()
                };
                tableModel.addRow(row);
            }
        }

        summaryArea.setText("Tổng số thành viên: " + (members != null ? members.size() : 0) + "\n" +
                           "Báo cáo được tạo lúc: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
    }

    private void generateAttendanceReport() {
        String[] columns = {"STT", "Thành viên", "Môn tập", "Ngày", "Trạng thái"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        try {
            Date startDate = Date.valueOf(startDateField.getText());
            Date endDate = Date.valueOf(endDateField.getText());
            
            List<Attendance> attendances = Query.findAttendanceByDateRange(startDate, endDate);
            if (attendances != null) {
                int index = 1;
                int presentCount = 0, absentCount = 0;
                
                for (Attendance attendance : attendances) {
                    Member member = Query.findById(Member.class, attendance.getMemId());
                    Timeline timeline = Query.findById(Timeline.class, attendance.getTimelineId());
                    Subject subject = null;
                    if (timeline != null) {
                        subject = Query.findById(Subject.class, timeline.getSubjId());
                    }

                    Object[] row = {
                        index++,
                        member != null ? member.getName() : "Unknown",
                        subject != null ? subject.getName() : "Unknown",
                        attendance.getAttendanceDate(),
                        attendance.getStatus()
                    };
                    tableModel.addRow(row);

                    if ("Có mặt".equals(attendance.getStatus())) presentCount++;
                    else absentCount++;
                }

                summaryArea.setText("Tổng số bản ghi: " + attendances.size() + "\n" +
                                   "Có mặt: " + presentCount + "\n" +
                                   "Vắng mặt: " + absentCount + "\n" +
                                   "Tỷ lệ có mặt: " + String.format("%.1f%%", 
                                   attendances.size() > 0 ? (presentCount * 100.0 / attendances.size()) : 0));
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! Vui lòng nhập yyyy-mm-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateSubjectReport() {
        String[] columns = {"STT", "Mã môn", "Tên môn", "Mô tả", "Huấn luyện viên"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        List<Subject> subjects = Query.findActiveSubjects();
        if (subjects != null) {
            int index = 1;
            for (Subject subject : subjects) {
                Object[] row = {
                    index++,
                    subject.getSubjId(),
                    subject.getName(),
                    subject.getDesc(),
                    subject.getCoach() != null ? subject.getCoach() : "Chưa có"
                };
                tableModel.addRow(row);
            }
        }

        summaryArea.setText("Tổng số môn tập: " + (subjects != null ? subjects.size() : 0) + "\n" +
                           "Báo cáo được tạo lúc: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
    }

    private void generateScheduleReport() {
        String[] columns = {"STT", "Môn tập", "Thứ", "Giờ bắt đầu", "Giờ kết thúc", "Địa điểm"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        List<Timeline> schedules = Query.findActiveSchedules();
        if (schedules != null) {
            int index = 1;
            for (Timeline timeline : schedules) {
                Subject subject = Query.findById(Subject.class, timeline.getSubjId());
                Object[] row = {
                    index++,
                    subject != null ? subject.getName() : "Unknown",
                    timeline.getWeekDay(),
                    timeline.getStartTime(),
                    timeline.getEndTime(),
                    timeline.getPlace()
                };
                tableModel.addRow(row);
            }
        }

        summaryArea.setText("Tổng số lịch học: " + (schedules != null ? schedules.size() : 0) + "\n" +
                           "Báo cáo được tạo lúc: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
    }

    private void generateOverviewReport() {
        String[] columns = {"Thống kê", "Số lượng", "Chi tiết", "", ""};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        // Get counts
        List<Member> members = Query.findActiveMembers();
        List<Subject> subjects = Query.findActiveSubjects();
        List<Timeline> schedules = Query.findActiveSchedules();
        List<Attendance> attendances = Query.findAllAttendance();

        int memberCount = members != null ? members.size() : 0;
        int subjectCount = subjects != null ? subjects.size() : 0;
        int scheduleCount = schedules != null ? schedules.size() : 0;
        int attendanceCount = attendances != null ? attendances.size() : 0;

        Object[][] data = {
            {"Tổng số thành viên", memberCount, "Thành viên đang hoạt động", "", ""},
            {"Tổng số môn tập", subjectCount, "Môn tập đang mở", "", ""},
            {"Tổng số lịch học", scheduleCount, "Lịch học trong tuần", "", ""},
            {"Tổng số điểm danh", attendanceCount, "Bản ghi điểm danh", "", ""}
        };

        for (Object[] row : data) {
            tableModel.addRow(row);
        }

        summaryArea.setText("=== THỐNG KÊ TỔNG QUAN ===\n" +
                           "Thành viên: " + memberCount + "\n" +
                           "Môn tập: " + subjectCount + "\n" +
                           "Lịch học: " + scheduleCount + "\n" +
                           "Điểm danh: " + attendanceCount + "\n" +
                           "Cập nhật: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
    }

    private void exportReport(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu báo cáo");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        fileChooser.setSelectedFile(new java.io.File("report_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                exportToCSV(fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToCSV(String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath, java.nio.charset.StandardCharsets.UTF_8);
        
        // Write header
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            writer.write(tableModel.getColumnName(i));
            if (i < tableModel.getColumnCount() - 1) writer.write(",");
        }
        writer.write("\n");

        // Write data
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object value = tableModel.getValueAt(i, j);
                writer.write(value != null ? value.toString() : "");
                if (j < tableModel.getColumnCount() - 1) writer.write(",");
            }
            writer.write("\n");
        }

        writer.close();
    }
}