package com.cirs.reportit.db.dbhelpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import com.cirs.entities.Admin;
import com.cirs.entities.CIRSUser;
import com.cirs.entities.Category;
import com.cirs.entities.Comment;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.db.dbconstants.CIRSUserConstants;
import com.cirs.reportit.db.dbconstants.CategoryConstants;
import com.cirs.reportit.db.dbconstants.CommentConstants;
import com.cirs.reportit.db.dbconstants.ComplaintConstants;
import com.cirs.reportit.db.dbconstants.DatabaseConstants;
import com.cirs.reportit.utils.Constants;
import com.pixplicity.easyprefs.library.Prefs;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cirs.reportit.db.dbconstants.ComplaintConstants.COLUMN_TITLE;

public class QueryHelper {

    private ContentResolver contentResolver;
    private ContentValues contentValues;

    private Long adminId;

    public QueryHelper(Context context) {
        contentResolver = context.getContentResolver();
        adminId = Prefs.getLong(Constants.SPUD_ADMIN_ID, -1);
    }

    public void insertOrUpdateCategory(List<Category> categoryList) {
        for (Category category : categoryList) {
            contentValues = new ContentValues();
            contentValues.put(CategoryConstants.COLUMN_ID, category.getId());
            contentValues.put(CategoryConstants.COLUMN_NAME, category.getName());
            contentValues.put(CategoryConstants.COLUMN_ACTIVE, category.getActive().toString());
            contentValues.put(CategoryConstants.COLUMN_ADMIN_ID, adminId);
            try {
                contentResolver.insert(CategoryConstants.TABLE_CATEGORY_URI, contentValues);
            } catch (SQLException e) {
                e.printStackTrace();
                contentResolver.update(CategoryConstants.TABLE_CATEGORY_URI, contentValues,
                        CategoryConstants.COLUMN_ID + "=?", new String[]{category.getId() + ""});
            }
        }
    }

    public void insertOrUpdateCirsUser(CIRSUser cirsUser) {
        contentValues = new ContentValues();
        contentValues.put(CIRSUserConstants.COLUMN_ID, cirsUser.getId());
        contentValues.put(CIRSUserConstants.COLUMN_FIRST_NAME, cirsUser.getFirstName());
        contentValues.put(CIRSUserConstants.COLUMN_LAST_NAME, cirsUser.getLastName());
        contentValues.put(CIRSUserConstants.COLUMN_GENDER, cirsUser.getGender());
        contentValues.put(CIRSUserConstants.COLUMN_DOB, cirsUser.getDob());
        contentValues.put(CIRSUserConstants.COLUMN_EMAIL, cirsUser.getEmail());
        contentValues.put(CIRSUserConstants.COLUMN_PHONE, cirsUser.getPhone());
        contentValues.put(CIRSUserConstants.COLUMN_PROFILE_PIC, cirsUser.getProfilePic());
        contentValues.put(CIRSUserConstants.COLUMN_ADMIN_ID, adminId);
        try {
            contentResolver.insert(CIRSUserConstants.TABLE_CIRSUSER_URI, contentValues);
        } catch (SQLException e) {
            e.printStackTrace();
            contentResolver.update(CIRSUserConstants.TABLE_CIRSUSER_URI, contentValues,
                    CIRSUserConstants.COLUMN_ID + "=?", new String[]{cirsUser.getId() + ""});
        }
    }

    public void insertOrUpdateComment(Comment comment) {
        contentValues = new ContentValues();
        CIRSUser user = comment.getUser();
        contentValues.put(CommentConstants.COLUMN_ID, comment.getId());
        contentValues.put(CommentConstants.COLUMN_CIRSUSER_ID, comment.getUser().getId());
        contentValues.put(CommentConstants.COLUMN_COMMENT, comment.getData());
        contentValues.put(CommentConstants.COLUMN_TIMESTAMP, comment.getTime().toString());
        contentValues.put(CommentConstants.COLUMN_COMPLAINT_ID, comment.getComplaint().getId());
        try {
            contentResolver.insert(CommentConstants.TABLE_COMMENT_URI, contentValues);
        } catch (SQLException e) {
            e.printStackTrace();
            contentResolver.update(CommentConstants.TABLE_COMMENT_URI, contentValues,
                    CommentConstants.COLUMN_ID + "=?", new String[]{comment.getId() + ""});
        }
    }

