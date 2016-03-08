package com.cirs.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cirs.entities.CIRSUser;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Rohan on 02-03-2016.
 */
public final class GcmUtils {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GcmUtils() {
        throw new AssertionError();
    }

    public static final String SENT_TOKEN_TO_SERVER = "token_sent";
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

    public static void invalidateToken(final Context context) {
        CIRSUser user = ReportItApplication.getCirsUser();
        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InstanceID.getInstance(context).deleteToken(ReportItApplication.getGcmToken(), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("GCMUtils", "error deleting token " + ReportItApplication.getGcmToken());
                }
            }
        });
        ex.shutdown();
        sendRegistrationToServer(context, null);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = prefs.edit();
        edit.putBoolean(SENT_TOKEN_TO_SERVER, false);
        edit.commit();
    }

    public static void sendRegistrationToServer(Context context, String token) {
        // Add custom implementation, as needed.
        Log.i("TokenRegistration", token + "");
        CIRSUser user = ReportItApplication.getCirsUser();

        TokenEntity entity = new TokenEntity(user.getId(), token);
        String url = Generator.getURLtoAddToken();
        new VolleyRequest<Void>(context.getApplicationContext()).makeGsonRequest(Request.Method.PUT, url, entity, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null) {
                    Log.d("TokenRegistration", "Error status: " + error.networkResponse.statusCode);
                }
            }
        }, Void.class);
    }

    public static class TokenEntity {
        public long id;
        public String token;

        public TokenEntity(long id, String token) {
            this.id = id;
            this.token = token;
        }
    }

}
