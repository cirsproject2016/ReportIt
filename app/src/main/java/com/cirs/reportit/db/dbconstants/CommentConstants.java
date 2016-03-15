package com.cirs.reportit.db.dbconstants;

import android.net.Uri;

import com.cirs.reportit.db.dbhelpers.CirsAppContentProvider;


public class CommentConstants {

    public static final String TABLE_COMMENT = "comment";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CIRSUSER_ID = "cirsuser_id";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_COMPLAINT_ID = "complaint_id";

    public static final String CREATE_TABLE_COMMENT = "create table " + TABLE_COMMENT + "("
            + COLUMN_ID + " integer primary key,"
            + COLUMN_CIRSUSER_ID + " integer,"
            + COLUMN_COMMENT + " text,"
            + COLUMN_TIMESTAMP + " text not null,"
            + COLUMN_COMPLAINT_ID + " integer not null)";

    public static final Uri TABLE_COMMENT_URI = Uri.withAppendedPath(CirsAppContentProvider.CONTENT_URI_DB, TABLE_COMMENT);

}