    public void insertOrUpdateComplaint(Complaint complaint) {
        contentValues = new ContentValues();
        contentValues.put(ComplaintConstants.COLUMN_ID, complaint.getId());
        contentValues.put(ComplaintConstants.COLUMN_CATEGORY_ID, complaint.getCategory().getId());
        contentValues.put(COLUMN_TITLE, complaint.getTitle());
        contentValues.put(ComplaintConstants.COLUMN_DESCRIPTION, complaint.getDescription());
        contentValues.put(ComplaintConstants.COLUMN_LOCATION, complaint.getLocation());
        contentValues.put(ComplaintConstants.COLUMN_LANDMARK, complaint.getLandmark());
        contentValues.put(ComplaintConstants.COLUMN_COMPLAINT_PIC, complaint.getComplaintPic());
        contentValues.put(ComplaintConstants.COLUMN_CIRSUSER_ID, complaint.getUser().getId());
        contentValues.put(ComplaintConstants.COLUMN_TIMESTAMP, complaint.getTimestamp().toString());
        contentValues.put(ComplaintConstants.COLUMN_STATUS, complaint.getStatus());
        contentValues.put(ComplaintConstants.COLUMN_UPVOTES, complaint.getUpvotes());
        contentValues.put(ComplaintConstants.COLUMN_BOOKMARKED, complaint.isBookmarked());
        contentValues.put(ComplaintConstants.COLUMN_UPVOTED, complaint.isUpvoted());
        contentValues.put(ComplaintConstants.COLUMN_COMMENT_COUNT, complaint.getCommentCount());
        try {
            contentResolver.insert(ComplaintConstants.TABLE_COMPLAINT_URI, contentValues);
        } catch (SQLException e) {
            e.printStackTrace();
            contentResolver.update(ComplaintConstants.TABLE_COMPLAINT_URI, contentValues,
                    ComplaintConstants.COLUMN_ID + "=?", new String[]{complaint.getId() + ""});
        }
    }

