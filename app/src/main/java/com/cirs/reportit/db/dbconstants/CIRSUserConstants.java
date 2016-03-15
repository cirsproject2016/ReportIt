package com.cirs.reportit.db.dbconstants;

import android.net.Uri;

import com.cirs.reportit.db.dbhelpers.CirsAppContentProvider;

public class CIRSUserConstants {

    public static final String TABLE_CIRSUSER = "cirsuser";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PROFILE_PIC = "profile_pic";
    public static final String COLUMN_ADMIN_ID = "admin_id";

    public static final String CREATE_TABLE_CIRSUSER = "create table " + TABLE_CIRSUSER + "("
            + COLUMN_ID + " integer primary key,"
            + COLUMN_FIRST_NAME + " text,"
            + COLUMN_LAST_NAME + " text,"
            + COLUMN_GENDER + " text,"
            + COLUMN_DOB + " text,"
            + COLUMN_EMAIL + " text,"
            + COLUMN_PHONE + " text,"
            + COLUMN_PROFILE_PIC + " blob,"
            + COLUMN_ADMIN_ID + " integer not null)";

    public static final Uri TABLE_CIRSUSER_URI = Uri.withAppendedPath(CirsAppContentProvider.CONTENT_URI_DB, TABLE_CIRSUSER);

}
