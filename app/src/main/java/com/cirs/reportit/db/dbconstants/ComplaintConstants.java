package com.cirs.reportit.db.dbconstants;

import android.net.Uri;

import com.cirs.reportit.db.dbhelpers.CirsAppContentProvider;

public class ComplaintConstants {

    public static final String TABLE_COMPLAINT = "complaint";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_LANDMARK = "landmark";
    public static final String COLUMN_COMPLAINT_PIC = "complaint_pic";
    public static final String COLUMN_CIRSUSER_ID = "cirsuser_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_UPVOTES = "upvotes";
    public static final String COLUMN_BOOKMARKED = "bookmarked";
    public static final String COLUMN_UPVOTED = "upvoted";
    public static final String COLUMN_COMMENT_COUNT = "comment_count";

    public static final String CREATE_TABLE_COMPLAINT = "create table " + TABLE_COMPLAINT + "("
            + COLUMN_ID + " integer primary key,"
            + COLUMN_CATEGORY_ID + " integer,"
            + COLUMN_TITLE + " text not null,"
            + COLUMN_DESCRIPTION + " text not null,"
            + COLUMN_LOCATION + " text not null,"
            + COLUMN_LANDMARK + " text,"
            + COLUMN_COMPLAINT_PIC + " blob,"
            + COLUMN_CIRSUSER_ID + " integer not null,"
            + COLUMN_TIMESTAMP + " text,"
            + COLUMN_STATUS + " text,"
            + COLUMN_UPVOTES + " integer,"
            + COLUMN_BOOKMARKED + " text,"
            + COLUMN_UPVOTED + " text,"
            + COLUMN_COMMENT_COUNT + " integer)";

    public static final Uri TABLE_COMPLAINT_URI = Uri.withAppendedPath(CirsAppContentProvider.CONTENT_URI_DB, TABLE_COMPLAINT);


}
