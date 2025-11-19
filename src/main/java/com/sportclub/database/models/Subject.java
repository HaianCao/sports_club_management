package com.sportclub.database.models;

import javax.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subj_id")
    private int subjId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String desc;

    @Column(name = "coach")
    private String coach;

    public Subject() {
    }

    public Subject(String name, String desc, String coach) {
        this.name = name;
        this.desc = desc;
        this.coach = coach;
    }

    public int getSubjId() {
        return subjId;
    }

    public void setSubjId(int subjId) {
        this.subjId = subjId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }
}
