package com.sportclub.database.models;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "timelines")
public class Timeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeline_id")
    private int timelineId;

    @Column(name = "subj_id", nullable = false)
    private int subjId;

    @Column(name = "week_day")
    private String weekDay; // Monday, Tuesday, etc.

    // Time format: HH:mm:ss (e.g., 18:00:00 for 6:00 PM)
    @Column(name = "start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "place")
    private String place;

    // Constructors
    public Timeline() {
    }

    public Timeline(int subjId, String weekDay, Time startTime, Time endTime, String place) {
        this.subjId = subjId;
        this.weekDay = weekDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
    }

    // Getters and Setters
    public int getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(int timelineId) {
        this.timelineId = timelineId;
    }

    public int getSubjId() {
        return subjId;
    }

    public void setSubjId(int subjId) {
        this.subjId = subjId;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
