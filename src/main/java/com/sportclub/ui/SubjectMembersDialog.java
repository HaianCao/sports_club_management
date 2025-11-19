package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.sql.Date;

/**
 * Dialog for managing members in a subject
 */
public class SubjectMembersDialog extends JDialog {

    private Subject subject;
    private JTable membersTable;
    private DefaultTableModel membersTableModel;
    private JComboBox<String> availableMembersCombo;
    private JButton addMemberBtn, removeMemberBtn, refreshBtn, closeBtn;
    private JLabel subjectInfoLabel;
    private List<Member> availableMembers;
    private List<Member> subjectMembers;

    public SubjectMembersDialog(JFrame parent, Subject subject) {
        super(parent, "Quản lý thành viên - " + subject.getName(), true);
        this.subject = subject;

        initializeComponents();
        setupLayout();
        loadSubjectInfo();
        loadSubjectMembers();
        loadAvailableMembers();

        setSize(800, 600);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Subject info label
        subjectInfoLabel = new JLabel();
        subjectInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        subjectInfoLabel.setForeground(new Color(51, 122, 183));

        // Members table
        String[] columns = { "ID", "Tên thành viên", "Giới tính", "Số điện thoại", "Email", "Ngày đăng ký" };
        membersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        membersTable = new JTable(membersTableModel);
        membersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Available members combo
        availableMembersCombo = new JComboBox<>();
        availableMembersCombo.setPreferredSize(new Dimension(300, 30));

        // Buttons
        addMemberBtn = new JButton("Thêm vào môn");
        addMemberBtn.setBackground(new Color(40, 167, 69));
        addMemberBtn.setForeground(Color.WHITE);
        addMemberBtn.setFocusPainted(false);

        removeMemberBtn = new JButton("Xóa khỏi môn");
        removeMemberBtn.setBackground(new Color(220, 53, 69));
        removeMemberBtn.setForeground(Color.WHITE);
        removeMemberBtn.setFocusPainted(false);

        refreshBtn = new JButton("Làm mới");
        refreshBtn.setBackground(new Color(23, 162, 184));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        closeBtn = new JButton("Đóng");
        closeBtn.setBackground(new Color(108, 117, 125));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);

        // Action listeners
        addMemberBtn.addActionListener(this::addMemberToSubject);
        removeMemberBtn.addActionListener(this::removeMemberFromSubject);
        refreshBtn.addActionListener(e -> {
            loadSubjectMembers();
            loadAvailableMembers();
        });
        closeBtn.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(subjectInfoLabel);

        // Members table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách thành viên trong môn"));
        tablePanel.add(new JScrollPane(membersTable), BorderLayout.CENTER);

        // Add member panel
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setBorder(BorderFactory.createTitledBorder("Thêm thành viên"));
        addPanel.add(new JLabel("Chọn thành viên:"));
        addPanel.add(availableMembersCombo);
        addPanel.add(addMemberBtn);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(removeMemberBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(closeBtn);

        // Main layout
        add(infoPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(addPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadSubjectInfo() {
        String info = String.format("<html><b>Môn tập:</b> %s<br><b>Huấn luyện viên:</b> %s<br><b>Mô tả:</b> %s</html>",
                subject.getName(),
                subject.getCoach() != null ? subject.getCoach() : "Chưa có",
                subject.getDesc() != null ? subject.getDesc() : "Chưa có mô tả");
        subjectInfoLabel.setText(info);
    }

    private void loadSubjectMembers() {
        membersTableModel.setRowCount(0);
        subjectMembers = new ArrayList<>();

        try {
            List<Regist> registrations = Query.findRegistrationsBySubject(subject.getSubjId());
            if (registrations != null) {
                for (Regist registration : registrations) {
                    Member member = Query.findMemberById(registration.getMemId());
                    if (member != null) {
                        subjectMembers.add(member);
                        Object[] row = {
                                member.getMemId(),
                                member.getName(),
                                member.getGender(),
                                member.getPhone(),
                                member.getEmail(),
                                registration.getRegistDay()
                        };
                        membersTableModel.addRow(row);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi khi tải danh sách thành viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAvailableMembers() {
        availableMembersCombo.removeAllItems();
        availableMembers = new ArrayList<>();

        try {
            List<Member> allMembers = Query.findActiveMembers();
            if (allMembers != null) {
                for (Member member : allMembers) {
                    // Kiểm tra xem thành viên đã tham gia môn này chưa
                    boolean alreadyRegistered = subjectMembers.stream()
                            .anyMatch(sm -> sm.getMemId() == member.getMemId());

                    if (!alreadyRegistered) {
                        availableMembers.add(member);
                        availableMembersCombo.addItem(member.getName() + " (ID: " + member.getMemId() + ")");
                    }
                }
            }

            addMemberBtn.setEnabled(availableMembersCombo.getItemCount() > 0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi khi tải danh sách thành viên có thể thêm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMemberToSubject(ActionEvent e) {
        int selectedIndex = availableMembersCombo.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= availableMembers.size()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên để thêm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Member selectedMember = availableMembers.get(selectedIndex);

            // Thêm registration mới sử dụng method đúng signature
            Add.addRegistration(selectedMember.getMemId(), subject.getSubjId(), new Date(System.currentTimeMillis()));

            JOptionPane.showMessageDialog(this,
                    "Đã thêm " + selectedMember.getName() + " vào môn " + subject.getName() + "!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Refresh data
            loadSubjectMembers();
            loadAvailableMembers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi khi thêm thành viên: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void removeMemberFromSubject(ActionEvent e) {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int memberId = (Integer) membersTableModel.getValueAt(selectedRow, 0);
        String memberName = (String) membersTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa " + memberName + " khỏi môn " + subject.getName() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Xóa registration
                Delete.deleteRegistration(memberId, subject.getSubjId());

                JOptionPane.showMessageDialog(this,
                        "Đã xóa " + memberName + " khỏi môn " + subject.getName() + "!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Refresh data
                loadSubjectMembers();
                loadAvailableMembers();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi khi xóa thành viên: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}