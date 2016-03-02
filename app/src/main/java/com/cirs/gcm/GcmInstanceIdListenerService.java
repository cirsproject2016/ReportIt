package com.cirs.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Rohan on 02-03-2016.
 */
public class GcmInstanceIdListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        /*Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);*/

    }
}
