package com.cirs.reportit.db.dbconstants;


import android.net.Uri;

import com.cirs.reportit.db.dbhelpers.CirsAppContentProvider;


public class CategoryConstants {

    public static final String TABLE_CATEGORY = "category";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_ADMIN_ID = "admin_id";

    public static final String CREATE_TABLE_CATEGORY = "create table " + TABLE_CATEGORY + "("
            + COLUMN_ID + " integer primary key,"
            + COLUMN_NAME + " text,"
            + COLUMN_ACTIVE + " text,"
            + COLUMN_ADMIN_ID + " integer not null)";

    public static final Uri TABLE_CATEGORY_URI = Uri.withAppendedPath(CirsAppContentProvider.CONTENT_URI_DB, TABLE_CATEGORY);

}
