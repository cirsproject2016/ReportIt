package com.cirs.reportit.offline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohan Kamat on 16-03-2016.
 */
public class ConnectivityChangeMonitor extends BroadcastReceiver {

    private static final String TAG = ConnectivityChangeMonitor.class.getSimpleName();
    private static final List<ConnectivityObserver> observers = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            Log.i(TAG, "Network is connected");
            for (ConnectivityObserver obs : observers) {
                obs.onConnected();
            }
        }
    }

    public static void subscribe(ConnectivityObserver observer) {
        Log.i(TAG, "adding observer");
        observers.add(observer);
    }

    public static void unsubscribe(ConnectivityObserver observer) {
        observers.remove(observer);
    }
}
