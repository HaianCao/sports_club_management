package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for managing club members
 */
public class MemberManagementPanel extends JPanel {

    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, accountField, passwordField;
    private JComboBox<String> genderCombo, roleCombo;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn;
    private int selectedMemberId = -1;

    public MemberManagementPanel() {
        initializeComponents();
        setupLayout();
        loadMembers();
    }

    private void initializeComponents() {
        // Table setup
        String[] columns = { "ID", "Tên", "Số điện thoại", "Tài khoản", "Giới tính", "Vai trò", "Trạng thái" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedMember();
            }
        });

        // Form fields
        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        accountField = new JTextField(20);
        passwordField = new JPasswordField(20);
        genderCombo = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        roleCombo = new JComboBox<>(new String[] { "User", "Manager", "Admin" });

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
        addBtn.addActionListener(this::addMember);
        updateBtn.addActionListener(this::updateMember);
        deleteBtn.addActionListener(this::deleteMember);
        refreshBtn.addActionListener(e -> {
            clearForm();
            loadMembers();
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("QUẢN LÝ THÀNH VIÊN", SwingConstants.CENTER);
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
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách thành viên"));

        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thành viên"));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        // Account
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Tài khoản:"), gbc);
        gbc.gridx = 1;
        panel.add(accountField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        panel.add(genderCombo, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        panel.add(roleCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

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

    private void loadMembers() {
        tableModel.setRowCount(0);
        try {
            List<User> users = Query.findAll(User.class);
            for (User user : users) {
                Object[] row = {
                        user.getId(),
                        user.getName(),
                        user.getPhone(),
                        user.getAccount(),
                        user.getGender(),
                        getRoleString(user.getRole()),
                        user.isDeleted() ? "Đã xóa" : "Hoạt động"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void loadSelectedMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedMemberId = (Integer) tableModel.getValueAt(selectedRow, 0);
            nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            phoneField.setText((String) tableModel.getValueAt(selectedRow, 2));
            accountField.setText((String) tableModel.getValueAt(selectedRow, 3));
            passwordField.setText(""); // Don't show password
            genderCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
            String roleStr = (String) tableModel.getValueAt(selectedRow, 5);
            roleCombo.setSelectedItem(roleStr);
        }
    }

    private void addMember(ActionEvent e) {
        try {
            if (validateForm()) {
                User user = Add.addUser(
                        nameField.getText(),
                        phoneField.getText(),
                        accountField.getText(),
                        passwordField.getText(),
                        (String) genderCombo.getSelectedItem(),
                        getRoleValue((String) roleCombo.getSelectedItem()));
                JOptionPane.showMessageDialog(this, "Thêm thành viên thành công!");
                clearForm();
                loadMembers();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm thành viên: " + ex.getMessage());
        }
    }

    private void updateMember(ActionEvent e) {
        try {
            if (selectedMemberId != -1 && validateForm()) {
                Update.updateUser(selectedMemberId, nameField.getText(), phoneField.getText());
                JOptionPane.showMessageDialog(this, "Cập nhật thành viên thành công!");
                clearForm();
                loadMembers();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên để cập nhật!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage());
        }
    }

    private void deleteMember(ActionEvent e) {
        try {
            if (selectedMemberId != -1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn xóa thành viên này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Delete.softDeleteUser(selectedMemberId);
                    JOptionPane.showMessageDialog(this, "Xóa thành viên thành công!");
                    clearForm();
                    loadMembers();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên để xóa!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!");
            return false;
        }
        if (accountField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản!");
            return false;
        }
        if (passwordField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!");
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedMemberId = -1;
        nameField.setText("");
        phoneField.setText("");
        accountField.setText("");
        passwordField.setText("");
        genderCombo.setSelectedIndex(0);
        roleCombo.setSelectedIndex(0);
        memberTable.clearSelection();
    }

    private String getRoleString(int role) {
        switch (role) {
            case 0:
                return "Root";
            case 1:
                return "Admin";
            case 2:
                return "Manager";
            case 3:
                return "User";
            default:
                return "User";
        }
    }

    private int getRoleValue(String roleStr) {
        switch (roleStr) {
            case "Root":
                return 0;
            case "Admin":
                return 1;
            case "Manager":
                return 2;
            case "User":
                return 3;
            default:
                return 3;
        }
    }
}