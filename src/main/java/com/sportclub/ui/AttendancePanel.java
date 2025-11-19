package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for attendance tracking
 */
public class AttendancePanel extends JPanel {

    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> userCombo, subjectCombo, timelineCombo;
    private JTextArea commentArea;
    private JCheckBox participatedCheck;
    private JTextField manageIdField;
    private JButton markBtn, updateBtn, refreshBtn, loadBtn;

    public AttendancePanel() {
        initializeComponents();
        setupLayout();
        loadData();
    }

    private void initializeComponents() {
        // Table setup
        String[] columns = { "Thành viên", "Môn tập", "Lịch tập", "Tham gia", "Ghi chú", "Người quản lý" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(tableModel);
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Form fields
        userCombo = new JComboBox<>();
        subjectCombo = new JComboBox<>();
        timelineCombo = new JComboBox<>();
        participatedCheck = new JCheckBox("Đã tham gia");
        commentArea = new JTextArea(3, 20);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        manageIdField = new JTextField("1", 10);

        // Buttons
        markBtn = new JButton("Điểm danh");
        updateBtn = new JButton("Cập nhật");
        refreshBtn = new JButton("Làm mới");
        loadBtn = new JButton("Tải dữ liệu");

        // Button styling
        styleButton(markBtn, new Color(92, 184, 92));
        styleButton(updateBtn, new Color(240, 173, 78));
        styleButton(refreshBtn, new Color(91, 192, 222));
        styleButton(loadBtn, new Color(128, 128, 128));

        // Button actions
        markBtn.addActionListener(this::markAttendance);
        updateBtn.addActionListener(this::updateAttendance);
        refreshBtn.addActionListener(e -> {
            clearForm();
            loadAttendanceRecords();
        });
        loadBtn.addActionListener(e -> loadData());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("ĐIỂM DANH THÀNH VIÊN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Main content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createFormPanel());
        splitPane.setDividerLocation(500);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách điểm danh"));

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshBtn);
        buttonPanel.add(loadBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Điểm danh"));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // User
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Thành viên:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(userCombo, gbc);

        // Subject
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Môn tập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(subjectCombo, gbc);

        // Timeline
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Lịch tập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(timelineCombo, gbc);

        // Participated
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(participatedCheck, gbc);

        // Comment
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        panel.add(new JScrollPane(commentArea), gbc);

        // Manager ID
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("ID Quản lý:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(manageIdField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(markBtn);
        buttonPanel.add(updateBtn);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
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

    private void loadData() {
        loadUsers();
        loadSubjects();
        loadTimelines();
        loadAttendanceRecords();
    }

    private void loadUsers() {
        userCombo.removeAllItems();
        try {
            List<User> users = Query.findAll(User.class);
            for (User user : users) {
                if (!user.isDeleted()) {
                    userCombo.addItem(user.getId() + " - " + user.getName());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách thành viên: " + e.getMessage());
        }
    }

    private void loadSubjects() {
        subjectCombo.removeAllItems();
        try {
            List<Subject> subjects = Query.findActiveSubjects();
            for (Subject subject : subjects) {
                subjectCombo.addItem(subject.getId() + " - " + subject.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách môn tập: " + e.getMessage());
        }
    }

    private void loadTimelines() {
        timelineCombo.removeAllItems();
        try {
            List<Timeline> timelines = Query.findAll(Timeline.class);
            for (Timeline timeline : timelines) {
                if (!timeline.isDeleted() && timeline.getStart() != null && timeline.getEnd() != null) {
                    String timeInfo = timeline.getTimeId() + " - " +
                            timeline.getStart().toString().split(" ")[0] + " " +
                            timeline.getStart().toString().split(" ")[1] + " → " +
                            timeline.getEnd().toString().split(" ")[1];
                    timelineCombo.addItem(timeInfo);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách lịch tập: " + e.getMessage());
        }
    }

    private void loadAttendanceRecords() {
        tableModel.setRowCount(0);
        try {
            List<Join> joins = CRUDManager.getAll(Join.class);
            for (Join join : joins) {
                if (join.getIsDeleted() == 0) {
                    User user = Query.findById(User.class, join.getId().getuId());
                    Subject subject = Query.findById(Subject.class, join.getId().getSubjectId());
                    Timeline timeline = Query.findById(Timeline.class, join.getId().gettId());

                    if (user != null && subject != null && timeline != null) {
                        Object[] row = {
                                user.getName(),
                                subject.getName(),
                                timeline.getStart() + " → " + timeline.getEnd(),
                                join.getParticipated() == 1 ? "Có" : "Không",
                                join.getComment(),
                                join.getManageId()
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu điểm danh: " + e.getMessage());
        }
    }

    private void markAttendance(ActionEvent e) {
        try {
            if (validateForm()) {
                int userId = extractId((String) userCombo.getSelectedItem());
                int subjectId = extractId((String) subjectCombo.getSelectedItem());
                int timelineId = extractId((String) timelineCombo.getSelectedItem());

                Join join = Add.addJoin(userId, timelineId, subjectId, manageIdField.getText());

                if (participatedCheck.isSelected() || !commentArea.getText().trim().isEmpty()) {
                    Update.updateJoinParticipation(userId, timelineId, subjectId,
                            participatedCheck.isSelected() ? 1 : 0,
                            commentArea.getText().trim(),
                            manageIdField.getText());
                }

                JOptionPane.showMessageDialog(this, "Điểm danh thành công!");
                clearForm();
                loadAttendanceRecords();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi điểm danh: " + ex.getMessage());
        }
    }

    private void updateAttendance(ActionEvent e) {
        try {
            if (validateForm()) {
                int userId = extractId((String) userCombo.getSelectedItem());
                int subjectId = extractId((String) subjectCombo.getSelectedItem());
                int timelineId = extractId((String) timelineCombo.getSelectedItem());

                Update.updateJoinParticipation(userId, timelineId, subjectId,
                        participatedCheck.isSelected() ? 1 : 0,
                        commentArea.getText().trim(),
                        manageIdField.getText());

                JOptionPane.showMessageDialog(this, "Cập nhật điểm danh thành công!");
                clearForm();
                loadAttendanceRecords();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage());
        }
    }

    private boolean validateForm() {
        if (userCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên!");
            return false;
        }
        if (subjectCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tập!");
            return false;
        }
        if (timelineCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch tập!");
            return false;
        }
        if (manageIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID quản lý!");
            return false;
        }
        return true;
    }

    private void clearForm() {
        userCombo.setSelectedIndex(-1);
        subjectCombo.setSelectedIndex(-1);
        timelineCombo.setSelectedIndex(-1);
        participatedCheck.setSelected(false);
        commentArea.setText("");
        manageIdField.setText("1");
        attendanceTable.clearSelection();
    }

    private int extractId(String comboItem) {
        if (comboItem == null)
            return -1;
        return Integer.parseInt(comboItem.split(" - ")[0]);
    }
}