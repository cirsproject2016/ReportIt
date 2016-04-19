package com.cirs.reportit.offline;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.NoConnectionError;
import com.cirs.entities.Complaint;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages all offline requests. An instance must be obtained by using
 * <code>
 * OfflineManager manager=OfflineManager.getInstance(context);
 * </code>
 * It observes network changes and sends the enqueued requests once connection
 * is established.
 *
 * @author Rohan Kamat
 */
public class OfflineManager implements ConnectivityObserver {
    private static final String TAG = OfflineManager.class.getSimpleName();
    private static Context mContext;
    private static OfflineManager mInstance;
    private static final ArrayBlockingQueue<EnqueuedRequest<Complaint>> complaintRequestQueue = new ArrayBlockingQueue<>(20);
    private static final ArrayBlockingQueue<EnqueuedImageRequest> imageRequestQueue = new ArrayBlockingQueue<>(20);

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

    public void enqueueComplaintImage(Complaint c, byte[] imgContent, VolleyRequest.FileType format) {
        RequestFileManager.createSavedComplaintImageFile(mContext, c, imgContent, format);
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
                            complaintRequestQueue.put(req);
                            Log.i(TAG, req.toString());
                            file.delete();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (fileName.matches("\\d+\\.(png|jpg|jpeg)")) {
                    EnqueuedImageRequest imgRequest = RequestFileManager.getImageRequestFromFile(mContext, fileName, (int) file.length());
                    imageRequestQueue.add(imgRequest);
                }
            }
        }
    }

    static class ImageRequestConsumer implements Runnable {
        @Override
        public void run() {

            while (true) {
                try {
                    EnqueuedImageRequest imageRequest = imageRequestQueue.poll(5, TimeUnit.MINUTES);
                    new VolleyRequest<Integer>(mContext).makeImageRequest(imageRequest.getUrl(), imageRequest.getMethod(), imageRequest.getFileType(), imageRequest.getContent(), null, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }
    }

    static class ComplaintRequestConsumer implements Runnable {
        @Override
        public void run() {
            Log.i(TAG, "here in request consumer");
            EnqueuedRequest<Complaint> requestObject = null;
            while (true) {
                try {
                    requestObject = complaintRequestQueue.poll(5, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                if (requestObject != null) {
                    final EnqueuedRequest<Complaint> request = requestObject;
                    Log.i(TAG, "retrieved from request queue " + request);
                    new VolleyRequest<Complaint>(mContext).makeGsonRequest(request.getMethod(), request.getUrl(), request.getRequestBody(), new Response.Listener<Complaint>() {
                        @Override
                        public void onResponse(final Complaint response) {
                            String url = Generator.getUrltoUploadComplaintPic(response);
                            new VolleyRequest<Integer>(mContext).makeImageRequest(url,
                                    "PUT", VolleyRequest.FileType.PNG, request.getRequestBody().getComplaintPic(), new Response.Listener<Integer>() {
                                        public void onResponse(Integer i) {
                                            //do nothing
                                            //not null to avoid NPE
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {
                                            Log.i(TAG, error.networkResponse.statusCode + "");
                                            if (error instanceof NoConnectionError) {
                                                mInstance.enqueueComplaintImage(response, request.getRequestBody().getComplaintPic(), VolleyRequest.FileType.PNG);
                                            }
                                        }
                                    });
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof NoConnectionError) {
                                //device offline enqueue again
                                mInstance.enqueueComplaintRequest(request.getRequestBody());
                            }
                        }
                    }, Complaint.class);

                }
            }
        }
    }

    @Override
    public void onConnected() {
        ExecutorService prod = Executors.newSingleThreadExecutor();
        ExecutorService cons = Executors.newSingleThreadExecutor();
        ExecutorService imgCons = Executors.newSingleThreadExecutor();

        prod.execute(new RequestCreator());
        cons.execute(new ComplaintRequestConsumer());
        imgCons.execute(new ImageRequestConsumer());

        prod.shutdown();
        cons.shutdown();
    }
}
