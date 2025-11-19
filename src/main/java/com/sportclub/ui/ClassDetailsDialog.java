package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.sql.Date;

public class ClassDetailsDialog extends JDialog {

    private Timeline timeline;
    private Subject subject;
    private JTable membersTable;
    private DefaultTableModel membersTableModel;
    private JComboBox<String> availableMembersCombo;
    private JButton addMemberBtn, removeMemberBtn, refreshBtn, closeBtn;
    private JLabel classInfoLabel;

    public ClassDetailsDialog(JFrame parent, Timeline timeline, Subject subject) {
        super(parent, "Chi tiết lớp học", true);
        this.timeline = timeline;
        this.subject = subject;

        initializeComponents();
        setupLayout();
        loadClassInfo();
        loadClassMembers();
        loadAvailableMembers();

        setSize(700, 500);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        classInfoLabel = new JLabel();
        classInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        classInfoLabel.setForeground(new Color(51, 122, 183));

        String[] columns = { "ID", "Tên thành viên", "Giới tính", "Số điện thoại", "Email", "Ngày đăng ký" };
        membersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        membersTable = new JTable(membersTableModel);
        membersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        availableMembersCombo = new JComboBox<>();

        addMemberBtn = new JButton("Thêm thành viên");
        addMemberBtn.addActionListener(this::addMemberToClass);

        removeMemberBtn = new JButton("Xóa khỏi lớp");
        removeMemberBtn.addActionListener(this::removeMemberFromClass);
        removeMemberBtn.setEnabled(false);

        refreshBtn = new JButton("Làm mới");
        refreshBtn.addActionListener(e -> {
            loadClassMembers();
            loadAvailableMembers();
        });

        closeBtn = new JButton("Đóng");
        closeBtn.addActionListener(e -> dispose());

        membersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                removeMemberBtn.setEnabled(membersTable.getSelectedRow() >= 0);
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin lớp học"));
        topPanel.add(classInfoLabel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Danh sách thành viên"));
        JScrollPane tableScrollPane = new JScrollPane(membersTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel addMemberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addMemberPanel.setBorder(BorderFactory.createTitledBorder("Thêm thành viên mới"));
        addMemberPanel.add(new JLabel("Chọn thành viên:"));
        addMemberPanel.add(availableMembersCombo);
        addMemberPanel.add(addMemberBtn);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(removeMemberBtn);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(closeBtn);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(addMemberPanel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadClassInfo() {
        String subjectName = subject != null ? subject.getName() : "Unknown";
        String coach = subject != null ? subject.getCoach() : "Unknown";
        String description = subject != null ? subject.getDesc() : "";

        String info = String.format(
                "<html>" +
                        "<b>Môn:</b> %s<br>" +
                        "<b>Huấn luyện viên:</b> %s<br>" +
                        "<b>Mô tả:</b> %s<br>" +
                        "<b>Thời gian:</b> %s %s - %s<br>" +
                        "<b>Địa điểm:</b> %s" +
                        "</html>",
                subjectName, coach, description,
                timeline.getWeekDay(),
                formatTime(timeline.getStartTime()),
                formatTime(timeline.getEndTime()),
                timeline.getPlace());

        classInfoLabel.setText(info);
    }

    private void loadClassMembers() {
        membersTableModel.setRowCount(0);

        if (subject == null)
            return;

        List<Regist> registrations = Query.findRegistrationsBySubject(subject.getSubjId());
        if (registrations != null) {
            for (Regist regist : registrations) {
                Member member = Query.findById(Member.class, regist.getMemId());
                if (member != null) {
                    Object[] row = {
                            member.getMemId(),
                            member.getName(),
                            member.getGender(),
                            member.getPhone(),
                            member.getEmail(),
                            regist.getRegistDay() != null ? regist.getRegistDay().toString() : "N/A"
                    };
                    membersTableModel.addRow(row);
                }
            }
        }
    }

    private void loadAvailableMembers() {
        availableMembersCombo.removeAllItems();
        availableMembersCombo.addItem("-- Chọn thành viên --");

        if (subject == null)
            return;

        List<Member> allMembers = Query.findActiveMembers();
        if (allMembers == null)
            return;

        List<Regist> registrations = Query.findRegistrationsBySubject(subject.getSubjId());

        for (Member member : allMembers) {
            boolean isRegistered = false;
            if (registrations != null) {
                for (Regist regist : registrations) {
                    if (regist.getMemId() == member.getMemId()) {
                        isRegistered = true;
                        break;
                    }
                }
            }

            if (!isRegistered) {
                availableMembersCombo.addItem(member.getMemId() + " - " + member.getName());
            }
        }
    }

    private void addMemberToClass(ActionEvent e) {
        String selectedMember = (String) availableMembersCombo.getSelectedItem();
        if (selectedMember == null || selectedMember.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (subject == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin môn học!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int memId = Integer.parseInt(selectedMember.split(" - ")[0]);
            Date currentDate = new Date(System.currentTimeMillis());

            List<Regist> existingRegistrations = Query.findRegistrationsByMember(memId);
            if (existingRegistrations != null) {
                for (Regist regist : existingRegistrations) {
                    if (regist.getSubjId() == subject.getSubjId()) {
                        JOptionPane.showMessageDialog(this, "Thành viên này đã đăng ký môn học!", "Thông báo",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            Add.addRegistration(memId, subject.getSubjId(), currentDate);

            JOptionPane.showMessageDialog(this, "Thêm thành viên vào lớp thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            loadClassMembers();
            loadAvailableMembers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeMemberFromClass(ActionEvent e) {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thành viên cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (subject == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin môn học!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int memId = (Integer) membersTableModel.getValueAt(selectedRow, 0);
        String memberName = (String) membersTableModel.getValueAt(selectedRow, 1);

        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa " + memberName + " khỏi lớp học này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                List<Regist> registrations = Query.findRegistrationsByMember(memId);
                if (registrations != null) {
                    for (Regist regist : registrations) {
                        if (regist.getSubjId() == subject.getSubjId()) {
                            Delete.deleteRegistration(regist);
                            break;
                        }
                    }
                }

                JOptionPane.showMessageDialog(this, "Đã xóa thành viên khỏi lớp học!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);

                loadClassMembers();
                loadAvailableMembers();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xóa: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String formatTime(java.sql.Time time) {
        if (time == null)
            return "";
        return time.toString().substring(0, 5);
    }
}