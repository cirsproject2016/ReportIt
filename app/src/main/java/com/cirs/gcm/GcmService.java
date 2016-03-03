package com.cirs.gcm;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
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
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack
		stackBuilder.addParentStack(ViewComplaintActivity.class);
		// Adds the Intent to the top of the stack
		stackBuilder.addNextIntent(i);
		// Gets a PendingIntent containing the entire back stack
		PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationHelper.showNotification(this, "ReportIt", "The status of your complaint was changed to " + status, pi);

    }

}
