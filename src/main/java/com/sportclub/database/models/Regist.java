package com.sportclub.database.models;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "regist")
public class Regist {

    @EmbeddedId
    private RegistId id;

    @Column(name = "regist_day")
    private Date registDay;

    public Regist() {
    }

    public Regist(RegistId registId, Date registDay) {
        this.id = registId;
        this.registDay = registDay;
    }

    public RegistId getId() {
        return id;
    }

    public void setId(RegistId id) {
        this.id = id;
    }

    public Date getRegistDay() {
        return registDay;
    }

    public void setRegistDay(Date registDay) {
        this.registDay = registDay;
    }

    public int getMemId() {
        return id != null ? id.getMemId() : 0;
    }

    public int getSubjId() {
        return id != null ? id.getSubjId() : 0;
    }
}