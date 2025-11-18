package com.sportclub.database.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class JoinId implements Serializable {

    @Column(name = "u_id")
    private int uId;

    @Column(name = "t_id")
    private int tId;

    @Column(name = "subject_id")
    private int subjectId;

    // Constructors
    public JoinId() {
    }

    public JoinId(int uId, int tId, int subjectId) {
        this.uId = uId;
        this.tId = tId;
        this.subjectId = subjectId;
    }

    // Getters and Setters
    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public int gettId() {
        return tId;
    }

    public void settId(int tId) {
        this.tId = tId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    // equals and hashCode are required for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinId joinId = (JoinId) o;
        return uId == joinId.uId && tId == joinId.tId && subjectId == joinId.subjectId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uId, tId, subjectId);
    }
}
