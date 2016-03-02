package com.cirs.gcm;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cirs.reportit.ui.activities.ViewComplaintActivity;
import com.cirs.reportit.utils.NotificationHelper;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Rohan on 02-03-2016.
 */
public class GcmService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";
    private static final String EXTRA_COMPLAINT_ID = "complaintId";
    private static final String EXTRA_NEW_STATUS = "newStatus";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        // normal downstream message.
        Long compId = data.getLong(EXTRA_COMPLAINT_ID);
        String status = data.getString(EXTRA_NEW_STATUS);
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "compId: " + compId);
        Intent i = new Intent(this, ViewComplaintActivity.class);
        i.putExtra(ViewComplaintActivity.EXTRA_COMPLAINT_ID, compId);
        PendingIntent pi = PendingIntent.getActivity(this, 100, i, PendingIntent.FLAG_ONE_SHOT);
        NotificationHelper.showNotification(this, "ReportIt", "The status of your complaint was changed to " + status, pi);

    }

}
