package com.sportclub;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;
import java.sql.Date;
import java.sql.Time;

public class AddTestData {
    public static void main(String[] args) {
        System.out.println("=== Adding Test Data for Attendance ===");

        try {
            // 1. Tạo các Timeline (Lịch học)
            System.out.println("Creating schedules...");

            // Bóng đá - subjId = 1
            Add.addTimeline(1, "Thứ 2", Time.valueOf("08:00:00"), Time.valueOf("10:00:00"), "Sân A");
            Add.addTimeline(1, "Thứ 5", Time.valueOf("16:00:00"), Time.valueOf("18:00:00"), "Sân B");

            // Bơi lội - subjId = 2
            Add.addTimeline(2, "Thứ 3", Time.valueOf("09:00:00"), Time.valueOf("11:00:00"), "Bể bơi");

            // Cầu lông - subjId = 3
            Add.addTimeline(3, "Thứ 6", Time.valueOf("14:00:00"), Time.valueOf("16:00:00"), "Nhà thi đấu");

            // 2. Đăng ký thành viên vào các lớp
            System.out.println("Creating registrations...");

            // Thành viên 1 (Nguyen Van A) đăng ký bóng đá và bơi lội
            Add.addRegistration(1, 1, Date.valueOf("2024-01-01")); // Member 1, Subject 1 (Bóng đá)
            Add.addRegistration(1, 2, Date.valueOf("2024-01-01")); // Member 1, Subject 2 (Bơi lội)

            // Thành viên 2 (Tran Thi B) đăng ký bóng đá và cầu lông
            Add.addRegistration(2, 1, Date.valueOf("2024-01-01")); // Member 2, Subject 1 (Bóng đá)
            Add.addRegistration(2, 3, Date.valueOf("2024-01-01")); // Member 2, Subject 3 (Cầu lông)

            // Thành viên 3 (Le Van C) đăng ký tất cả môn
            Add.addRegistration(3, 1, Date.valueOf("2024-01-01")); // Member 3, Subject 1 (Bóng đá)
            Add.addRegistration(3, 2, Date.valueOf("2024-01-01")); // Member 3, Subject 2 (Bơi lội)
            Add.addRegistration(3, 3, Date.valueOf("2024-01-01")); // Member 3, Subject 3 (Cầu lông)

            System.out.println("Test data has been created successfully!");

            // Hiển thị thông tin đã tạo
            System.out.println("\n--- Created Schedules ---");
            Query.findActiveSchedules().forEach(t -> {
                Subject s = Query.findById(Subject.class, t.getSubjId());
                System.out.println("ID: " + t.getTimelineId() + " - " +
                        (s != null ? s.getName() : "Unknown") +
                        " (" + t.getWeekDay() + " " + t.getStartTime() +
                        " - " + t.getEndTime() + " tại " + t.getPlace() + ")");
            });

            System.out.println("\n--- Registrations ---");
            Query.findActiveRegistrations().forEach(r -> {
                Member m = Query.findById(Member.class, r.getMemId());
                Subject s = Query.findById(Subject.class, r.getSubjId());
                System.out.println((m != null ? m.getName() : "Unknown") +
                        " đăng ký " + (s != null ? s.getName() : "Unknown"));
            });

        } catch (Exception e) {
            System.err.println("Error creating test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}