package com.cirs.reportit;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cirs.entities.CIRSUser;
import com.cirs.entities.Category;
import com.cirs.entities.Complaint;
import com.cirs.reportit.db.dbhelpers.CirsAppDbHelper;
import com.cirs.reportit.db.dbhelpers.QueryHelper;
import com.cirs.reportit.offline.OfflineManager;
import com.cirs.reportit.utils.Constants;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportItApplication extends Application {

    private static final String TAG = ReportItApplication.class.getSimpleName();

    private static Context mAppContext;

    private static Set<Category> categories = new HashSet<>();

    private static Set<String> upvotedComplaintsStringIds = new HashSet<>();

    private static List<Complaint> bookmarkedComplaintList;

    private static String gcmToken;

    public static String getGcmToken() {
        return gcmToken;
    }

    public static void setGcmToken(String gcmToken) {
        ReportItApplication.gcmToken = gcmToken;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        CirsAppDbHelper.getInstance(this);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        OfflineManager.getInstance(this);
        //Create an instance of offline manager
        //So it can listen to network changes

        mAppContext = getApplicationContext();
        fetchCategories();
        fetchAllComplaintsByThisUser();
    }


    public static CIRSUser getCirsUser() {
        CIRSUser cirsUser = new CIRSUser();
        cirsUser.setId(Prefs.getLong(Constants.SPUD_USER_ID, -1));
        cirsUser.setFirstName(Prefs.getString(Constants.SPUD_FIRSTNAME, null));
        cirsUser.setLastName(Prefs.getString(Constants.SPUD_LASTNAME, null));
        cirsUser.setGender(Prefs.getString(Constants.SPUD_GENDER, null));
        cirsUser.setDob(Prefs.getString(Constants.SPUD_DOB, null));
        cirsUser.setEmail(Prefs.getString(Constants.SPUD_EMAIL, null));
        cirsUser.setPhone(Prefs.getString(Constants.SPUD_PHONE, null));
        return cirsUser;
    }

    public static void setCirsUser(CIRSUser cirsUser) {
        Prefs.putLong(Constants.SPUD_USER_ID, cirsUser.getId());
        Prefs.putString(Constants.SPUD_FIRSTNAME, cirsUser.getFirstName());
        Prefs.putString(Constants.SPUD_LASTNAME, cirsUser.getLastName());
        Prefs.putString(Constants.SPUD_GENDER, cirsUser.getGender());
        Prefs.putString(Constants.SPUD_DOB, cirsUser.getDob());
        Prefs.putString(Constants.SPUD_EMAIL, cirsUser.getEmail());
        Prefs.putString(Constants.SPUD_PHONE, cirsUser.getPhone());
    }

    public static Context getmAppContext() {
        return mAppContext;
    }

    public static Set<Category> getCategories() {
        return categories;
    }

    public static void fetchCategories() {
        new VolleyRequest<Category[]>(mAppContext).makeGsonRequest(
                Request.Method.GET,
                Generator.getURLtoFetchCategoriesFromServer(),
                null,
                new Response.Listener<Category[]>() {
                    @Override
                    public void onResponse(Category[] response) {
                        for (Category category : response) {
                            if (!categories.contains(category)) {
                                categories.add(category);
                            }
                        }
                        System.out.println(response);
                        new QueryHelper(mAppContext).insertOrUpdateCategory(new ArrayList<Category>(categories));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        categories = new HashSet<>(new QueryHelper(mAppContext).getCategoryList());
                    }
                },
                Category[].class);
    }

    public static void fetchAllComplaintsByThisUser() {
        new VolleyRequest<CIRSUser>(mAppContext).makeGsonRequest(
                Request.Method.GET,
                Generator.getURLtoGetUserById(getCirsUser().getId()),
                null,
                new Response.Listener<CIRSUser>() {
                    @Override
                    public void onResponse(CIRSUser response) {
                        for (Complaint complaint : response.getComplaints()) {
                            complaint.setUser(response);
                            new QueryHelper(mAppContext).insertOrUpdateComplaint(complaint);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                },
                CIRSUser.class);
    }

    public static void fetchUpvotedComplaintsByThisUser() {
        new VolleyRequest<Complaint[]>(mAppContext).makeGsonRequest(
                Request.Method.GET,
                Generator.getURLtoFetchUpvotedComplaints(),
                null,
                new Response.Listener<Complaint[]>() {
                    @Override
                    public void onResponse(Complaint[] response) {
                        Toast.makeText(mAppContext, "Successfully fetched Upvoted Complaints", Toast.LENGTH_SHORT).show();
                        for (Complaint complaint : response) {
                            Log.i(TAG, "persisting complaint with id: " + complaint.getId());
                            upvotedComplaintsStringIds.add(complaint.getId().toString());
                        }
                        Prefs.remove(Constants.SPUD_UPVOTED_COMPLAINTS);
                        Prefs.putOrderedStringSet(Constants.SPUD_UPVOTED_COMPLAINTS, upvotedComplaintsStringIds);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mAppContext, "Could not fetch Upvoted Complaints", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                        HashSet<String> strings = new HashSet<>(Prefs.getOrderedStringSet(Constants.SPUD_UPVOTED_COMPLAINTS, new HashSet<String>()));
                        upvotedComplaintsStringIds = new HashSet<String>(strings);
                    }
                },
                Complaint[].class);
    }

    public static void addIdToUpvotedSet(Long complaintId) {
        upvotedComplaintsStringIds.add(complaintId.toString());
        Prefs.remove(Constants.SPUD_UPVOTED_COMPLAINTS);
        Prefs.putOrderedStringSet(Constants.SPUD_UPVOTED_COMPLAINTS, upvotedComplaintsStringIds);
    }

    public static boolean checkIfComplaintIsUpvoted(Long complaintId) {
        if (upvotedComplaintsStringIds != null && upvotedComplaintsStringIds.contains(complaintId.toString()))
            return true;
        return false;
    }

    public static List<Complaint> getBookmarkedComplaintList() {
        return bookmarkedComplaintList;
    }

    public static void setBookmarkedComplaintList(List<Complaint> bookmarkedComplaintList) {
        ReportItApplication.bookmarkedComplaintList = bookmarkedComplaintList;
    }
}