package com.sportclub.database.models;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "Timelines")
public class Timeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_id")
    private int timeId;

    @Column(name = "start_time")
    private Time start;

    @Column(name = "end_time")
    private Time end;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    // Constructors
    public Timeline() {
    }

    // Getters and Setters
    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }
}