    public List<Category> getCategoryList() {

        List<Category> categoryList = null;

        Cursor cursor = contentResolver.query(CategoryConstants.TABLE_CATEGORY_URI, null, null, null, null);

        if (cursor != null) {
            categoryList = new ArrayList<>();
            Category category;
            while (cursor.moveToNext()) {
                category = new Category();
                category.setId(cursor.getLong(cursor.getColumnIndex(CategoryConstants.COLUMN_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(CategoryConstants.COLUMN_NAME)));
                category.setActive(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(CategoryConstants.COLUMN_ACTIVE))));
                Admin admin = new Admin();
                admin.setId(adminId);
                category.setAdmin(admin);
                categoryList.add(category);
            }
            cursor.close();
        }
        return categoryList;
    }

    public Category getCategory(long id) {
        Category category = null;

        Cursor cursor = contentResolver.query(CategoryConstants.TABLE_CATEGORY_URI, null, CategoryConstants.COLUMN_ID + "=?", new String[]{Long.toString(id)}, null);

        if (cursor != null) {
            category = new Category();
            while (cursor.moveToNext()) {
                category.setId(cursor.getLong(cursor.getColumnIndex(CategoryConstants.COLUMN_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(CategoryConstants.COLUMN_NAME)));
                category.setActive(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(CategoryConstants.COLUMN_ACTIVE))));
                Admin admin = new Admin();
                admin.setId(adminId);
                category.setAdmin(admin);
            }
            cursor.close();
        }

        return category;
    }

    public CIRSUser getCirsUser(long id) {

        CIRSUser user = null;

        Cursor cursor = contentResolver.query(CIRSUserConstants.TABLE_CIRSUSER_URI, null, CIRSUserConstants.COLUMN_ID + "=?", new String[]{Long.toString(id)}, null);

        if (cursor != null) {
            user = new CIRSUser();
            while (cursor.moveToNext()) {
                user.setId(cursor.getLong(cursor.getColumnIndex(CIRSUserConstants.COLUMN_ID)));
                user.setFirstName(cursor.getString(cursor.getColumnIndex(CIRSUserConstants.COLUMN_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndex(CIRSUserConstants.COLUMN_LAST_NAME)));
                user.setGender(cursor.getString(cursor.getColumnIndex(CIRSUserConstants.COLUMN_GENDER)));
                user.setDob(cursor.getString(cursor.getColumnIndex(CIRSUserConstants.COLUMN_DOB)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(CIRSUserConstants.COLUMN_EMAIL)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(CIRSUserConstants.COLUMN_PHONE)));
                user.setProfilePic(cursor.getBlob(cursor.getColumnIndex(CIRSUserConstants.COLUMN_PROFILE_PIC)));
                Admin admin = new Admin();
                admin.setId(adminId);
                user.setAdmin(admin);
                user.setComplaints(getComplaintWithoutComment(user.getId()));
            }
            cursor.close();
        }

        return user;
    }

    public Comment getComment(long id) {

        //CIRSUser object has only id
        //Complaint object has only id

        Comment comment = null;

        Cursor cursor = contentResolver.query(CommentConstants.TABLE_COMMENT_URI, null, CommentConstants.COLUMN_ID + "=?", new String[]{Long.toString(id)}, null);

        if (cursor != null) {
            comment = new Comment();
            while (cursor.moveToNext()) {
                comment.setId(cursor.getLong(cursor.getColumnIndex(CommentConstants.COLUMN_ID)));
                CIRSUser user = new CIRSUser();
                user.setId(cursor.getLong(cursor.getColumnIndex(CommentConstants.COLUMN_CIRSUSER_ID)));
                comment.setUser(user);
                comment.setData(cursor.getString(cursor.getColumnIndex(CommentConstants.COLUMN_COMMENT)));
                comment.setTime(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(CommentConstants.COLUMN_TIMESTAMP))));
                Complaint complaint = new Complaint();
                complaint.setId(cursor.getLong(cursor.getColumnIndex(CommentConstants.COLUMN_COMPLAINT_ID)));
                comment.setComplaint(complaint);
            }
            cursor.close();
        }

        return comment;
    }

    public Comment[] getCommentArray(long complaintId) {
        Comment[] comments = null;

        Cursor cursor = contentResolver.query(CommentConstants.TABLE_COMMENT_URI, null, CommentConstants.COLUMN_COMPLAINT_ID + "=?", new String[]{Long.toString(complaintId)}, null);

        if (cursor != null) {
            comments = new Comment[cursor.getCount()];
            Comment comment;
            while (cursor.moveToNext()) {
                comment = new Comment();
                comment.setId(cursor.getLong(cursor.getColumnIndex(CommentConstants.COLUMN_ID)));
                CIRSUser user = new CIRSUser();
                user.setId(cursor.getLong(cursor.getColumnIndex(CommentConstants.COLUMN_CIRSUSER_ID)));
                comment.setUser(user);
                comment.setData(cursor.getString(cursor.getColumnIndex(CommentConstants.COLUMN_COMMENT)));
                comment.setTime(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(CommentConstants.COLUMN_TIMESTAMP))));
                Complaint complaint = new Complaint();
                complaint.setId(cursor.getLong(cursor.getColumnIndex(CommentConstants.COLUMN_COMPLAINT_ID)));
                comment.setComplaint(complaint);
                comments[cursor.getPosition()] = comment;
            }
            cursor.close();
        }


        return comments;
    }

    public Complaint getComplaintWithComment(long id) {

        //CIRSUser object has only id

        Complaint complaint = null;

        Cursor cursor = contentResolver.query(ComplaintConstants.TABLE_COMPLAINT_URI, null, ComplaintConstants.COLUMN_ID + "=?", new String[]{Long.toString(id)}, null);

        if (cursor != null) {
            complaint = new Complaint();
            while (cursor.moveToNext()) {
                complaint.setId(cursor.getLong(cursor.getColumnIndex(ComplaintConstants.COLUMN_ID)));
                complaint.setCategory(getCategory(cursor.getLong(cursor.getColumnIndex(ComplaintConstants.COLUMN_CATEGORY_ID))));
                complaint.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                complaint.setDescription(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_DESCRIPTION)));
                complaint.setLocation(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_LOCATION)));
                complaint.setLandmark(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_LANDMARK)));
                complaint.setComplaintPic(cursor.getBlob(cursor.getColumnIndex(ComplaintConstants.COLUMN_COMPLAINT_PIC)));
                CIRSUser user = new CIRSUser();
                user.setId(cursor.getLong(cursor.getColumnIndex(ComplaintConstants.COLUMN_CIRSUSER_ID)));
                complaint.setUser(user);
                complaint.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_TIMESTAMP))));
                complaint.setStatus(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_STATUS)));
                complaint.setUpvotes(cursor.getInt(cursor.getColumnIndex(ComplaintConstants.COLUMN_UPVOTES)));
                complaint.setBookmarked(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_BOOKMARKED))));
                complaint.setUpvoted(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_UPVOTED))));
                complaint.setCommentCount(cursor.getInt(cursor.getColumnIndex(ComplaintConstants.COLUMN_COMMENT_COUNT)));
                complaint.setComments(new ArrayList<Comment>(Arrays.asList(getCommentArray(complaint.getId()))));
            }
            cursor.close();
        }
        return complaint;
    }

    public List<Complaint> getComplaintWithoutComment(long userId) {

        //Comment[] object is null
        //CIRSUser object has only id

        List<Complaint> complaintList = null;

        Cursor cursor = contentResolver.query(ComplaintConstants.TABLE_COMPLAINT_URI, null, ComplaintConstants.COLUMN_CIRSUSER_ID + "=?", new String[]{Long.toString(userId)}, null);

        if (cursor != null) {
            complaintList = new ArrayList<>();
            Complaint complaint;
            while (cursor.moveToNext()) {
                complaint = new Complaint();
                complaint.setId(cursor.getLong(cursor.getColumnIndex(ComplaintConstants.COLUMN_ID)));
                complaint.setCategory(getCategory(cursor.getLong(cursor.getColumnIndex(ComplaintConstants.COLUMN_CATEGORY_ID))));
                complaint.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                complaint.setDescription(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_DESCRIPTION)));
                complaint.setLocation(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_LOCATION)));
                complaint.setLandmark(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_LANDMARK)));
                complaint.setComplaintPic(cursor.getBlob(cursor.getColumnIndex(ComplaintConstants.COLUMN_COMPLAINT_PIC)));
                CIRSUser user = new CIRSUser();
                user.setId(cursor.getLong(cursor.getColumnIndex(ComplaintConstants.COLUMN_CIRSUSER_ID)));
                complaint.setUser(user);
                complaint.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_TIMESTAMP))));
                complaint.setStatus(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_STATUS)));
                complaint.setUpvotes(cursor.getInt(cursor.getColumnIndex(ComplaintConstants.COLUMN_UPVOTES)));
                complaint.setBookmarked(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_BOOKMARKED))));
                complaint.setUpvoted(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_UPVOTED))));
                complaint.setCommentCount(cursor.getInt(cursor.getColumnIndex(ComplaintConstants.COLUMN_COMMENT_COUNT)));
                complaintList.add(complaint);
            }
            cursor.close();
        }

        return complaintList;

    }

    /**
     * Returns the complaints that have been bookmarked by the current user
     *
     * @return a list of bookmarked complaint, or an empty list if there are none.
     */
    public List<Complaint> getBookmarkedComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        Cursor cursor = contentResolver.query(ComplaintConstants.TABLE_COMPLAINT_URI, null, ComplaintConstants.COLUMN_BOOKMARKED + " =?", new String[]{"1"}, null);
        while (cursor.moveToNext()) {
            Complaint complaint = new Complaint();
            complaint.setId(cursor.getLong(cursor.getColumnIndex(ComplaintConstants.COLUMN_ID)));
            complaint.setCategory(getCategory(cursor.getLong(cursor.getColumnIndex(ComplaintConstants.COLUMN_CATEGORY_ID))));
            complaint.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            complaint.setDescription(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_DESCRIPTION)));
            complaint.setLocation(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_LOCATION)));
            complaint.setLandmark(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_LANDMARK)));
            complaint.setComplaintPic(cursor.getBlob(cursor.getColumnIndex(ComplaintConstants.COLUMN_COMPLAINT_PIC)));
            CIRSUser user = new CIRSUser();
            user.setId(cursor.getLong(cursor.getColumnIndex(ComplaintConstants.COLUMN_CIRSUSER_ID)));
            complaint.setUser(user);
            complaint.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_TIMESTAMP))));
            complaint.setStatus(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_STATUS)));
            complaint.setUpvotes(cursor.getInt(cursor.getColumnIndex(ComplaintConstants.COLUMN_UPVOTES)));
            complaint.setBookmarked(true);
            complaint.setUpvoted(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ComplaintConstants.COLUMN_UPVOTED))));
            complaint.setCommentCount(cursor.getInt(cursor.getColumnIndex(ComplaintConstants.COLUMN_COMMENT_COUNT)));
            complaints.add(complaint);
        }
        ReportItApplication.setBookmarkedComplaintList(complaints);
        cursor.close();
        return complaints;
    }

    public boolean isBookmarked(Complaint complaint) {
        List<Complaint> complaintList = ReportItApplication.getBookmarkedComplaintList();
        if (complaintList == null) {
            complaintList = getBookmarkedComplaints();
        }
        return complaintList.contains(complaint);
    }


    public void emptyAllTables() {
        Uri[] uris = {ComplaintConstants.TABLE_COMPLAINT_URI, CategoryConstants.TABLE_CATEGORY_URI, CIRSUserConstants.TABLE_CIRSUSER_URI, CommentConstants.TABLE_COMMENT_URI};
        for (Uri uri : uris) {
            contentResolver.delete(uri, null, null);
        }
    }
}
