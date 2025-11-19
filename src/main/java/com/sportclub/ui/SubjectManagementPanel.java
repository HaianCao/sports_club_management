package com.sportclub.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.sportclub.database.CRUD.Add;
import com.sportclub.database.CRUD.Delete;
import com.sportclub.database.CRUD.Query;
import com.sportclub.database.CRUD.Update;
import com.sportclub.database.models.Subject;

/**
 * Panel for managing sports subjects
 */
public class SubjectManagementPanel extends JPanel {

    private JTable subjectTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, coachField;
    private JTextArea descriptionArea;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn, viewMembersBtn;
    private int selectedSubjectId = -1;
    private ScheduleManagementPanel schedulePanelRef; // optional ref to refresh schedule subjects
    private AttendancePanel attendancePanelRef; // optional ref to refresh attendance

    public SubjectManagementPanel() {
        this(null, null);
    }

    /**
     * Construct with optional references to schedule and attendance panels
     */
    public SubjectManagementPanel(ScheduleManagementPanel schedulePanel, AttendancePanel attendancePanel) {
        this.schedulePanelRef = schedulePanel;
        this.attendancePanelRef = attendancePanel;
        initializeComponents();
        setupLayout();
        loadSubjects();
    }

    private void initializeComponents() {
        // Table setup
        String[] columns = { "Mã môn", "Tên môn", "Mô tả", "Huấn luyện viên", "Số thành viên" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        subjectTable = new JTable(tableModel);
        subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedSubject();
            }
        });

        // Form fields
        nameField = new JTextField(20);
        coachField = new JTextField(20);
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // Buttons
        addBtn = new JButton("Thêm môn tập");
        addBtn.setPreferredSize(new Dimension(100, 35));
        addBtn.setMargin(new Insets(5, 10, 5, 10));

        updateBtn = new JButton("Cập nhật");
        updateBtn.setPreferredSize(new Dimension(100, 35));
        updateBtn.setMargin(new Insets(5, 10, 5, 10));
        updateBtn.setEnabled(false);

        deleteBtn = new JButton("Xóa");
        deleteBtn.setPreferredSize(new Dimension(100, 35));
        deleteBtn.setMargin(new Insets(5, 10, 5, 10));
        deleteBtn.setEnabled(false);

        refreshBtn = new JButton("Làm mới");
        refreshBtn.setPreferredSize(new Dimension(100, 35));
        refreshBtn.setMargin(new Insets(5, 10, 5, 10));

        viewMembersBtn = new JButton("Quản lý thành viên");
        viewMembersBtn.setPreferredSize(new Dimension(140, 35));
        viewMembersBtn.setMargin(new Insets(5, 10, 5, 10));
        viewMembersBtn.setEnabled(false);

        // Action listeners
        addBtn.addActionListener(this::addSubject);
        updateBtn.addActionListener(this::updateSubject);
        deleteBtn.addActionListener(this::deleteSubject);
        refreshBtn.addActionListener(e -> loadSubjects());
        viewMembersBtn.addActionListener(e -> viewSubjectMembers());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin môn tập"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Tên môn tập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        // Coach field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Huấn luyện viên:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(coachField, gbc);

        // Description area
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(viewMembersBtn);
        buttonPanel.add(refreshBtn);

        // Main layout
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadSubjects() {
        tableModel.setRowCount(0);
        List<Subject> subjects = Query.findActiveSubjects();
        if (subjects != null) {
            for (Subject subject : subjects) {
                // Đếm số thành viên trong môn này
                List<com.sportclub.database.models.Regist> registrations = Query
                        .findRegistrationsBySubject(subject.getSubjId());
                int memberCount = registrations != null ? registrations.size() : 0;

                Object[] row = {
                        subject.getSubjId(),
                        subject.getName(),
                        subject.getDesc() != null ? subject.getDesc() : "",
                        subject.getCoach() != null ? subject.getCoach() : "",
                        memberCount + " thành viên"
                };
                tableModel.addRow(row);
            }
        }

        // Setup button renderer and editor for "Xem chi tiết" column
        if (subjectTable.getColumnCount() > 5) {
            subjectTable.removeColumn(subjectTable.getColumnModel().getColumn(5));
        }
    }

    private void viewSubjectMembers() {
        if (selectedSubjectId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tập để xem danh sách thành viên!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Subject subject = Query.findSubjectById(selectedSubjectId);
            if (subject != null) {
                // Tạo dialog quản lý thành viên cho môn tập
                SubjectMembersDialog dialog = new SubjectMembersDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                        subject);
                dialog.setVisible(true);
                // Refresh sau khi đóng dialog
                loadSubjects();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedSubjectId = (Integer) tableModel.getValueAt(selectedRow, 0);
            nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            descriptionArea.setText((String) tableModel.getValueAt(selectedRow, 2));
            coachField.setText((String) tableModel.getValueAt(selectedRow, 3));

            updateBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
            viewMembersBtn.setEnabled(true);
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        selectedSubjectId = -1;
        nameField.setText("");
        descriptionArea.setText("");
        coachField.setText("");
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        viewMembersBtn.setEnabled(false);
    }

    private void addSubject(ActionEvent e) {
        try {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            String coach = coachField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên môn tập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (coach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên huấn luyện viên!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Subject subject = Add.addSubject(name, description, coach);

            if (subject != null) {
                JOptionPane.showMessageDialog(this, "Thêm môn tập thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadSubjects();
                if (schedulePanelRef != null) schedulePanelRef.loadSubjects();
                if (attendancePanelRef != null) attendancePanelRef.refreshAttendance();
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSubject(ActionEvent e) {
        if (selectedSubjectId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tập để cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            String coach = coachField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên môn tập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (coach.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên huấn luyện viên!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn cập nhật môn tập này?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Update.updateSubjectInfo(selectedSubjectId, name, description, coach);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadSubjects();
                if (schedulePanelRef != null) schedulePanelRef.loadSubjects();
                if (attendancePanelRef != null) attendancePanelRef.refreshAttendance();
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSubject(ActionEvent e) {
        if (selectedSubjectId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tập để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa môn tập này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Delete.softDeleteSubject(selectedSubjectId);
                JOptionPane.showMessageDialog(this, "Xóa môn tập thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadSubjects();
                if (schedulePanelRef != null) schedulePanelRef.loadSubjects();
                if (attendancePanelRef != null) attendancePanelRef.refreshAttendance();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}