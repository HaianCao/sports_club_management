package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.sql.Time;

/**
 * Panel for managing weekly schedules with class details and member management
 */
public class ScheduleManagementPanel extends JPanel {

    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> subjectCombo, weekDayCombo;
    private JTextField startTimeField, endTimeField, placeField;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn;
    private int selectedTimelineId = -1;
    private AttendancePanel attendancePanelRef; // optional ref to refresh attendance

    public ScheduleManagementPanel() {
        this(null);
    }

    /**
     * Construct with optional reference to attendance panel
     */
    public ScheduleManagementPanel(AttendancePanel attendancePanel) {
        this.attendancePanelRef = attendancePanel;
        initializeComponents();
        setupLayout();
        loadSchedules();
        loadSubjects();
    }

    private void initializeComponents() {
        // Table setup
        String[] columns = { "ID", "Môn tập", "Thứ", "Giờ bắt đầu", "Giờ kết thúc", "Địa điểm" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(tableModel);
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedSchedule();
            }
        });

        // Form fields
        subjectCombo = new JComboBox<>();
        weekDayCombo = new JComboBox<>(new String[] {
                "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"
        });

        startTimeField = new JTextField(8);
        endTimeField = new JTextField(8);
        placeField = new JTextField(20);

        // Buttons
        addBtn = new JButton("Thêm lịch");
        addBtn.setPreferredSize(new Dimension(100, 35));

        updateBtn = new JButton("Cập nhật");
        updateBtn.setPreferredSize(new Dimension(100, 35));
        updateBtn.setEnabled(false);

        deleteBtn = new JButton("Xóa");
        deleteBtn.setPreferredSize(new Dimension(100, 35));
        deleteBtn.setEnabled(false);

        refreshBtn = new JButton("Làm mới");
        refreshBtn.setPreferredSize(new Dimension(100, 35));

        // Action listeners
        addBtn.addActionListener(this::addSchedule);
        updateBtn.addActionListener(this::updateSchedule);
        deleteBtn.addActionListener(this::deleteSchedule);
        refreshBtn.addActionListener(e -> {
            loadSchedules();
            loadSubjects();
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin lịch học"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Môn tập:"), gbc);
        gbc.gridx = 1;
        formPanel.add(subjectCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Thứ:"), gbc);
        gbc.gridx = 3;
        formPanel.add(weekDayCombo, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Giờ bắt đầu:"), gbc);
        gbc.gridx = 1;
        formPanel.add(startTimeField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Giờ kết thúc:"), gbc);
        gbc.gridx = 3;
        formPanel.add(endTimeField, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Địa điểm:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        formPanel.add(placeField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(scheduleTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách lịch học"));

        // Layout
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addSchedule(ActionEvent e) {
        try {
            String subjectText = (String) subjectCombo.getSelectedItem();
            if (subjectText == null || subjectText.startsWith("--")) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int subjId = Integer.parseInt(subjectText.split(" - ")[0]);
            String weekDay = (String) weekDayCombo.getSelectedItem();

            String startTimeText = startTimeField.getText().trim();
            String endTimeText = endTimeField.getText().trim();
            String place = placeField.getText().trim();

            if (startTimeText.isEmpty() || endTimeText.isEmpty() || place.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse time with format validation
            Time startTime = parseTimeInput(startTimeText);
            Time endTime = parseTimeInput(endTimeText);

            if (startTime == null || endTime == null) {
                JOptionPane.showMessageDialog(this,
                        "Định dạng thời gian không hợp lệ! Vui lòng nhập theo định dạng HH:mm", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (startTime.after(endTime)) {
                JOptionPane.showMessageDialog(this, "Giờ bắt đầu phải nhỏ hơn giờ kết thúc!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Add.addTimeline(subjId, weekDay, startTime, endTime, place);

            JOptionPane.showMessageDialog(this, "Thêm lịch học thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadSchedules();
            if (attendancePanelRef != null) attendancePanelRef.refreshAttendance();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSchedule(ActionEvent e) {
        if (selectedTimelineId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch học cần cập nhật!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String subjectText = (String) subjectCombo.getSelectedItem();
            if (subjectText == null || subjectText.startsWith("--")) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int subjId = Integer.parseInt(subjectText.split(" - ")[0]);
            String weekDay = (String) weekDayCombo.getSelectedItem();

            String startTimeText = startTimeField.getText().trim();
            String endTimeText = endTimeField.getText().trim();
            String place = placeField.getText().trim();

            if (startTimeText.isEmpty() || endTimeText.isEmpty() || place.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Time startTime = parseTimeInput(startTimeText);
            Time endTime = parseTimeInput(endTimeText);

            if (startTime == null || endTime == null) {
                JOptionPane.showMessageDialog(this,
                        "Định dạng thời gian không hợp lệ! Vui lòng nhập theo định dạng HH:mm", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (startTime.after(endTime)) {
                JOptionPane.showMessageDialog(this, "Giờ bắt đầu phải nhỏ hơn giờ kết thúc!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Timeline timeline = Query.findById(Timeline.class, selectedTimelineId);
            if (timeline != null) {
                timeline.setSubjId(subjId);
                timeline.setWeekDay(weekDay);
                timeline.setStartTime(startTime);
                timeline.setEndTime(endTime);
                timeline.setPlace(place);

                Update.updateTimeline(selectedTimelineId, weekDay, startTime, endTime, place);

                JOptionPane.showMessageDialog(this, "Cập nhật lịch học thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSchedules();
                if (attendancePanelRef != null) attendancePanelRef.refreshAttendance();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSchedule(ActionEvent e) {
        if (selectedTimelineId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch học cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa lịch học này?\nViệc xóa sẽ ảnh hưởng đến dữ liệu điểm danh liên quan.",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                Delete.deleteTimeline(selectedTimelineId);
                JOptionPane.showMessageDialog(this, "Xóa lịch học thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSchedules();
                if (attendancePanelRef != null) attendancePanelRef.refreshAttendance();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xóa: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadSelectedSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedTimelineId = (Integer) tableModel.getValueAt(selectedRow, 0);

            Timeline timeline = Query.findById(Timeline.class, selectedTimelineId);
            if (timeline != null) {
                // Set form fields
                setSubjectComboSelection(timeline.getSubjId());
                weekDayCombo.setSelectedItem(timeline.getWeekDay());
                startTimeField.setText(formatTime(timeline.getStartTime()));
                endTimeField.setText(formatTime(timeline.getEndTime()));
                placeField.setText(timeline.getPlace());

                updateBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
            }
        } else {
            clearForm();
        }
    }

    private void setSubjectComboSelection(int subjId) {
        for (int i = 0; i < subjectCombo.getItemCount(); i++) {
            String item = subjectCombo.getItemAt(i);
            if (item.startsWith(subjId + " - ")) {
                subjectCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        selectedTimelineId = -1;
        subjectCombo.setSelectedIndex(0);
        weekDayCombo.setSelectedIndex(0);
        startTimeField.setText("");
        endTimeField.setText("");
        placeField.setText("");

        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }

    private void loadSchedules() {
        tableModel.setRowCount(0);

        List<Timeline> schedules = Query.findActiveSchedules();
        if (schedules != null) {
            for (Timeline timeline : schedules) {
                Subject subject = Query.findById(Subject.class, timeline.getSubjId());
                String subjectName = subject != null ? subject.getName() : "Unknown";

                Object[] row = {
                        timeline.getTimelineId(),
                        subjectName,
                        timeline.getWeekDay(),
                        formatTime(timeline.getStartTime()),
                        formatTime(timeline.getEndTime()),
                        timeline.getPlace()
                };
                tableModel.addRow(row);
            }
        }
    }

    public void loadSubjects() {
        subjectCombo.removeAllItems();
        subjectCombo.addItem("-- Chọn môn tập --");

        List<Subject> subjects = Query.findActiveSubjects();
        if (subjects != null) {
            for (Subject subject : subjects) {
                subjectCombo.addItem(subject.getSubjId() + " - " + subject.getName());
            }
        }
    }

    private String formatTime(Time time) {
        if (time == null)
            return "";
        return time.toString().substring(0, 5); // HH:mm
    }

    private Time parseTimeInput(String timeText) {
        try {
            // Accept both HH:mm and HH:mm:ss formats
            if (timeText.matches("\\d{1,2}:\\d{2}")) {
                timeText += ":00"; // Add seconds
            }
            return Time.valueOf(timeText);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}