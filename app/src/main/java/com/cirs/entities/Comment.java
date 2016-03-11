package com.cirs.entities;

import java.sql.Timestamp;

public class Comment implements Comparable<Comment> {

    private Long id;

    private CIRSUser user;

    private String data;

    private Timestamp time;

    private Complaint complaint;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CIRSUser getUser() {
        return user;
    }

    public void setUser(CIRSUser user) {
        this.user = user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    @Override
    public int compareTo(Comment comment) {
        return this.time.compareTo(comment.time);
    }
}
