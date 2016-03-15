package com.cirs.reportit.utils;

public final class Constants {

    //Global Constants
    public static final String FILE_PATH_PROFILE_PIC = "profile.jpg";
    public static final String FILE_PATH_COMPLAINT_PIC = "complaint.jpg";
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    //Shared Preferences Constants for User Details
    //Name of Shared Pref
    public static final String SHARED_PREF_USER_DETAILS = "prefUserInfo";
    //Boolean Pref
    public static final String SPUD_IS_SIGNED_IN = "isSignedIn";
    public static final String SPUD_IS_PROFILE_CREATED = "isProfileCreated";
    public static final String SPUD_IS_IMAGE_SET = "isImageSet";
    //Long Pref
    public static final String SPUD_USER_ID = "userid";
    public static final String SPUD_ADMIN_ID = "adminid";
    //String Pref
    public static final String SPUD_USERNAME = "username";
    public static final String SPUD_FIRSTNAME = "firstname";
    public static final String SPUD_LASTNAME = "lastname";
    public static final String SPUD_GENDER = "gender";
    public static final String SPUD_DOB = "dob";
    public static final String SPUD_EMAIL = "email";
    public static final String SPUD_PHONE = "phone";
    //String Set Pref
    public static final String SPUD_UPVOTED_COMPLAINTS = "upvotedUserComplaints";
    public static final String SPUD_BOOKMARKED_COMPLAINTS = "bookmarkedUserComplaints";

    //Splash Screen Activity Constants
    public static final int SPLASH_SCREEN_TIMEOUT = 3000;

    //Create Profile Activity Constants
    public static final String REGEX_PHONE = "^[789]\\d{9}$";

    //Genymotion Base URI Path
    public static final String BASE_URI = "http://10.0.3.2:8080/CIRSWeb/res";
    public static final String BASE_URI_FOR_IMAGE = "http://10.0.3.2:8080/CIRSWeb/image";

    //Alternate URI Path
//    public static final String BASE_URI = "http://192.168.0.103:8080/CIRSWeb/res";
//    public static final String BASE_URI_FOR_IMAGE = "http://192.168.0.103:8080/CIRSWeb/image";
}
