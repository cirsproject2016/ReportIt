package com.cirs.reportit.offline;

import android.content.Context;
import android.util.Log;

import com.cirs.entities.Complaint;
import com.cirs.reportit.utils.VolleyRequest;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rohan Kamat on 16-03-2016.
 */
public class OfflineManager implements ConnectivityObserver {
    private static final String TAG = OfflineManager.class.getSimpleName();
    private static Context mContext;
    private static OfflineManager mInstance;
    private static final ArrayBlockingQueue<EnqueuedRequest> requestQueue = new ArrayBlockingQueue<>(20);

    private OfflineManager(Context context) {
        mContext = context;
    }


    public static OfflineManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (OfflineManager.class) {
                if (mInstance == null) {
                    mInstance = new OfflineManager(context.getApplicationContext());
                    Log.i(TAG, "subscribing to ConnectivityChangeMonitor");
                    ConnectivityChangeMonitor.subscribe(mInstance);
                }
            }
        }
        return mInstance;
    }

    public void enqueueComplaintRequest(Complaint c) {
        RequestFileManager.createRequestFileForComplaint(mContext, c);
    }

    static class RequestCreator implements Runnable {
        @Override
        public void run() {
            File filesDir = mContext.getFilesDir();
            Log.i(TAG, "in on connected on OfflineManager");
            for (File file : filesDir.listFiles()) {
                String fileName = file.getName();
                Log.i(TAG, "file name:" + fileName);
                if (fileName.endsWith(".json")) {
                    if (fileName.startsWith("complaint")) {
                        EnqueuedRequest<Complaint> req = RequestFileManager.getComplaintRequestFromFile(mContext, fileName);
                        try {
                            requestQueue.put(req);
                            Log.i(TAG, req.toString());
                            file.delete();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    static class RequestConsumer implements Runnable {
        @Override
        public void run() {
            Log.i(TAG, "here in request consumer");
            EnqueuedRequest<?> request = null;
            while (true) {
                try {
                    request = requestQueue.poll(5, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "retrieved from request queue " + request);
                if (request != null) {
                    new VolleyRequest<>(mContext).makeGsonRequest(request.getMethod(), request.getUrl(), request.getRequestBody(), null, null, (Class<Object>) request.getClazz());
                }
            }
        }
    }

    @Override
    public void onConnected() {
        ExecutorService prod = Executors.newSingleThreadExecutor();
        ExecutorService cons = Executors.newSingleThreadExecutor();
        prod.execute(new RequestCreator());
        cons.execute(new RequestConsumer());
        prod.shutdown();
        cons.shutdown();
    }
}
