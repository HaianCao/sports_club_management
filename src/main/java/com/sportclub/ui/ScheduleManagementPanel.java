package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.Timeline;
import com.sportclub.database.models.Subject;
import com.sportclub.util.TimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel for managing training schedules
 */
public class ScheduleManagementPanel extends JPanel {

    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JTextField startDateField, startTimeField, endDateField, endTimeField;
    private JComboBox<String> subjectComboBox;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn;
    private int selectedTimelineId = -1;
    private List<Subject> subjects;

    public ScheduleManagementPanel() {
        initializeComponents();
        setupLayout();
        loadSchedules();
    }

    private void initializeComponents() {
        // Load subjects first
        loadSubjects();

        // Table setup
        String[] columns = { "ID", "Môn tập", "Ngày bắt đầu", "Giờ bắt đầu", "Ngày kết thúc", "Giờ kết thúc",
                "Trạng thái" };
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

        // Subject ComboBox
        subjectComboBox = new JComboBox<>();
        updateSubjectComboBox();

        // Form fields
        startDateField = new JTextField("2025-01-01", 10);
        startTimeField = new JTextField("08:00:00", 10);
        endDateField = new JTextField("2025-01-01", 10);
        endTimeField = new JTextField("09:00:00", 10);

        // Buttons
        addBtn = new JButton("Thêm mới");
        updateBtn = new JButton("Cập nhật");
        deleteBtn = new JButton("Xóa");
        refreshBtn = new JButton("Làm mới");

        // Button styling
        styleButton(addBtn, new Color(92, 184, 92));
        styleButton(updateBtn, new Color(240, 173, 78));
        styleButton(deleteBtn, new Color(217, 83, 79));
        styleButton(refreshBtn, new Color(91, 192, 222));

        // Button actions
        addBtn.addActionListener(this::addSchedule);
        updateBtn.addActionListener(this::updateSchedule);
        deleteBtn.addActionListener(this::deleteSchedule);
        refreshBtn.addActionListener(e -> {
            clearForm();
            loadSchedules();
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("QUẢN LÝ LỊCH TẬP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Main content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createFormPanel());
        splitPane.setDividerLocation(600);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách lịch tập"));

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin lịch tập"));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Subject Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Môn tập:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(subjectComboBox, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        // Start Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1;
        panel.add(startDateField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(yyyy-MM-dd)"), gbc);

        // Start Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Giờ bắt đầu:"), gbc);
        gbc.gridx = 1;
        panel.add(startTimeField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(HH:mm:ss)"), gbc);

        // End Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 1;
        panel.add(endDateField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(yyyy-MM-dd)"), gbc);

        // End Time
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Giờ kết thúc:"), gbc);
        gbc.gridx = 1;
        panel.add(endTimeField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(HH:mm:ss)"), gbc);

        // Current time info
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        JLabel currentTimeLabel = new JLabel("Thời gian hiện tại (GMT+7): " +
                TimeUtil.formatTimestamp(TimeUtil.getCurrentTimestamp()));
        currentTimeLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        currentTimeLabel.setForeground(Color.GRAY);
        panel.add(currentTimeLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));
        button.setMargin(new java.awt.Insets(5, 10, 5, 10));
    }

    private void loadSchedules() {
        tableModel.setRowCount(0);
        try {
            List<Timeline> timelines = Query.findAll(Timeline.class);
            for (Timeline timeline : timelines) {
                if (timeline.getStart() != null && timeline.getEnd() != null) {
                    String startDate = timeline.getStart().toString().split(" ")[0];
                    String startTime = timeline.getStart().toString().split(" ")[1];
                    String endDate = timeline.getEnd().toString().split(" ")[0];
                    String endTime = timeline.getEnd().toString().split(" ")[1];

                    // Get subject name
                    String subjectName = "Chưa chọn";
                    if (timeline.getSubjectId() > 0) {
                        for (Subject subject : subjects) {
                            if (subject.getId() == timeline.getSubjectId()) {
                                subjectName = subject.getName();
                                break;
                            }
                        }
                    }

                    Object[] row = {
                            timeline.getTimeId(),
                            subjectName,
                            startDate,
                            startTime,
                            endDate,
                            endTime,
                            timeline.isDeleted() ? "Đã xóa" : "Hoạt động"
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void loadSelectedSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedTimelineId = (Integer) tableModel.getValueAt(selectedRow, 0);
            String subjectName = (String) tableModel.getValueAt(selectedRow, 1);
            startDateField.setText((String) tableModel.getValueAt(selectedRow, 2));
            startTimeField.setText((String) tableModel.getValueAt(selectedRow, 3));
            endDateField.setText((String) tableModel.getValueAt(selectedRow, 4));
            endTimeField.setText((String) tableModel.getValueAt(selectedRow, 5));

            // Set selected subject in combo box
            for (int i = 0; i < subjectComboBox.getItemCount(); i++) {
                if (subjectComboBox.getItemAt(i).contains(subjectName)) {
                    subjectComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void addSchedule(ActionEvent e) {
        try {
            if (validateForm()) {
                String startDateTime = startDateField.getText() + " " + startTimeField.getText();
                String endDateTime = endDateField.getText() + " " + endTimeField.getText();

                Timestamp startTimestamp = TimeUtil.createTimestamp(startDateTime);
                Timestamp endTimestamp = TimeUtil.createTimestamp(endDateTime);

                int subjectId = getSelectedSubjectId();

                if (subjectId > 0) {
                    Add.addTimeline(startTimestamp, endTimestamp, subjectId);
                } else {
                    Add.addTimeline(startTimestamp, endTimestamp);
                }

                JOptionPane.showMessageDialog(this, "Thêm lịch tập thành công!");
                clearForm();
                loadSchedules();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm lịch tập: " + ex.getMessage());
        }
    }

    private void updateSchedule(ActionEvent e) {
        try {
            if (selectedTimelineId != -1 && validateForm()) {
                // Note: Current Update class doesn't have timeline update method
                // This is a placeholder for future implementation
                JOptionPane.showMessageDialog(this,
                        "Chức năng cập nhật lịch tập sẽ được bổ sung trong phiên bản sau!");
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch tập để cập nhật!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage());
        }
    }

    private void deleteSchedule(ActionEvent e) {
        try {
            if (selectedTimelineId != -1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn xóa lịch tập này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Note: Current Delete class doesn't have timeline delete method
                    // This is a placeholder for future implementation
                    Timeline timeline = Query.findById(Timeline.class, selectedTimelineId);
                    if (timeline != null) {
                        timeline.setDeleted(true);
                        CRUDManager.update(timeline);
                        JOptionPane.showMessageDialog(this, "Xóa lịch tập thành công!");
                        clearForm();
                        loadSchedules();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch tập để xóa!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
        }
    }

    private boolean validateForm() {
        if (startDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày bắt đầu!");
            return false;
        }
        if (startTimeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giờ bắt đầu!");
            return false;
        }
        if (endDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày kết thúc!");
            return false;
        }
        if (endTimeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giờ kết thúc!");
            return false;
        }

        try {
            String startDateTime = startDateField.getText() + " " + startTimeField.getText();
            String endDateTime = endDateField.getText() + " " + endTimeField.getText();

            Timestamp startTimestamp = TimeUtil.createTimestamp(startDateTime);
            Timestamp endTimestamp = TimeUtil.createTimestamp(endDateTime);

            if (endTimestamp.before(startTimestamp)) {
                JOptionPane.showMessageDialog(this, "Thời gian kết thúc phải sau thời gian bắt đầu!");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Định dạng thời gian không hợp lệ!");
            return false;
        }

        return true;
    }

    private void loadSubjects() {
        try {
            subjects = Query.findAll(Subject.class);
        } catch (Exception e) {
            subjects = new java.util.ArrayList<>();
            System.err.println("Error loading subjects: " + e.getMessage());
        }
    }

    private void updateSubjectComboBox() {
        subjectComboBox.removeAllItems();
        subjectComboBox.addItem("-- Chọn môn tập --");

        for (Subject subject : subjects) {
            if (!subject.isDeleted()) {
                subjectComboBox.addItem(subject.getId() + " - " + subject.getName());
            }
        }
    }

    private int getSelectedSubjectId() {
        String selected = (String) subjectComboBox.getSelectedItem();
        if (selected == null || selected.startsWith("--")) {
            return 0;
        }

        try {
            return Integer.parseInt(selected.split(" - ")[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    private void clearForm() {
        selectedTimelineId = -1;
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        startDateField.setText(currentDate);
        startTimeField.setText("08:00:00");
        endDateField.setText(currentDate);
        endTimeField.setText("09:00:00");
        subjectComboBox.setSelectedIndex(0);
        scheduleTable.clearSelection();
    }
}