package com.sportclub.database.models;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attend_id")
    private int attendId;

    @Column(name = "mem_id")
    private int memId;

    @Column(name = "timeline_id")
    private int timelineId;

    @Column(name = "attend_date")
    private Date attendDate;

    @Column(name = "status")
    private String status;

    @Column(name = "notes")
    private String notes;

    // Constructors
    public Attendance() {
    }

    public Attendance(int memId, int timelineId, Date attendDate) {
        this.memId = memId;
        this.timelineId = timelineId;
        this.attendDate = attendDate;
    }

    public Attendance(int memId, int timelineId, Date attendDate, String status, String notes) {
        this.memId = memId;
        this.timelineId = timelineId;
        this.attendDate = attendDate;
        this.status = status;
        this.notes = notes;
    }

    // Getters and Setters
    public int getAttendId() {
        return attendId;
    }

    public void setAttendId(int attendId) {
        this.attendId = attendId;
    }

    public int getMemId() {
        return memId;
    }

    public void setMemId(int memId) {
        this.memId = memId;
    }

    public int getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(int timelineId) {
        this.timelineId = timelineId;
    }

    public Date getAttendDate() {
        return attendDate;
    }

    public void setAttendDate(Date attendDate) {
        this.attendDate = attendDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Convenience methods for GUI compatibility
    public int getAttendanceId() {
        return attendId;
    }

    public Date getAttendanceDate() {
        return attendDate;
    }
}