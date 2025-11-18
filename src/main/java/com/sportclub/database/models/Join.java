package com.sportclub.database.models;

import javax.persistence.*;

@Entity
@Table(name = "Joins")
public class Join {

    @EmbeddedId
    private JoinId id;

    @Column(name = "participated")
    private int participated = 0; // 0 for false, 1 for true

    @Column(name = "comment")
    private String comment;

    @Column(name = "is_deleted")
    private int isDeleted = 0; // 0 for false, 1 for true

    @Column(name = "manage_id")
    private String manageId; // ID of the manager for this join

    // Constructors
    public Join() {
    }

    // Getters and Setters
    public JoinId getId() {
        return id;
    }

    public void setId(JoinId id) {
        this.id = id;
    }

    public int getParticipated() {
        return participated;
    }

    public void setParticipated(int participated) {
        this.participated = participated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getManageId() {
        return manageId;
    }

    public void setManageId(String manageId) {
        this.manageId = manageId;
    }
}

