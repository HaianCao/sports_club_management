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
            // Initialize root account if it doesn't exist
            Init.initRootAccount();

            testUserManagement();
            testSubjectManagement();
            testTimelineAndJoinManagement();

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

    private static void testUserManagement() {
        System.out.println("\n--- Testing User Management ---");
        // Add a new user
        User newUser = Add.addUser("John Doe", "123456789", "johndoe", "password123", "Male", 1);
        System.out.println("Added user with ID: " + newUser.getId());

        // Query the user by account
        User foundUser = Query.findUserByAccount("johndoe");
        if (foundUser != null) {
            System.out.println("Found user: " + foundUser.getName());
        }

        // Update the user
        Update.updateUser(foundUser.getId(), "John Doe Updated", "987654321");
        User updatedUser = Query.findById(User.class, foundUser.getId());
        System.out.println("Updated user name: " + updatedUser.getName());

        // Soft delete the user
        Delete.softDeleteUser(updatedUser.getId());
        User deletedUser = Query.findById(User.class, updatedUser.getId());
        System.out.println("Is user deleted? " + deletedUser.isDeleted());
    }

    private static void testSubjectManagement() {
        System.out.println("\n--- Testing Subject Management ---");
        // Add a new subject
        Subject newSubject = Add.addSubject("Morning Yoga", "Relaxing yoga session for all levels.");
        System.out.println("Added subject with ID: " + newSubject.getId());

        // Query all active subjects
        List<Subject> subjects = Query.findActiveSubjects();
        System.out.println("Found " + subjects.size() + " active subjects.");

        // Update subject
        Update.updateSubjectDescription(newSubject.getId(), "An invigorating morning yoga session.");
        Subject updatedSubject = Query.findById(Subject.class, newSubject.getId());
        System.out.println("Updated subject description: " + updatedSubject.getDescription());

        // Soft delete the subject
        Delete.softDeleteSubject(updatedSubject.getId());
        Subject deletedSubject = Query.findById(Subject.class, updatedSubject.getId());
        System.out.println("Is subject deleted? " + deletedSubject.isDeleted());
    }

    private static void testTimelineAndJoinManagement() {
        System.out.println("\n--- Testing Timeline & Join Management ---");
        // Prerequisites: Need a user and a subject
        User participant = Add.addUser("Jane Smith", "555555555", "janesmith", "pass", "Female", 2);
        Subject swimming = Add.addSubject("Afternoon Swim", "Lane swimming session.");

        // Add a timeline (using current time as base)
        Timestamp startTime = TimeUtil.getCurrentTimestamp();
        Timestamp endTime = TimeUtil.addHours(startTime, 1); // 1 hour later
        Timeline swimTime = Add.addTimeline(startTime, endTime);
        System.out.println("Added timeline with ID: " + swimTime.getTimeId());
        System.out.println("Start time (GMT+7): " + TimeUtil.formatTimestamp(startTime));
        System.out.println("End time (GMT+7): " + TimeUtil.formatTimestamp(endTime));

        // A user joins a subject's timeline
        Join newJoin = Add.addJoin(participant.getId(), swimTime.getTimeId(), swimming.getId(), "1");
        System.out.println("User " + newJoin.getId().getuId() + " joined subject " + newJoin.getId().getSubjectId());

        // Update participation status
        Update.updateJoinParticipation(participant.getId(), swimTime.getTimeId(), swimming.getId(), 1,
                "Attended the session.", "1");
        JoinId joinId = new JoinId(participant.getId(), swimTime.getTimeId(), swimming.getId());
        Join updatedJoin = CRUDManager.get(Join.class, joinId);
        System.out.println("User participation status: " + updatedJoin.getParticipated());
        System.out.println("Comment: " + updatedJoin.getComment());
    }
}
