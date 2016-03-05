package com.cirs.reportit.utils;

import android.content.SharedPreferences;

import com.cirs.entities.CIRSUser;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ReportItApplication;

public class Generator {

    private static SharedPreferences pref;

    public static String getURLtoEditUser(CIRSUser user) {
        pref = ReportItApplication.getmAppContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);
        return Constants.BASE_URI + "/user/" + user.getId() + "?adminId=" + pref.getLong(Constants.SPUD_ADMIN_ID, -1) + "";
    }

    public static String getURLtoUploadProfilePic(CIRSUser user) {
        return Constants.BASE_URI + "/user/image/" + user.getId();
    }

    public static String getURLtoFetchCategoriesFromServer() {
        pref = ReportItApplication.getmAppContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);
        return Constants.BASE_URI + "/cat/?adminId=" + pref.getLong(Constants.SPUD_ADMIN_ID, -1) + "&activeOnly=true";
    }

    public static String getURLtoSendComplaint() {
        return Constants.BASE_URI + "/complaint";
    }

    public static String getUrltoUploadComplaintPic(Complaint complaint) {
        return Constants.BASE_URI + "/complaint/image/" + complaint.getId();
    }

    public static String getURLtoFetchAllComplaints() {
        pref = ReportItApplication.getmAppContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);
        return Constants.BASE_URI + "/complaint?adminId=" + pref.getLong(Constants.SPUD_ADMIN_ID, -1);
    }

    public static String getURLtoGetUserImage(long id) {
        return Constants.BASE_URI_FOR_IMAGE + "/users/" + id;
    }

    public static String getURLtoGetComplaintImage(Long id) {
        return Constants.BASE_URI_FOR_IMAGE + "/complaints/" + id;
    }

    public static String getURLtoGetComplaintById(Long id) {
        pref = ReportItApplication.getmAppContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);
        return Constants.BASE_URI + "/complaint/" + id + "?adminId=" + pref.getLong(Constants.SPUD_ADMIN_ID, -1);
    }

    public static String getURLtoAddToken() {
        return Constants.BASE_URI + "/user/token";
    }

    public static String getURLtoLoginUser() {
        pref = ReportItApplication.getmAppContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);
        return Constants.BASE_URI + "/user?adminId=" + pref.getLong(Constants.SPUD_ADMIN_ID, -1);
    }
}
