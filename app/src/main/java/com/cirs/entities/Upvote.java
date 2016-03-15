package com.cirs.entities;

public class Upvote {

    private Long id;

    private CIRSUser user;

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

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }
}
