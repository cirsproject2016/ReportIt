package com.cirs.reportit.db.dbhelpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cirs.reportit.db.dbconstants.CIRSUserConstants;
import com.cirs.reportit.db.dbconstants.CategoryConstants;
import com.cirs.reportit.db.dbconstants.CommentConstants;
import com.cirs.reportit.db.dbconstants.ComplaintConstants;
import com.cirs.reportit.db.dbconstants.DatabaseConstants;

/**
 * Singleton
 */
public class CirsAppDbHelper {

    private CirsDbHelper cirsDbHelper;
    private SQLiteDatabase sqdb;

    private static CirsAppDbHelper instance;


    private CirsAppDbHelper(Context context) {
        cirsDbHelper = new CirsDbHelper(context);
        sqdb = cirsDbHelper.getWritableDatabase();
    }

    public static CirsAppDbHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (CirsAppDbHelper.class) {
                if (instance == null) {
                    instance = new CirsAppDbHelper(context);
                }
            }
        }
        return instance;
    }

    public static void close(){
        if(instance==null){
            throw new IllegalStateException("Cannot close null instance of "+CirsAppDbHelper.class.getName());
        }else {
            instance.sqdb.close();
        }
    }

    public long insertValue(String tableName, ContentValues contentValues) throws SQLException {
        return sqdb.insertOrThrow(tableName, null, contentValues);
    }

    public Cursor getValues(String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return sqdb.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public int updateValues(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return sqdb.update(table, values, whereClause, whereArgs);
    }

    private class CirsDbHelper extends SQLiteOpenHelper {

        public CirsDbHelper(Context context) {
            super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DatabaseConstants.ENABLE_FOREIGN_KEY_SUPPORT);
            db.execSQL(CategoryConstants.CREATE_TABLE_CATEGORY);
            db.execSQL(CIRSUserConstants.CREATE_TABLE_CIRSUSER);
            db.execSQL(CommentConstants.CREATE_TABLE_COMMENT);
            db.execSQL(ComplaintConstants.CREATE_TABLE_COMPLAINT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
