package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.Date;

/**
 * Panel for managing club members
 */
public class MemberManagementPanel extends JPanel {

    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, emailField, birthField;
    private JComboBox<String> genderCombo;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn;
    private int selectedMemberId = -1;

    public MemberManagementPanel() {
        initializeComponents();
        setupLayout();
        loadMembers();
    }

    private void initializeComponents() {
        // Table setup
        String[] columns = { "Mã TV", "Tên", "Ngày sinh", "Giới tính", "Sđt", "Email" };
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
        emailField = new JTextField(20);
        birthField = new JTextField(10); // Format: YYYY-MM-DD

        // Gender combo
        genderCombo = new JComboBox<>(new String[] { "Nam", "Nữ", "Khác" });

        // Buttons
        addBtn = new JButton("Thêm thành viên");
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

        // Action listeners
        addBtn.addActionListener(this::addMember);
        updateBtn.addActionListener(this::updateMember);
        deleteBtn.addActionListener(this::deleteMember);
        refreshBtn.addActionListener(e -> loadMembers());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin thành viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Ngày sinh (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3;
        formPanel.add(birthField, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 3;
        formPanel.add(phoneField, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        // Main layout
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(memberTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMembers() {
        tableModel.setRowCount(0);
        List<Member> members = Query.findActiveMembers();
        if (members != null) {
            for (Member member : members) {
                Object[] row = {
                        member.getMemId(),
                        member.getName(),
                        member.getBirth(),
                        member.getGender(),
                        member.getPhone(),
                        member.getEmail()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void loadSelectedMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedMemberId = (Integer) tableModel.getValueAt(selectedRow, 0);
            nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
            birthField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            genderCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 3));
            phoneField.setText((String) tableModel.getValueAt(selectedRow, 4));
            emailField.setText((String) tableModel.getValueAt(selectedRow, 5));

            updateBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        selectedMemberId = -1;
        nameField.setText("");
        birthField.setText("");
        phoneField.setText("");
        emailField.setText("");
        genderCombo.setSelectedIndex(0);
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }

    private void addMember(ActionEvent e) {
        try {
            String name = nameField.getText().trim();
            String birthStr = birthField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || birthStr.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date birth = Date.valueOf(birthStr); // Format: YYYY-MM-DD
            Member member = Add.addMember(name, birth, gender, phone, email);

            if (member != null) {
                JOptionPane.showMessageDialog(this, "Thêm thành viên thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadMembers();
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMember(ActionEvent e) {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên để cập nhật!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn cập nhật thông tin thành viên này?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Update.updateMember(selectedMemberId, name, phone, email);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadMembers();
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMember(ActionEvent e) {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa thành viên này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Delete.softDeleteMember(selectedMemberId);
                JOptionPane.showMessageDialog(this, "Xóa thành viên thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadMembers();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}