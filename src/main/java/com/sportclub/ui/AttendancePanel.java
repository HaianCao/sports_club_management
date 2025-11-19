package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;
import com.sportclub.util.CSVExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.text.SimpleDateFormat;

public class AttendancePanel extends JPanel {

    private JComboBox<String> scheduleCombo;
    private JComboBox<String> sessionCombo;
    private JTextField dateField;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JButton saveBtn, markAllBtn, unmarkAllBtn, refreshBtn, clearTableBtn, createListBtn, exportCSVBtn;
    private int selectedTimelineId = -1;
    private java.sql.Date selectedDate = null;

    public AttendancePanel() {
        initializeComponents();
        setupLayout();

        loadSchedules();
    }

    private void initializeComponents() {
        scheduleCombo = new JComboBox<>();
        scheduleCombo.addActionListener(e -> {
            loadSelectedSchedule();
            loadSessionsForSchedule();
            resetButtons();
            tableModel.setRowCount(0);
        });

        sessionCombo = new JComboBox<>();
        sessionCombo.setEnabled(false);
        sessionCombo.addActionListener(e -> {
            loadSelectedSession();
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField = new JTextField(sdf.format(new java.util.Date()), 10);

        String[] columns = { "Mã TV", "Tên thành viên", "Số điện thoại", "Có mặt", "Ghi chú" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(25);

        TableColumn col0 = attendanceTable.getColumnModel().getColumn(0);
        col0.setPreferredWidth(60);
        col0.setMaxWidth(60);

        TableColumn col1 = attendanceTable.getColumnModel().getColumn(1);
        col1.setPreferredWidth(150);

        TableColumn col2 = attendanceTable.getColumnModel().getColumn(2);
        col2.setPreferredWidth(100);

        TableColumn col3 = attendanceTable.getColumnModel().getColumn(3);
        col3.setPreferredWidth(60);
        col3.setMaxWidth(60);

        TableColumn col4 = attendanceTable.getColumnModel().getColumn(4);
        col4.setPreferredWidth(120);

        saveBtn = new JButton("Lưu điểm danh");
        saveBtn.setPreferredSize(new Dimension(130, 35));
        saveBtn.addActionListener(this::saveAttendance);
        saveBtn.setEnabled(false);

        markAllBtn = new JButton("Chọn tất cả");
        markAllBtn.setPreferredSize(new Dimension(110, 35));
        markAllBtn.addActionListener(this::markAll);
        markAllBtn.setEnabled(false);

        unmarkAllBtn = new JButton("Bỏ chọn tất cả");
        unmarkAllBtn.setPreferredSize(new Dimension(130, 35));
        unmarkAllBtn.addActionListener(this::unmarkAll);
        unmarkAllBtn.setEnabled(false);

        refreshBtn = new JButton("Làm mới lớp học");
        refreshBtn.setPreferredSize(new Dimension(150, 35));
        refreshBtn.addActionListener(e -> loadSchedules());

        clearTableBtn = new JButton("Làm mới danh sách");
        clearTableBtn.setPreferredSize(new Dimension(160, 35));
        clearTableBtn.addActionListener(this::refreshAttendance);
        clearTableBtn.setEnabled(false);

        createListBtn = new JButton("Tạo danh sách mới");
        createListBtn.setPreferredSize(new Dimension(150, 35));
        createListBtn.addActionListener(this::createNewAttendanceList);
        createListBtn.setEnabled(false);

        exportCSVBtn = new JButton("Xuất CSV");
        exportCSVBtn.setPreferredSize(new Dimension(100, 35));
        exportCSVBtn.addActionListener(e -> exportToCSV());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Quản lý điểm danh"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Lịch tập:"));
        row1.add(scheduleCombo);
        row1.add(refreshBtn);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Buổi học đã diễn ra:"));
        row2.add(sessionCombo);
        row2.add(Box.createHorizontalStrut(20));
        row2.add(clearTableBtn);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Hoặc tạo mới cho ngày (yyyy-mm-dd):"));
        row3.add(dateField);
        row3.add(createListBtn);

        JPanel topContent = new JPanel(new BorderLayout());
        topContent.add(row1, BorderLayout.NORTH);
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.add(row2, BorderLayout.NORTH);
        middlePanel.add(row3, BorderLayout.SOUTH);
        topContent.add(middlePanel, BorderLayout.CENTER);

        topPanel.add(topContent, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Danh sách thành viên"));
        centerPanel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(markAllBtn);
        bottomPanel.add(unmarkAllBtn);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(saveBtn);
        bottomPanel.add(exportCSVBtn);

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

        selectedTimelineId = -1;
        tableModel.setRowCount(0);
    }

    private void resetButtons() {
        saveBtn.setEnabled(false);
        markAllBtn.setEnabled(false);
        unmarkAllBtn.setEnabled(false);
        clearTableBtn.setEnabled(false);
    }

    private void updateCreateButtonState() {
        createListBtn.setEnabled(selectedTimelineId != -1);
    }

    private void loadSessionsForSchedule() {
        sessionCombo.removeAllItems();
        sessionCombo.setEnabled(false);

        if (selectedTimelineId == -1) {
            sessionCombo.addItem("-- Chọn lịch tập trước --");
            return;
        }

        try {
            List<Attendance> attendanceList = Query.findAttendanceByTimeline(selectedTimelineId);
            if (attendanceList != null && !attendanceList.isEmpty()) {
                sessionCombo.addItem("-- Chọn buổi học --");

                java.util.Set<java.sql.Date> uniqueDates = new java.util.HashSet<>();
                for (Attendance att : attendanceList) {
                    uniqueDates.add(att.getAttendDate());
                }

                java.util.List<java.sql.Date> sortedDates = new java.util.ArrayList<>(uniqueDates);
                sortedDates.sort((d1, d2) -> d2.compareTo(d1));

                for (java.sql.Date date : sortedDates) {
                    sessionCombo.addItem(date.toString());
                }

                sessionCombo.setEnabled(true);
            } else {
                sessionCombo.addItem("-- Chưa có buổi học nào --");
            }
        } catch (Exception e) {
            sessionCombo.addItem("-- Lỗi khi tải dữ liệu --");
            e.printStackTrace();
        }
    }

    private void loadSelectedSession() {
        String selected = (String) sessionCombo.getSelectedItem();
        if (selected != null && !selected.startsWith("--")) {
            try {
                selectedDate = java.sql.Date.valueOf(selected);
                loadAttendanceForSelectedSession();
            } catch (Exception e) {
                selectedDate = null;
                resetButtons();
                tableModel.setRowCount(0);
                e.printStackTrace();
            }
        } else {
            selectedDate = null;
            resetButtons();
            tableModel.setRowCount(0);
        }
    }

    private void loadAttendanceForSelectedSession() {
        if (selectedTimelineId == -1 || selectedDate == null) {
            return;
        }

        tableModel.setRowCount(0);

        try {
            List<Attendance> existingAttendance = Query.findAttendanceByTimelineAndDate(selectedTimelineId,
                    selectedDate);

            if (existingAttendance != null && !existingAttendance.isEmpty()) {
                for (Attendance att : existingAttendance) {
                    Member member = Query.findMemberById(att.getMemId());
                    if (member != null) {
                        boolean isPresent = "Có mặt".equals(att.getStatus());
                        String notes = att.getNotes() != null ? att.getNotes() : "";

                        Object[] row = {
                                member.getMemId(),
                                member.getName(),
                                member.getPhone(),
                                isPresent,
                                notes
                        };
                        tableModel.addRow(row);
                    }
                }

                saveBtn.setEnabled(true);
                markAllBtn.setEnabled(true);
                unmarkAllBtn.setEnabled(true);
                clearTableBtn.setEnabled(true);

                Timeline timeline = Query.findById(Timeline.class, selectedTimelineId);
                Subject subject = Query.findById(Subject.class, timeline.getSubjId());

                JOptionPane.showMessageDialog(this,
                        String.format("Đã tải danh sách điểm danh!\n\nMôn: %s\nNgày: %s\nTổng số thành viên: %d",
                                subject.getName(),
                                selectedDate.toString(),
                                tableModel.getRowCount()),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu điểm danh cho ngày này!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi khi tải danh sách: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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
        updateCreateButtonState();
    }

    private void refreshAttendance(ActionEvent e) {
        if (selectedTimelineId != -1 && selectedDate != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Làm mới danh sách điểm danh cho ngày " + selectedDate + "?",
                    "Xác nhận làm mới",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                loadAttendanceForSelectedSession();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch tập và buổi học!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private int getSubjectIdFromTimeline() {
        if (selectedTimelineId == -1)
            return -1;
        Timeline timeline = Query.findById(Timeline.class, selectedTimelineId);
        return timeline != null ? timeline.getSubjId() : -1;
    }

    private void saveAttendance(ActionEvent e) {
        if (selectedTimelineId == -1 || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch học và buổi học!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<Attendance> existingAttendance = Query.findAttendanceByTimelineAndDate(selectedTimelineId,
                    selectedDate);
            for (Attendance att : existingAttendance) {
                Delete.deleteAttendance(att.getAttendId());
            }

            int savedCount = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int memId = (Integer) tableModel.getValueAt(i, 0);
                Boolean isPresent = (Boolean) tableModel.getValueAt(i, 3);
                String notes = tableModel.getValueAt(i, 4).toString();

                if (isPresent != null && isPresent) {
                    Add.addAttendance(memId, selectedTimelineId, selectedDate, "Có mặt", notes);
                    savedCount++;
                } else {
                    Add.addAttendance(memId, selectedTimelineId, selectedDate, "Vắng mặt", notes);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Đã lưu điểm danh thành công!\n" +
                            "Có mặt: " + savedCount + "/" + tableModel.getRowCount() + " thành viên",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

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

    private void createNewAttendanceList(ActionEvent e) {
        if (selectedTimelineId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch tập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            java.sql.Date attendanceDate = java.sql.Date.valueOf(dateField.getText().trim());
            Timeline timeline = Query.findById(Timeline.class, selectedTimelineId);
            Subject subject = Query.findById(Subject.class, timeline.getSubjId());

            List<Attendance> existingAttendance = Query.findAttendanceByTimelineAndDate(selectedTimelineId,
                    attendanceDate);
            String warningText = "";
            if (existingAttendance != null && !existingAttendance.isEmpty()) {
                warningText = "\n\n⚠️ CẢNH BÁO: Ngày này đã có điểm danh. Tạo mới sẽ ghi đè dữ liệu cũ!";
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format(
                            "Tạo danh sách điểm danh cho:\n\nMôn: %s\nLịch: %s %s-%s tại %s\nNgày: %s%s\n\nXác nhận tạo danh sách?",
                            subject.getName(),
                            timeline.getWeekDay(),
                            timeline.getStartTime(),
                            timeline.getEndTime(),
                            timeline.getPlace(),
                            attendanceDate,
                            warningText),
                    "Xác nhận tạo danh sách điểm danh",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                createAttendanceListForDate(attendanceDate);
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! Vui lòng nhập yyyy-mm-dd", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAttendanceListForDate(java.sql.Date attendanceDate) {
        tableModel.setRowCount(0);

        try {
            List<Regist> registrations = Query.findRegistrationsBySubject(getSubjectIdFromTimeline());
            if (registrations == null || registrations.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có thành viên nào đăng ký môn tập này!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            List<Attendance> existingAttendance = Query.findAttendanceByTimelineAndDate(selectedTimelineId,
                    attendanceDate);

            for (Regist regist : registrations) {
                Member member = Query.findMemberById(regist.getMemId());
                if (member != null) {
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
                            isPresent,
                            notes
                    };
                    tableModel.addRow(row);
                }
            }

            if (existingAttendance.isEmpty()) {
                for (Regist regist : registrations) {
                    Member member = Query.findMemberById(regist.getMemId());
                    if (member != null) {
                        Add.addAttendance(member.getMemId(), selectedTimelineId, attendanceDate, "Vắng mặt", "");
                    }
                }
            }

            selectedDate = attendanceDate;
            saveBtn.setEnabled(true);
            markAllBtn.setEnabled(true);
            unmarkAllBtn.setEnabled(true);
            clearTableBtn.setEnabled(true);

            loadSessionsForSchedule();
            sessionCombo.setSelectedItem(attendanceDate.toString());

            JOptionPane.showMessageDialog(this,
                    String.format("Đã tạo danh sách điểm danh thành công!\nNgày: %s\nTổng số thành viên: %d",
                            attendanceDate, tableModel.getRowCount()),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi khi tạo danh sách: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportToCSV() {
        if (attendanceTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String filename = "danh_sach_diem_danh_" + (selectedDate != null ? selectedDate.toString() : "all");
        CSVExporter.exportTableToCSV(attendanceTable, filename);
    }
}