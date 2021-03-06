package com.cirs.entities;

import java.sql.Timestamp;
import java.util.ArrayList;


public class Complaint implements Comparable<Complaint> {

    private Long id;

    private Category category;

    private String title;

    private String description;

    private String location;

    private String landmark;

    private byte[] complaintPic;

    private CIRSUser user;

    private Timestamp timestamp;

    private String status;

    private Integer upvotes;

    private boolean bookmarked;

    private ArrayList<Comment> comments;

    private boolean upvoted;

    private int commentCount;

    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public byte[] getComplaintPic() {
        return complaintPic;
    }

    public void setComplaintPic(byte[] complaintPic) {
        this.complaintPic = complaintPic;
    }

    public CIRSUser getUser() {
        return user;
    }

    public void setUser(CIRSUser user) {
        this.user = user;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public boolean isUpvoted() {
        return upvoted;
    }

    public void setUpvoted(boolean upvoted) {
        this.upvoted = upvoted;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "bookmarked=" + bookmarked +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", user=" + user +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", upvotes=" + upvotes +
                ", upvoted=" + upvoted +
                ", commentCount=" + commentCount +
                '}';
    }

    @Override
    public int compareTo(Complaint complaint) {
        return complaint.timestamp.compareTo(this.timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Complaint complaint = (Complaint) o;

        return id.equals(complaint.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
