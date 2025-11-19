package com.sportclub.ui;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.Member;
import com.sportclub.database.models.Subject;
import com.sportclub.database.models.Regist;
import com.sportclub.database.models.Attendance;
import com.sportclub.database.models.Timeline;
import com.sportclub.util.CSVExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class MemberManagementPanel extends JPanel {

    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, emailField, birthField;
    private JComboBox<String> genderCombo;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn, clearFilterBtn, exportCSVBtn;
    private int selectedMemberId = -1;

    private JTextField searchField;
    private JComboBox<String> subjectFilterCombo;
    private java.util.List<Member> allMembers;

    private JRadioButton memberViewMode, attendanceViewMode;
    private JComboBox<String> attendanceSubjectCombo;
    private JCheckBox currentWeekFilter;
    private JTextField dateFilterField;
    private java.util.List<Object[]> allAttendanceRecords;

    public MemberManagementPanel() {
        initializeComponents();
        setupLayout();
        loadMembers();
    }

    private void initializeComponents() {
        setupMemberTableModel();
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedMember();
            }
        });

        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        birthField = new JTextField(10);

        genderCombo = new JComboBox<>(new String[] { "Nam", "Nữ", "Khác" });

        addBtn = new JButton("Thêm thành viên");
        addBtn.setPreferredSize(new Dimension(140, 35));
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
        exportCSVBtn = new JButton("Xuất CSV");
        refreshBtn.setPreferredSize(new Dimension(100, 35));
        refreshBtn.setMargin(new Insets(5, 10, 5, 10));

        clearFilterBtn = new JButton("Xóa bộ lọc");
        clearFilterBtn.setPreferredSize(new Dimension(100, 35));
        clearFilterBtn.setMargin(new Insets(5, 10, 5, 10));

        exportCSVBtn = new JButton("Xuất CSV");
        exportCSVBtn.setPreferredSize(new Dimension(100, 35));
        exportCSVBtn.setMargin(new Insets(5, 10, 5, 10));

        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                filterData();
            }

            public void removeUpdate(DocumentEvent e) {
                filterData();
            }

            public void insertUpdate(DocumentEvent e) {
                filterData();
            }
        });

        subjectFilterCombo = new JComboBox<>();
        subjectFilterCombo.addActionListener(e -> filterData());
        loadSubjectsForFilter();

        memberViewMode = new JRadioButton("Thành viên duy nhất", true);
        attendanceViewMode = new JRadioButton("Danh sách điểm danh");
        ButtonGroup viewGroup = new ButtonGroup();
        viewGroup.add(memberViewMode);
        viewGroup.add(attendanceViewMode);

        memberViewMode.addActionListener(e -> {
            switchViewMode();
            filterData();
        });
        attendanceViewMode.addActionListener(e -> {
            switchViewMode();
            filterData();
        });

        attendanceSubjectCombo = new JComboBox<>();
        attendanceSubjectCombo.addActionListener(e -> filterData());

        currentWeekFilter = new JCheckBox("Tuần hiện tại");
        currentWeekFilter.addActionListener(e -> filterData());

        dateFilterField = new JTextField(8);
        dateFilterField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                filterData();
            }

            public void removeUpdate(DocumentEvent e) {
                filterData();
            }

            public void insertUpdate(DocumentEvent e) {
                filterData();
            }
        });

        loadSubjectsForAttendanceFilter();

        addBtn.addActionListener(this::addMember);
        updateBtn.addActionListener(this::updateMember);
        deleteBtn.addActionListener(this::deleteMember);
        refreshBtn.addActionListener(e -> loadMembers());
        clearFilterBtn.addActionListener(this::clearFilters);
        exportCSVBtn.addActionListener(e -> exportToCSV());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm và lọc"));

        JPanel viewModePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewModePanel.add(new JLabel("Chế độ xem:"));
        viewModePanel.add(memberViewMode);
        viewModePanel.add(attendanceViewMode);

        JPanel basicFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        basicFilterPanel.add(new JLabel("Tìm kiếm:"));
        basicFilterPanel.add(searchField);
        basicFilterPanel.add(Box.createHorizontalStrut(10));
        basicFilterPanel.add(new JLabel("Lọc theo môn:"));
        basicFilterPanel.add(subjectFilterCombo);

        JPanel attendanceFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        attendanceFilterPanel.add(new JLabel("Môn học:"));
        attendanceFilterPanel.add(attendanceSubjectCombo);
        attendanceFilterPanel.add(Box.createHorizontalStrut(10));
        attendanceFilterPanel.add(currentWeekFilter);
        attendanceFilterPanel.add(Box.createHorizontalStrut(10));
        attendanceFilterPanel.add(new JLabel("Ngày:"));
        attendanceFilterPanel.add(dateFilterField);
        attendanceFilterPanel.add(Box.createHorizontalStrut(10));
        attendanceFilterPanel.add(clearFilterBtn);
        attendanceFilterPanel.setVisible(false);

        JPanel topFilterPanel = new JPanel(new BorderLayout());
        topFilterPanel.add(viewModePanel, BorderLayout.NORTH);
        topFilterPanel.add(basicFilterPanel, BorderLayout.CENTER);

        filterPanel.add(topFilterPanel, BorderLayout.NORTH);
        filterPanel.add(attendanceFilterPanel, BorderLayout.SOUTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin thành viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Ngày sinh (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3;
        formPanel.add(birthField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 3;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(exportCSVBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(memberTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMembers() {
        allMembers = Query.findActiveMembers();
        displayMembers(allMembers);
    }

    private void displayMembers(java.util.List<Member> members) {
        tableModel.setRowCount(0);
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

            Date birth = Date.valueOf(birthStr);
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
                Delete.deleteMember(selectedMemberId);
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

    private void loadSubjectsForFilter() {
        subjectFilterCombo.removeAllItems();
        subjectFilterCombo.addItem("-- Tất cả môn --");

        try {
            java.util.List<Subject> subjects = Query.findActiveSubjects();
            if (subjects != null) {
                for (Subject subject : subjects) {
                    subjectFilterCombo.addItem(subject.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterMembers() {
        if (allMembers == null) {
            return;
        }

        String searchText = searchField.getText().trim().toLowerCase();
        String selectedSubject = (String) subjectFilterCombo.getSelectedItem();

        java.util.List<Member> filteredMembers = new java.util.ArrayList<>();

        java.util.List<Member> subjectFilteredMembers = new java.util.ArrayList<>();

        if (selectedSubject == null || selectedSubject.equals("-- Tất cả môn --")) {
            subjectFilteredMembers.addAll(allMembers);
        } else {
            for (Member member : allMembers) {
                try {
                    java.util.List<Regist> registrations = Query.findRegistrationsByMember(member.getMemId());
                    if (registrations != null) {
                        for (Regist regist : registrations) {
                            Subject subject = Query.findById(Subject.class, regist.getSubjId());
                            if (subject != null && subject.getName().equals(selectedSubject)) {
                                subjectFilteredMembers.add(member);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (Member member : subjectFilteredMembers) {
            boolean matchesSearch = true;
            if (!searchText.isEmpty()) {
                matchesSearch = member.getName().toLowerCase().contains(searchText) ||
                        member.getPhone().toLowerCase().contains(searchText) ||
                        member.getEmail().toLowerCase().contains(searchText) ||
                        member.getGender().toLowerCase().contains(searchText) ||
                        member.getBirth().toString().toLowerCase().contains(searchText);
            }

            if (matchesSearch) {
                filteredMembers.add(member);
            }
        }

        displayMembers(filteredMembers);
    }

    private void clearFilters(java.awt.event.ActionEvent e) {
        searchField.setText("");
        subjectFilterCombo.setSelectedIndex(0);
        if (attendanceViewMode.isSelected()) {
            attendanceSubjectCombo.setSelectedIndex(0);
            currentWeekFilter.setSelected(false);
            dateFilterField.setText("");
        }
        filterData();
    }

    private void switchViewMode() {
        boolean isAttendanceMode = attendanceViewMode.isSelected();

        Container parent = attendanceSubjectCombo.getParent();
        if (parent != null) {
            parent.setVisible(isAttendanceMode);
        }

        if (memberTable != null) {
            if (isAttendanceMode) {
                setupAttendanceTableModel();
                loadAttendanceData();
            } else {
                setupMemberTableModel();
                loadMembers();
            }
        }
    }

    private void setupMemberTableModel() {
        String[] columns = { "Mã TV", "Tên", "Ngày sinh", "Giới tính", "SĐT", "Email" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        if (memberTable != null) {
            memberTable.setModel(tableModel);
        }
    }

    private void setupAttendanceTableModel() {
        String[] columns = { "Mã TV", "Tên thành viên", "SĐT", "Môn học", "Ngày điểm danh", "Trạng thái", "Ghi chú" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        if (memberTable != null) {
            memberTable.setModel(tableModel);
        }
    }

    private void loadAttendanceData() {
        allAttendanceRecords = new java.util.ArrayList<>();
        try {
            java.util.List<Attendance> attendances = Query.findAll(Attendance.class);
            if (attendances != null) {
                for (Attendance attendance : attendances) {
                    Member member = Query.findMemberById(attendance.getMemId());
                    Timeline timeline = Query.findById(Timeline.class, attendance.getTimelineId());
                    Subject subject = null;
                    if (timeline != null) {
                        subject = Query.findById(Subject.class, timeline.getSubjId());
                    }

                    if (member != null && subject != null) {
                        Object[] record = {
                                member.getMemId(),
                                member.getName(),
                                member.getPhone(),
                                subject.getName(),
                                attendance.getAttendDate(),
                                attendance.getStatus(),
                                attendance.getNotes() != null ? attendance.getNotes() : ""
                        };
                        allAttendanceRecords.add(record);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayAttendanceRecords(allAttendanceRecords);
    }

    private void displayAttendanceRecords(java.util.List<Object[]> records) {
        tableModel.setRowCount(0);
        if (records != null) {
            for (Object[] record : records) {
                tableModel.addRow(record);
            }
        }
    }

    private void loadSubjectsForAttendanceFilter() {
        attendanceSubjectCombo.removeAllItems();
        attendanceSubjectCombo.addItem("-- Tất cả môn --");

        try {
            java.util.List<Subject> subjects = Query.findActiveSubjects();
            if (subjects != null) {
                for (Subject subject : subjects) {
                    attendanceSubjectCombo.addItem(subject.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterData() {
        if (memberViewMode == null || attendanceViewMode == null) {
            return;
        }

        if (memberViewMode.isSelected()) {
            filterMembers();
        } else {
            filterAttendanceRecords();
        }
    }

    private void filterAttendanceRecords() {
        if (allAttendanceRecords == null) {
            return;
        }

        String searchText = searchField.getText().trim().toLowerCase();
        String selectedSubject = (String) attendanceSubjectCombo.getSelectedItem();
        boolean filterCurrentWeek = currentWeekFilter.isSelected();
        String dateText = dateFilterField.getText().trim();

        java.util.List<Object[]> filteredRecords = new java.util.ArrayList<>();

        for (Object[] record : allAttendanceRecords) {
            boolean matches = true;

            if (!searchText.isEmpty()) {
                boolean textMatches = record[1].toString().toLowerCase().contains(searchText) ||
                        record[2].toString().toLowerCase().contains(searchText) ||
                        record[3].toString().toLowerCase().contains(searchText) ||
                        record[5].toString().toLowerCase().contains(searchText) ||
                        record[6].toString().toLowerCase().contains(searchText);
                matches = matches && textMatches;
            }

            if (selectedSubject != null && !selectedSubject.equals("-- Tất cả môn --")) {
                matches = matches && record[3].toString().equals(selectedSubject);
            }

            if (filterCurrentWeek) {
                try {
                    Date attendDate = (Date) record[4];
                    LocalDate recordDate = attendDate.toLocalDate();
                    LocalDate today = LocalDate.now();

                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int recordWeek = recordDate.get(weekFields.weekOfWeekBasedYear());
                    int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
                    int recordYear = recordDate.getYear();
                    int currentYear = today.getYear();

                    matches = matches && (recordWeek == currentWeek && recordYear == currentYear);
                } catch (Exception e) {
                    matches = false;
                }
            }

            if (!dateText.isEmpty()) {
                try {
                    Date filterDate = Date.valueOf(dateText);
                    Date recordDate = (Date) record[4];
                    matches = matches && recordDate.equals(filterDate);
                } catch (Exception e) {
                }
            }

            if (matches) {
                filteredRecords.add(record);
            }
        }

        displayAttendanceRecords(filteredRecords);
    }

    private void exportToCSV() {
        if (memberTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String filename;
        if (memberViewMode.isSelected()) {
            filename = "danh_sach_thanh_vien";
        } else {
            filename = "danh_sach_diem_danh";
        }

        CSVExporter.exportTableToCSV(memberTable, filename);
    }
}