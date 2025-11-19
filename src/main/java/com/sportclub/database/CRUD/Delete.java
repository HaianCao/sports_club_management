package com.sportclub.database.CRUD;

import com.sportclub.database.models.*;
import java.util.List;

public class Delete {

    public static void deleteMember(int memId) {
        Member member = Query.findById(Member.class, memId);
        if (member != null) {
            CRUDManager.delete(member);
            System.out.println("Deleted member with ID: " + memId);
        } else {
            System.out.println("Member with ID " + memId + " not found for deletion.");
        }
    }

    public static void deleteSubject(int subjId) {
        Subject subject = Query.findById(Subject.class, subjId);
        if (subject != null) {
            CRUDManager.delete(subject);
            System.out.println("Deleted subject with ID: " + subjId);
        } else {
            System.out.println("Subject with ID " + subjId + " not found for deletion.");
        }
    }

    public static void deleteTimeline(int timelineId) {
        Timeline timeline = Query.findById(Timeline.class, timelineId);
        if (timeline != null) {
            CRUDManager.delete(timeline);
            System.out.println("Deleted timeline with ID: " + timelineId);
        } else {
            System.out.println("Timeline with ID " + timelineId + " not found for deletion.");
        }
    }

    public static void deleteAttendance(int attendId) {
        Attendance attendance = Query.findById(Attendance.class, attendId);
        if (attendance != null) {
            CRUDManager.delete(attendance);
            System.out.println("Deleted attendance with ID: " + attendId);
        } else {
            System.out.println("Attendance with ID " + attendId + " not found for deletion.");
        }
    }

    public static void deleteRegistration(Regist regist) {
        if (regist != null) {
            CRUDManager.delete(regist);
            System.out
                    .println("Deleted registration: Member " + regist.getMemId() + " - Subject " + regist.getSubjId());
        } else {
            System.out.println("Registration not found for deletion.");
        }
    }

    public static void deleteRegistration(int memId, int subjId) {
        try {
            // Find registration by IDs
            List<Regist> registrations = Query.findRegistrationsBySubject(subjId);
            if (registrations != null) {
                for (Regist regist : registrations) {
                    if (regist.getMemId() == memId) {
                        deleteRegistration(regist);
                        return;
                    }
                }
            }
            System.out.println("Registration not found: Member " + memId + " - Subject " + subjId);
        } catch (Exception e) {
            System.out.println("Error deleting registration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}