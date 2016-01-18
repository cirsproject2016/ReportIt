package com.cirs.reportit.utils;

import com.example.kshitij.reportit.R;

public final class Constants {

    //Global Constants
    public static final String FILE_PATH_PROFILE_PIC = "profile.jpg";
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    //Shared Preferences Constants for User Details
    //Name of Shared Pref
    public static final String SHARED_PREF_USER_DETAILS = "prefUserInfo";
    //Boolean Pref
    public static final String SPUD_IS_SIGNED_IN = "isSignedIn";
    public static final String SPUD_IS_PROFILE_CREATED = "isProfileCreated";
    public static final String SPUD_IS_IMAGE_SET = "isImageSet";
    //String Pref
    public static final String SPUD_USERNAME = "username";
    public static final String SPUD_FIRSTNAME = "firstname";
    public static final String SPUD_LASTNAME = "lastname";
    public static final String SPUD_GENDER = "gender";
    public static final String SPUD_DOB = "dob";
    public static final String SPUD_EMAIL = "email";
    public static final String SPUD_PHONE = "phone";

    //Splash Screen Activity Constants
    public static final int SPLASH_SCREEN_TIMEOUT = 3000;

    //Create Profile Activity Constants
    public static final String REGEX_PHONE = "^[789]\\d{9}$";
}
