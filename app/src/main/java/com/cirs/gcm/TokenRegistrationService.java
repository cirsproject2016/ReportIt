package com.cirs.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import static com.cirs.gcm.GcmUtils.SENT_TOKEN_TO_SERVER;

/**
 * Created by Rohan on 02-03-2016.
 */
public class TokenRegistrationService extends IntentService {
    public static final String TAG=TokenRegistrationService.class.getSimpleName();
    public TokenRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            sendRegistrationToServer(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
/*
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
*/
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        Log.i(TAG,token);
        CIRSUser user=((ReportItApplication)getApplication()).getCirsUser();

        TokenEntity entity=new TokenEntity(user.getId(),token);
        String url= Generator.getURLtoAddToken();
        new VolleyRequest<Void>(getApplicationContext()).makeGsonRequest(Request.Method.PUT, url, entity, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(error.networkResponse!=null) {
                    Log.d(TAG, "Error status: " + error.networkResponse.statusCode);
                }
            }
        }, Void.class);
    }
    public static class TokenEntity{
        public long id;
        public String token;

        public TokenEntity(long id, String token) {
            this.id = id;
            this.token = token;
        }
    }

}
