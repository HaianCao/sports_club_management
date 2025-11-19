package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for managing sports subjects
 */
public class SubjectManagementPanel extends JPanel {

    private JTable subjectTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn;
    private int selectedSubjectId = -1;

    public SubjectManagementPanel() {
        initializeComponents();
        setupLayout();
        loadSubjects();
    }

    private void initializeComponents() {
        // Table setup
        String[] columns = { "ID", "Tên môn tập", "Mô tả", "Trạng thái" };
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
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

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
        addBtn.addActionListener(this::addSubject);
        updateBtn.addActionListener(this::updateSubject);
        deleteBtn.addActionListener(this::deleteSubject);
        refreshBtn.addActionListener(e -> {
            clearForm();
            loadSubjects();
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("QUẢN LÝ MÔN TẬP", SwingConstants.CENTER);
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
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách môn tập"));

        JScrollPane scrollPane = new JScrollPane(subjectTable);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin môn tập"));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Tên môn tập:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(descriptionArea), gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
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

    private void loadSubjects() {
        tableModel.setRowCount(0);
        try {
            List<Subject> subjects = Query.findAll(Subject.class);
            for (Subject subject : subjects) {
                Object[] row = {
                        subject.getId(),
                        subject.getName(),
                        subject.getDescription() != null ? (subject.getDescription().length() > 50
                                ? subject.getDescription().substring(0, 50) + "..."
                                : subject.getDescription()) : "",
                        subject.isDeleted() ? "Đã xóa" : "Hoạt động"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void loadSelectedSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedSubjectId = (Integer) tableModel.getValueAt(selectedRow, 0);
            try {
                Subject subject = Query.findById(Subject.class, selectedSubjectId);
                if (subject != null) {
                    nameField.setText(subject.getName());
                    descriptionArea.setText(subject.getDescription());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin: " + e.getMessage());
            }
        }
    }

    private void addSubject(ActionEvent e) {
        try {
            if (validateForm()) {
                Subject subject = Add.addSubject(nameField.getText().trim(), descriptionArea.getText().trim());
                JOptionPane.showMessageDialog(this, "Thêm môn tập thành công!");
                clearForm();
                loadSubjects();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm môn tập: " + ex.getMessage());
        }
    }

    private void updateSubject(ActionEvent e) {
        try {
            if (selectedSubjectId != -1 && validateForm()) {
                Update.updateSubjectDescription(selectedSubjectId, descriptionArea.getText().trim());
                JOptionPane.showMessageDialog(this, "Cập nhật môn tập thành công!");
                clearForm();
                loadSubjects();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tập để cập nhật!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage());
        }
    }

    private void deleteSubject(ActionEvent e) {
        try {
            if (selectedSubjectId != -1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn xóa môn tập này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Delete.softDeleteSubject(selectedSubjectId);
                    JOptionPane.showMessageDialog(this, "Xóa môn tập thành công!");
                    clearForm();
                    loadSubjects();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn môn tập để xóa!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên môn tập!");
            nameField.requestFocus();
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedSubjectId = -1;
        nameField.setText("");
        descriptionArea.setText("");
        subjectTable.clearSelection();
    }
}