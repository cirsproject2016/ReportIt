package com.cirs.gcm;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Rohan on 02-03-2016.
 */
public final class GcmUtils {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GcmUtils(){
        throw new AssertionError();
    }
    public static final String SENT_TOKEN_TO_SERVER="token_sent";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static boolean checkPlayServices(Activity context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("GCMUtils", "This device is not supported.");
            }
            return false;
        }
        return true;
    }


}
