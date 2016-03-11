package com.cirs.reportit;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cirs.entities.CIRSUser;
import com.cirs.entities.Category;
import com.cirs.reportit.utils.Constants;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;

import java.util.ArrayList;

public class ReportItApplication extends Application {

    private static SharedPreferences pref;

    private static SharedPreferences.Editor editor;

    private static Context mAppContext;

    private static ArrayList<Category> categories = new ArrayList<>();

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
        mAppContext = getApplicationContext();
        pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);
        fetchCategories();
    }

    public static CIRSUser getCirsUser() {
        CIRSUser cirsUser = new CIRSUser();
        cirsUser.setId(pref.getLong(Constants.SPUD_USER_ID, -1));
        cirsUser.setFirstName(pref.getString(Constants.SPUD_FIRSTNAME, null));
        cirsUser.setLastName(pref.getString(Constants.SPUD_LASTNAME, null));
        cirsUser.setGender(pref.getString(Constants.SPUD_GENDER, null));
        cirsUser.setDob(pref.getString(Constants.SPUD_DOB, null));
        cirsUser.setEmail(pref.getString(Constants.SPUD_EMAIL, null));
        cirsUser.setPhone(pref.getString(Constants.SPUD_PHONE, null));
        return cirsUser;
    }

    public static void setCirsUser(CIRSUser cirsUser) {
        editor = pref.edit();
        editor.putLong(Constants.SPUD_USER_ID, cirsUser.getId());
        editor.putString(Constants.SPUD_FIRSTNAME, cirsUser.getFirstName());
        editor.putString(Constants.SPUD_LASTNAME, cirsUser.getLastName());
        editor.putString(Constants.SPUD_GENDER, cirsUser.getGender());
        editor.putString(Constants.SPUD_DOB, cirsUser.getDob());
        editor.putString(Constants.SPUD_EMAIL, cirsUser.getEmail());
        editor.putString(Constants.SPUD_PHONE, cirsUser.getPhone());
        editor.commit();
    }

    public static Context getmAppContext() {
        return mAppContext;
    }

    public static ArrayList<Category> getCategories() {
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
                            if (!categories.contains(category))
                                categories.add(category);
                        }
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                },
                Category[].class);
    }
}