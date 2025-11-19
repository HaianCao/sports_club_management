package com.sportclub.database.CRUD;

import com.sportclub.database.models.*;
import java.sql.Date;
import java.sql.Time;

public class Add {

    public static Member addMember(String name, Date birth, String gender, String phone, String email) {
        Member member = new Member();
        member.setName(name);
        member.setBirth(birth);
        member.setGender(gender);
        member.setPhone(phone);
        member.setEmail(email);
        CRUDManager.save(member);
        return member;
    }

    public static Subject addSubject(String name, String desc, String coach) {
        Subject subject = new Subject();
        subject.setName(name);
        subject.setDesc(desc);
        subject.setCoach(coach);
        CRUDManager.save(subject);
        return subject;
    }

    public static Timeline addTimeline(int subjId, String weekDay, Time startTime, Time endTime, String place) {
        Timeline timeline = new Timeline();
        timeline.setSubjId(subjId);
        timeline.setWeekDay(weekDay);
        timeline.setStartTime(startTime);
        timeline.setEndTime(endTime);
        timeline.setPlace(place);
        CRUDManager.save(timeline);
        return timeline;
    }

    public static Regist addRegistration(int memId, int subjId, Date registDay) {
        RegistId registId = new RegistId(memId, subjId);
        Regist regist = new Regist();
        regist.setId(registId);
        regist.setRegistDay(registDay);
        CRUDManager.save(regist);
        return regist;
    }

    public static Attendance addAttendance(int memId, int timelineId, Date attendDate) {
        Attendance attendance = new Attendance();
        attendance.setMemId(memId);
        attendance.setTimelineId(timelineId);
        attendance.setAttendDate(attendDate);
        attendance.setStatus("Có mặt");
        attendance.setNotes("");
        CRUDManager.save(attendance);
        return attendance;
    }

    public static Attendance addAttendance(int memId, int timelineId, Date attendDate, String status, String notes) {
        Attendance attendance = new Attendance();
        attendance.setMemId(memId);
        attendance.setTimelineId(timelineId);
        attendance.setAttendDate(attendDate);
        attendance.setStatus(status);
        attendance.setNotes(notes);
        CRUDManager.save(attendance);
        return attendance;
    }
}
