package com.cirs.entities;

import java.sql.Timestamp;

public class Comment {

    private Long id;

    private CIRSUser commenter;

    private String comment;

    private Timestamp timestamp;

    private Complaint complaint;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CIRSUser getCommenter() {
        return commenter;
    }

    public void setCommenter(CIRSUser commenter) {
        this.commenter = commenter;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }
}
