package com.cirs.entities;

import java.util.Arrays;
import java.util.List;

public class CIRSUser {

    private Long id;

    private String firstName;

    private String lastName;

    private String gender;

    private String dob;

    private String email;

    private String phone;

    private byte[] profilePic;

    private Admin admin;

    private List<Complaint> complaints;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<Complaint> complaints) {
        this.complaints = complaints;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

    public List<String> getAllFields() {
        return Arrays.asList(getFirstName(), getLastName(), getGender(), getDob(), getEmail(), getPhone());
    }
}