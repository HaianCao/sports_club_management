package com.sportclub.database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RegistId implements Serializable {

    @Column(name = "mem_id")
    private int memId;

    @Column(name = "subj_id")
    private int subjId;

    public RegistId() {
    }

    public RegistId(int memId, int subjId) {
        this.memId = memId;
        this.subjId = subjId;
    }

    public int getMemId() {
        return memId;
    }

    public void setMemId(int memId) {
        this.memId = memId;
    }

    public int getSubjId() {
        return subjId;
    }

    public void setSubjId(int subjId) {
        this.subjId = subjId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RegistId registId = (RegistId) o;
        return memId == registId.memId && subjId == registId.subjId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(memId, subjId);
    }
}