package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Panel for class-based attendance management
 */
public class AttendancePanel extends JPanel {

    private JComboBox<String> scheduleCombo;
    private JTextField dateField;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JButton loadBtn, saveBtn, markAllBtn, unmarkAllBtn, refreshBtn;
    private int selectedTimelineId = -1;

    public AttendancePanel() {
        initializeComponents();
        setupLayout();

        // Load dữ liệu ban đầu
        loadSchedules();

        // Hiển thị hướng dẫn nếu không có lịch học
        SwingUtilities.invokeLater(() -> {
            if (scheduleCombo.getItemCount() <= 2) { // <= 2 vì có "Chọn lớp học" và có thể có "Không có lịch học nào"
                JOptionPane.showMessageDialog(this,
                        "Chưa có lịch học nào được tạo!\n\nVui lòng:\n1. Vào 'Quản lý Môn tập' để tạo môn tập\n2. Vào 'Quản lý Lịch tập' để tạo lịch học\n3. Quay lại đây để điểm danh\n\nHoặc nhấn 'Làm mới lớp học' để tải lại danh sách.",
                        "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void initializeComponents() {
        // Controls
        scheduleCombo = new JComboBox<>();
        scheduleCombo.addActionListener(e -> {
            loadSelectedSchedule();
            loadAttendanceData();
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField = new JTextField(sdf.format(new java.util.Date()), 10);

        // Table setup - với checkbox column
        String[] columns = { "Mã TV", "Tên thành viên", "Số điện thoại", "Có mặt", "Ghi chú" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) { // "Có mặt" column
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho phép edit cột "Có mặt" và "Ghi chú"
                return column == 3 || column == 4;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(25);

        // Set column widths
        TableColumn col0 = attendanceTable.getColumnModel().getColumn(0); // Mã TV
        col0.setPreferredWidth(60);
        col0.setMaxWidth(60);

        TableColumn col1 = attendanceTable.getColumnModel().getColumn(1); // Tên
        col1.setPreferredWidth(150);

        TableColumn col2 = attendanceTable.getColumnModel().getColumn(2); // SĐT
        col2.setPreferredWidth(100);

        TableColumn col3 = attendanceTable.getColumnModel().getColumn(3); // Có mặt
        col3.setPreferredWidth(60);
        col3.setMaxWidth(60);

        TableColumn col4 = attendanceTable.getColumnModel().getColumn(4); // Ghi chú
        col4.setPreferredWidth(120);

        // Buttons
        loadBtn = new JButton("Tải danh sách");
        loadBtn.addActionListener(this::loadAttendanceData);

        saveBtn = new JButton("Lưu điểm danh");
        saveBtn.addActionListener(this::saveAttendance);

        markAllBtn = new JButton("Chọn tất cả");
        markAllBtn.addActionListener(this::markAll);

        unmarkAllBtn = new JButton("Bỏ chọn tất cả");
        unmarkAllBtn.addActionListener(this::unmarkAll);

        refreshBtn = new JButton("Làm mới lớp học");
        refreshBtn.addActionListener(e -> loadSchedules());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel - Chọn lớp học và ngày
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Chọn lớp học để điểm danh"));

        topPanel.add(new JLabel("Lớp học:"));
        topPanel.add(scheduleCombo);
        topPanel.add(refreshBtn);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Ngày (yyyy-mm-dd):"));
        topPanel.add(dateField);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(loadBtn);

        // Center panel - Bảng điểm danh
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Danh sách thành viên"));
        centerPanel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        // Bottom panel - Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(markAllBtn);
        bottomPanel.add(unmarkAllBtn);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(saveBtn);

        // Main layout
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadSchedules() {
        scheduleCombo.removeAllItems();
        scheduleCombo.addItem("-- Chọn lớp học --");

        try {
            List<Timeline> schedules = Query.findActiveSchedules();
            if (schedules != null && !schedules.isEmpty()) {
                int count = 0;
                for (Timeline timeline : schedules) {
                    Subject subject = Query.findById(Subject.class, timeline.getSubjId());
                    String subjectName = subject != null ? subject.getName() : "Unknown";
                    String item = timeline.getTimelineId() + " - " + subjectName +
                            " (" + timeline.getWeekDay() + " " + timeline.getStartTime() +
                            " - " + timeline.getEndTime() + " tại " + timeline.getPlace() + ")";
                    scheduleCombo.addItem(item);
                    count++;
                }
                System.out.println("Loaded " + count + " schedules successfully");
            } else {
                scheduleCombo.addItem("-- Không có lịch học nào --");
                System.out.println("No schedules found in database");
            }
        } catch (Exception e) {
            scheduleCombo.addItem("-- Lỗi khi tải dữ liệu --");
            System.out.println("Error loading schedules: " + e.getMessage());
            e.printStackTrace();
        }

        // Reset selection
        selectedTimelineId = -1;
        tableModel.setRowCount(0);
    }

    private void loadSelectedSchedule() {
        String selected = (String) scheduleCombo.getSelectedItem();
        if (selected != null && !selected.startsWith("--") && selected.contains(" - ")) {
            try {
                selectedTimelineId = Integer.parseInt(selected.split(" - ")[0]);
                System.out.println("Selected timeline ID: " + selectedTimelineId);
            } catch (NumberFormatException e) {
                selectedTimelineId = -1;
                System.out.println("Error parsing timeline ID from: " + selected);
            }
        } else {
            selectedTimelineId = -1;
            System.out.println("No valid schedule selected: " + selected);
        }
    }

    private void loadAttendanceData(ActionEvent e) {
        // Kiểm tra khi người dùng nhấn nút
        if (selectedTimelineId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        loadAttendanceData();
    }

    private void loadAttendanceData() {
        tableModel.setRowCount(0);

        if (selectedTimelineId == -1) {
            // Không hiện thông báo lỗi, chỉ return im lặng
            return;
        }

        try {
            Date attendanceDate = Date.valueOf(dateField.getText().trim());

            // Lấy tất cả thành viên đăng ký lớp này
            List<Regist> registrations = Query.findRegistrationsBySubject(getSubjectIdFromTimeline());
            if (registrations == null || registrations.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có thành viên nào đăng ký lớp học này!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Lấy dữ liệu điểm danh có sẵn cho ngày này
            List<Attendance> existingAttendance = Query.findAttendanceByTimelineAndDate(selectedTimelineId,
                    attendanceDate);

            for (Regist regist : registrations) {
                Member member = Query.findById(Member.class, regist.getMemId());
                if (member != null) {
                    // Kiểm tra xem thành viên này đã được điểm danh chưa
                    boolean isPresent = false;
                    String notes = "";

                    for (Attendance att : existingAttendance) {
                        if (att.getMemId() == member.getMemId()) {
                            isPresent = "Có mặt".equals(att.getStatus());
                            notes = att.getNotes() != null ? att.getNotes() : "";
                            break;
                        }
                    }

                    Object[] row = {
                            member.getMemId(),
                            member.getName(),
                            member.getPhone(),
                            isPresent, // Boolean checkbox
                            notes
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! Vui lòng nhập yyyy-mm-dd", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getSubjectIdFromTimeline() {
        if (selectedTimelineId == -1)
            return -1;
        Timeline timeline = Query.findById(Timeline.class, selectedTimelineId);
        return timeline != null ? timeline.getSubjId() : -1;
    }

    private void saveAttendance(ActionEvent e) {
        if (selectedTimelineId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Date attendanceDate = Date.valueOf(dateField.getText().trim());

            // Xóa tất cả điểm danh cũ cho lớp này trong ngày
            List<Attendance> existingAttendance = Query.findAttendanceByTimelineAndDate(selectedTimelineId,
                    attendanceDate);
            for (Attendance att : existingAttendance) {
                Delete.deleteAttendance(att.getAttendId());
            }

            // Lưu điểm danh mới
            int savedCount = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int memId = (Integer) tableModel.getValueAt(i, 0);
                Boolean isPresent = (Boolean) tableModel.getValueAt(i, 3);
                String notes = tableModel.getValueAt(i, 4).toString();

                if (isPresent != null && isPresent) {
                    Add.addAttendance(memId, selectedTimelineId, attendanceDate, "Có mặt", notes);
                    savedCount++;
                } else {
                    Add.addAttendance(memId, selectedTimelineId, attendanceDate, "Vắng mặt", notes);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Đã lưu điểm danh thành công!\n" +
                            "Có mặt: " + savedCount + "/" + tableModel.getRowCount() + " thành viên",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! Vui lòng nhập yyyy-mm-dd", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAll(ActionEvent e) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(true, i, 3);
        }
    }

    private void unmarkAll(ActionEvent e) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(false, i, 3);
        }
    }
}