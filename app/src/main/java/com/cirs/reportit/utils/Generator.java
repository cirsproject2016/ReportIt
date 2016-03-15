package com.cirs.reportit.utils;

import com.cirs.entities.CIRSUser;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ReportItApplication;
import com.pixplicity.easyprefs.library.Prefs;

public class Generator {

    public static String getURLtoEditUser(CIRSUser user) {
        return Constants.BASE_URI + "/user/" + user.getId() + "?adminId=" + Prefs.getLong(Constants.SPUD_ADMIN_ID, -1) + "";
    }

    public static String getURLtoUploadProfilePic(CIRSUser user) {
        return Constants.BASE_URI + "/user/image/" + user.getId();
    }

    public static String getURLtoFetchCategoriesFromServer() {
        return Constants.BASE_URI + "/cat/?adminId=" + Prefs.getLong(Constants.SPUD_ADMIN_ID, -1) + "&activeOnly=true";
    }

    public static String getURLtoSendComplaint() {
        return Constants.BASE_URI + "/complaint";
    }

    public static String getUrltoUploadComplaintPic(Complaint complaint) {
        return Constants.BASE_URI + "/complaint/image/" + complaint.getId();
    }

    public static String getURLtoFetchAllComplaints() {
        return Constants.BASE_URI + "/complaint?adminId=" + Prefs.getLong(Constants.SPUD_ADMIN_ID, -1);
    }

    public static String getURLtoGetUserImage(long id) {
        return Constants.BASE_URI_FOR_IMAGE + "/users/" + id;
    }

    public static String getURLtoGetComplaintImage(Long id) {
        return Constants.BASE_URI_FOR_IMAGE + "/complaints/" + id;
    }

    public static String getURLtoGetComplaintById(Long id) {
        return Constants.BASE_URI + "/complaint/" + id + "?adminId=" + Prefs.getLong(Constants.SPUD_ADMIN_ID, -1);
    }

    public static String getURLtoAddToken() {
        return Constants.BASE_URI + "/user/token";
    }

    public static String getURLtoLoginUser() {
        return Constants.BASE_URI + "/user";
    }

    public static String getURLtoGetUserById(Long id) {
        return Constants.BASE_URI + "/user/" + id + "?adminId=" + Prefs.getLong(Constants.SPUD_ADMIN_ID, -1) + "";
    }

    public static String getURLtoSendComment() {
        return Constants.BASE_URI + "/comment";
    }

    public static String getURLtoUpvote() {
        return Constants.BASE_URI + "/upvote";
    }

    public static String getURLtoFetchUpvotedComplaints() {
        return Constants.BASE_URI + "/upvote?userId=" + ReportItApplication.getCirsUser().getId();
    }
}
