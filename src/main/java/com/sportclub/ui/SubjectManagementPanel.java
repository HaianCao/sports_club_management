package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;
import com.sportclub.util.CSVExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SubjectManagementPanel extends JPanel {

    private JTable subjectTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, coachField;
    private JTextArea descriptionArea;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn, viewMembersBtn, exportCSVBtn;
    private int selectedSubjectId = -1;

    public SubjectManagementPanel() {
        initializeComponents();
        setupLayout();
        loadSubjects();
    }

    private void initializeComponents() {
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

        nameField = new JTextField(20);
        coachField = new JTextField(20);
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        addBtn = new JButton("Thêm môn tập");
        addBtn.setPreferredSize(new Dimension(130, 35));
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

        viewMembersBtn = new JButton("Xem thành viên");
        viewMembersBtn.setPreferredSize(new Dimension(140, 35));
        viewMembersBtn.setMargin(new Insets(5, 10, 5, 10));
        viewMembersBtn.setEnabled(false);

        exportCSVBtn = new JButton("Xuất CSV");
        exportCSVBtn.setPreferredSize(new Dimension(100, 35));
        exportCSVBtn.setMargin(new Insets(5, 10, 5, 10));

        addBtn.addActionListener(this::addSubject);
        updateBtn.addActionListener(this::updateSubject);
        deleteBtn.addActionListener(this::deleteSubject);
        refreshBtn.addActionListener(e -> loadSubjects());
        viewMembersBtn.addActionListener(e -> viewSubjectMembers());
        exportCSVBtn.addActionListener(e -> exportToCSV());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin môn tập"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Tên môn tập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Huấn luyện viên:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(coachField, gbc);

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

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(viewMembersBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(exportCSVBtn);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadSubjects() {
        tableModel.setRowCount(0);
        List<Subject> subjects = Query.findActiveSubjects();
        if (subjects != null) {
            for (Subject subject : subjects) {
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
                SubjectMembersDialog dialog = new SubjectMembersDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                        subject);
                dialog.setVisible(true);
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
                Delete.deleteSubject(selectedSubjectId);
                JOptionPane.showMessageDialog(this, "Xóa môn tập thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadSubjects();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToCSV() {
        if (subjectTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CSVExporter.exportTableToCSV(subjectTable, "danh_sach_mon_hoc");
    }
}