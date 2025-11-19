package com.sportclub.database.CRUD;

import com.sportclub.database.models.*;
import java.sql.Date;
import java.sql.Time;

public class Update {

    // Member updates
    public static void updateMember(int memId, String newName, String newPhone, String newEmail) {
        Member member = Query.findById(Member.class, memId);
        if (member != null) {
            member.setName(newName);
            member.setPhone(newPhone);
            member.setEmail(newEmail);
            CRUDManager.update(member);
            System.out.println("Member with ID " + memId + " updated.");
        } else {
            System.out.println("Member with ID " + memId + " not found.");
        }
    }

    // Subject updates
    public static void updateSubjectInfo(int subjId, String newName, String newDesc, String newCoach) {
        Subject subject = Query.findById(Subject.class, subjId);
        if (subject != null) {
            subject.setName(newName);
            subject.setDesc(newDesc);
            subject.setCoach(newCoach);
            CRUDManager.update(subject);
            System.out.println("Subject with ID " + subjId + " updated.");
        } else {
            System.out.println("Subject with ID " + subjId + " not found.");
        }
    }

    // Timeline updates
    public static void updateTimeline(int timelineId, String newWeekDay, Time newStartTime, Time newEndTime,
            String newPlace) {
        Timeline timeline = Query.findById(Timeline.class, timelineId);
        if (timeline != null) {
            timeline.setWeekDay(newWeekDay);
            timeline.setStartTime(newStartTime);
            timeline.setEndTime(newEndTime);
            timeline.setPlace(newPlace);
            CRUDManager.update(timeline);
            System.out.println("Timeline with ID " + timelineId + " updated.");
        } else {
            System.out.println("Timeline with ID " + timelineId + " not found.");
        }
    }

    // Attendance updates
    public static void updateAttendance(int attendId, Date attendDate, String status, String notes) {
        Attendance attendance = Query.findById(Attendance.class, attendId);
        if (attendance != null) {
            attendance.setAttendDate(attendDate);
            attendance.setStatus(status);
            attendance.setNotes(notes);
            CRUDManager.update(attendance);
            System.out.println("Attendance with ID " + attendId + " updated.");
        } else {
            System.out.println("Attendance with ID " + attendId + " not found.");
        }
    }
}