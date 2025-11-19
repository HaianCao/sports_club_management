package com.sportclub;

import com.sportclub.database.CRUD.*;
import com.sportclub.database.models.*;
import com.sportclub.util.HibernateUtil;
import com.sportclub.util.TimeUtil;
import com.sportclub.ui.MainWindow;
import java.sql.Timestamp;
import java.util.List;
import java.util.TimeZone;

public class Main {

    public static void main(String[] args) {
        // Set timezone to GMT+7 (Vietnam timezone)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        // Check if GUI mode is requested
        if (args.length > 0 && args[0].equals("--gui")) {
            // Launch GUI
            javax.swing.SwingUtilities.invokeLater(() -> {
                new MainWindow().setVisible(true);
            });
            return;
        }

        // Run console tests
        System.out.println("=== Starting Sport Club Management CRUD Tests ===");
        System.out.println("Current timezone: " + TimeUtil.getCurrentTimeZoneInfo());
        System.out.println("Current time: " + TimeUtil.formatTimestamp(TimeUtil.getCurrentTimestamp()));
        System.out.println("Tip: Run with --gui argument to start GUI mode");

        try {
            // Initialize sample data for the new database structure
            Init.initSampleData();

            testMemberManagement();
            testSubjectManagement();
            testScheduleManagement();

        } catch (Throwable ex) {
            System.err.println("\n✗✗✗ A CRITICAL ERROR OCCURRED ✗✗✗");
            System.err.println("Details: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        } finally {
            HibernateUtil.shutdown();
            System.out.println("=== CRUD Tests Finished ===");
        }
    }

    private static void testMemberManagement() {
        System.out.println("\n--- Testing Member Management ---");
        List<Member> members = Query.findActiveMembers();
        System.out.println("Found " + members.size() + " active members:");
        for (Member member : members) {
            System.out.println("- " + member.getName() + " (" + member.getGender() + ") - " + member.getPhone());
        }
    }

    private static void testSubjectManagement() {
        System.out.println("\n--- Testing Subject Management ---");
        List<Subject> subjects = Query.findActiveSubjects();
        System.out.println("Found " + subjects.size() + " active subjects:");
        for (Subject subject : subjects) {
            System.out.println("- " + subject.getName() + " - Coach: " + subject.getCoach());
        }
    }

    private static void testScheduleManagement() {
        System.out.println("\n--- Testing Schedule Management ---");
        List<Timeline> schedules = Query.findActiveSchedules();
        System.out.println("Found " + schedules.size() + " active schedules:");
        for (Timeline schedule : schedules) {
            System.out.println("- " + schedule.getWeekDay() + " " + schedule.getStartTime()
                    + "-" + schedule.getEndTime() + " at " + schedule.getPlace());
        }
    }
}
