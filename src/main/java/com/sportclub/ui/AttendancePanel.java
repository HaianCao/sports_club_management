package com.sportclub.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.sportclub.database.CRUD.Add;
import com.sportclub.database.CRUD.Delete;
import com.sportclub.database.CRUD.Query;
import com.sportclub.database.models.Attendance;
import com.sportclub.database.models.Member;
import com.sportclub.database.models.Regist;
import com.sportclub.database.models.Subject;
import com.sportclub.database.models.Timeline;

/**
 * Panel for class-based attendance management
 */
public class AttendancePanel extends JPanel {

    private JComboBox<String> subjectCombo;
    private JComboBox<String> weekCombo;
    private JComboBox<String> dayCombo;
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

    /**
     * Public method to refresh subjects and schedules (called when subject/schedule is added/updated/deleted)
     */
    public void refreshAttendance() {
        loadSchedules();
    }

    private void initializeComponents() {
        // Subject combo - để lọc lịch học theo môn
        subjectCombo = new JComboBox<>();
        subjectCombo.addActionListener(e -> {
            loadWeeks();
            tableModel.setRowCount(0);
        });

        // Week combo
        weekCombo = new JComboBox<>();
        weekCombo.addActionListener(e -> {
            loadDaysForWeek();
            tableModel.setRowCount(0);
        });

        // Day combo
        dayCombo = new JComboBox<>();
        dayCombo.addActionListener(e -> {
            loadSchedulesForDay();
            tableModel.setRowCount(0);
        });

        // Schedule combo
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

        // Top panel - Chọn môn tập, tuần, ngày, lịch học
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Chọn môn tập, tuần, ngày để điểm danh"));

        topPanel.add(new JLabel("Môn tập:"));
        topPanel.add(subjectCombo);
        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(new JLabel("Tuần:"));
        topPanel.add(weekCombo);
        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(new JLabel("Ngày:"));
        topPanel.add(dayCombo);
        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(new JLabel("Lớp học:"));
        topPanel.add(scheduleCombo);
        topPanel.add(refreshBtn);
        
        // Second row for date and load button
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        datePanel.add(new JLabel("Ngày (yyyy-mm-dd):"));
        datePanel.add(dateField);
        datePanel.add(loadBtn);

        // Combine top panels
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(topPanel, BorderLayout.NORTH);
        filterPanel.add(datePanel, BorderLayout.SOUTH);

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
        add(filterPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadSchedules() {
        // Load all subjects first
        subjectCombo.removeAllItems();
        subjectCombo.addItem("-- Chọn môn tập --");
        
        try {
            List<Subject> subjects = Query.findActiveSubjects();
            if (subjects != null && !subjects.isEmpty()) {
                for (Subject subject : subjects) {
                    subjectCombo.addItem(subject.getSubjId() + " - " + subject.getName());
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading subjects: " + e.getMessage());
        }

        // Reset schedules and table
        loadSchedulesForSubject();
    }

    private void loadSchedulesForSubject() {
        scheduleCombo.removeAllItems();
        scheduleCombo.addItem("-- Chọn lớp học --");
        tableModel.setRowCount(0);
        selectedTimelineId = -1;

        String selected = (String) subjectCombo.getSelectedItem();
        if (selected == null || selected.startsWith("--")) {
            scheduleCombo.addItem("-- Vui lòng chọn môn tập trước --");
            return;
        }

        try {
            // Extract subject ID
            int subjId = Integer.parseInt(selected.split(" - ")[0]);

            // Get all schedules
            List<Timeline> schedules = Query.findActiveSchedules();
            if (schedules != null && !schedules.isEmpty()) {
                int count = 0;
                for (Timeline timeline : schedules) {
                    // Filter by selected subject
                    if (timeline.getSubjId() == subjId) {
                        Subject subject = Query.findById(Subject.class, timeline.getSubjId());
                        String subjectName = subject != null ? subject.getName() : "Unknown";
                        String item = timeline.getTimelineId() + " - " + subjectName +
                                " (" + timeline.getWeekDay() + " " + timeline.getStartTime() +
                                " - " + timeline.getEndTime() + " tại " + timeline.getPlace() + ")";
                        scheduleCombo.addItem(item);
                        count++;
                    }
                }
                if (count == 0) {
                    scheduleCombo.addItem("-- Môn này không có lịch học nào --");
                }
            } else {
                scheduleCombo.addItem("-- Không có lịch học nào --");
            }
        } catch (Exception e) {
            scheduleCombo.addItem("-- Lỗi khi tải dữ liệu --");
            System.out.println("Error loading schedules for subject: " + e.getMessage());
        }
    }

    /**
     * Get week label with start and end dates (e.g., "Tuần 1 (01/01 - 07/01/2025)")
     */
    private String getWeekLabel(int weekNumber) {
        Calendar cal = Calendar.getInstance();
        // Set to first day of the year
        cal.set(Calendar.DAY_OF_YEAR, 1);
        
        // Calculate the start date of the week (Monday of that week)
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysToMonday = (dayOfWeek == 1) ? 6 : dayOfWeek - 2;
        cal.add(Calendar.DAY_OF_MONTH, -daysToMonday);
        
        // Move to the requested week
        cal.add(Calendar.WEEK_OF_YEAR, weekNumber - 1);
        
        // Start date is Monday
        java.util.Date startDate = cal.getTime();
        SimpleDateFormat weekFormat = new SimpleDateFormat("dd/MM");
        String startDateStr = weekFormat.format(startDate);
        
        // End date is Sunday (6 days later)
        cal.add(Calendar.DAY_OF_MONTH, 6);
        java.util.Date endDate = cal.getTime();
        SimpleDateFormat endFormat = new SimpleDateFormat("dd/MM/yyyy");
        String endDateStr = endFormat.format(endDate);
        
        return "Tuần " + weekNumber + " (" + startDateStr + " - " + endDateStr + ")";
    }

    /**
     * Get day label with specific date (e.g., "Thứ 2 (19/11/2025)")
     */
    private String getDayLabel(String dayOfWeek) {
        // Get the next occurrence of this day of week
        Calendar cal = Calendar.getInstance();
        
        // Map day name to Calendar constant
        int targetDay = mapDayToCalendar(dayOfWeek);
        
        // Find the next occurrence of this day
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);
        int daysAhead = targetDay - currentDay;
        if (daysAhead <= 0) {
            daysAhead += 7;
        }
        
        cal.add(Calendar.DAY_OF_MONTH, daysAhead);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = dateFormat.format(cal.getTime());
        
        return dayOfWeek + " (" + dateStr + ")";
    }

    /**
     * Map Vietnamese day name to Calendar day constant
     */
    private int mapDayToCalendar(String dayName) {
        switch (dayName) {
            case "Thứ 2": return Calendar.MONDAY;
            case "Thứ 3": return Calendar.TUESDAY;
            case "Thứ 4": return Calendar.WEDNESDAY;
            case "Thứ 5": return Calendar.THURSDAY;
            case "Thứ 6": return Calendar.FRIDAY;
            case "Thứ 7": return Calendar.SATURDAY;
            case "Chủ nhật": return Calendar.SUNDAY;
            default: return Calendar.MONDAY;
        }
    }

    private void loadWeeks() {
        weekCombo.removeAllItems();
        weekCombo.addItem("-- Chọn tuần --");
        dayCombo.removeAllItems();
        dayCombo.addItem("-- Chọn ngày --");
        scheduleCombo.removeAllItems();
        scheduleCombo.addItem("-- Chọn lớp học --");
        tableModel.setRowCount(0);
        selectedTimelineId = -1;

        String selected = (String) subjectCombo.getSelectedItem();
        if (selected == null || selected.startsWith("--")) {
            return;
        }

        try {
            int subjId = Integer.parseInt(selected.split(" - ")[0]);
            List<Timeline> schedules = Query.findActiveSchedules();
            
            if (schedules != null && !schedules.isEmpty()) {
                java.util.Set<Integer> weeks = new java.util.TreeSet<>();
                for (Timeline timeline : schedules) {
                    if (timeline.getSubjId() == subjId) {
                        weeks.add(1); // Mặc định tuần 1 - có thể mở rộng sau
                    }
                }
                for (Integer week : weeks) {
                    String weekLabel = getWeekLabel(week);
                    weekCombo.addItem(weekLabel);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading weeks: " + e.getMessage());
        }
    }

    private void loadDaysForWeek() {
        dayCombo.removeAllItems();
        dayCombo.addItem("-- Chọn ngày --");
        scheduleCombo.removeAllItems();
        scheduleCombo.addItem("-- Chọn lớp học --");
        tableModel.setRowCount(0);
        selectedTimelineId = -1;

        String subjectSelected = (String) subjectCombo.getSelectedItem();
        String weekSelected = (String) weekCombo.getSelectedItem();

        if (subjectSelected == null || subjectSelected.startsWith("--") ||
            weekSelected == null || weekSelected.startsWith("--")) {
            return;
        }

        try {
            int subjId = Integer.parseInt(subjectSelected.split(" - ")[0]);
            List<Timeline> schedules = Query.findActiveSchedules();

            if (schedules != null && !schedules.isEmpty()) {
                java.util.Set<String> days = new java.util.LinkedHashSet<>();
                for (Timeline timeline : schedules) {
                    if (timeline.getSubjId() == subjId) {
                        days.add(timeline.getWeekDay());
                    }
                }
                for (String day : days) {
                    String dayLabel = getDayLabel(day);
                    dayCombo.addItem(dayLabel);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading days: " + e.getMessage());
        }
    }

    private void loadSchedulesForDay() {
        scheduleCombo.removeAllItems();
        scheduleCombo.addItem("-- Chọn lớp học --");
        tableModel.setRowCount(0);
        selectedTimelineId = -1;

        String subjectSelected = (String) subjectCombo.getSelectedItem();
        String daySelected = (String) dayCombo.getSelectedItem();

        if (subjectSelected == null || subjectSelected.startsWith("--") ||
            daySelected == null || daySelected.startsWith("--")) {
            scheduleCombo.addItem("-- Vui lòng chọn đầy đủ thông tin --");
            return;
        }

        try {
            int subjId = Integer.parseInt(subjectSelected.split(" - ")[0]);
            List<Timeline> schedules = Query.findActiveSchedules();

            if (schedules != null && !schedules.isEmpty()) {
                // Filter schedules by subject and day
                java.util.List<Timeline> filteredSchedules = new java.util.ArrayList<>();
                for (Timeline timeline : schedules) {
                    if (timeline.getSubjId() == subjId && isMatchingDay(daySelected, timeline.getWeekDay())) {
                        filteredSchedules.add(timeline);
                    }
                }
                
                // Sort by start time
                filteredSchedules.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));

                if (!filteredSchedules.isEmpty()) {
                    // Add classes without headers - just sorted by time
                    for (Timeline timeline : filteredSchedules) {
                        String item = timeline.getTimelineId() + " - " + timeline.getStartTime() +
                                " - " + timeline.getEndTime() + " (" + timeline.getPlace() + ")";
                        scheduleCombo.addItem(item);
                    }
                } else {
                    scheduleCombo.addItem("-- Không có lớp học vào " + daySelected + " --");
                }
            } else {
                scheduleCombo.addItem("-- Không có lịch học nào --");
            }
        } catch (Exception e) {
            scheduleCombo.addItem("-- Lỗi khi tải dữ liệu --");
            System.out.println("Error loading schedules for day: " + e.getMessage());
        }
    }

    /**
     * Check if day selected matches timeline day (extract day name from daySelected which includes date)
     */
    private boolean isMatchingDay(String daySelected, String timelineDay) {
        // daySelected is like "Thứ 2 (19/11/2025)", extract the day name part
        if (daySelected.contains("(")) {
            String dayName = daySelected.substring(0, daySelected.indexOf("(")).trim();
            return dayName.equals(timelineDay);
        }
        return daySelected.equals(timelineDay);
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