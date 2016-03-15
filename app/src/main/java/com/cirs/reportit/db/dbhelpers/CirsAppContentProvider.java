package com.cirs.reportit.db.dbhelpers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

public class CirsAppContentProvider extends ContentProvider {

    private static final String PROVIDER_NAME = "com.cirs.reportit.db";
    private static final String CONTENT_URI_DB_STR = "content://" + PROVIDER_NAME + "/";
    public static final Uri CONTENT_URI_DB = Uri.parse(CONTENT_URI_DB_STR);

    private CirsAppDbHelper cirsAppDbHelper;

    @Override
    public Uri insert(Uri uri, ContentValues values) throws SQLException {
        cirsAppDbHelper = CirsAppDbHelper.getInstance(getContext());
        String tableName = getTableName(uri);
        long rowID = cirsAppDbHelper.insertValue(tableName, values);
        return Uri.withAppendedPath(CONTENT_URI_DB, rowID + "");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        cirsAppDbHelper = CirsAppDbHelper.getInstance(getContext());
        String tableName = getTableName(uri);
        return cirsAppDbHelper.getValues(tableName, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        cirsAppDbHelper = CirsAppDbHelper.getInstance(getContext());
        String tableName = getTableName(uri);
        return cirsAppDbHelper.updateValues(tableName, values, selection, selectionArgs);
    }

    public static String getTableName(Uri uri) {
        String value = uri.getPath();
        value = value.replace("/", "");
        return value;
    }

}
