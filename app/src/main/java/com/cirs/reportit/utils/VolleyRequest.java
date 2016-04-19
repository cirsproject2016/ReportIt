package com.cirs.reportit.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Used to send a network request.
 * Dependencies:
 * - Volley https://android.googlesource.com/platform/frameworks/volley,
 * - GSON
 * Example usage
 * new VolleyRequest<User>(context).makeGsonRequest(GET,"localhost/index",null,listener,errorListener,User.class);
 * <p/>
 * Created by Rohan Kamat on 30-Jan-16.
 */

public class VolleyRequest<T> {
    private static RequestQueue queue;
    private static Context context;

    public VolleyRequest(Context context) {
        VolleyRequest.context = context;
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
    }


    /**
     * Creates and makes a network request with the given parameters, and delivers the response on the UI thread.
     * Can be called directly on the UI thread.
     *
     * @param method        one of the constants from Request.Method.
     * @param url           the URL where the request is to be sent.
     * @param object        optional object for body of request. Null for GET requests.
     * @param listener      the callback for receiving the response. It is called on the UI thread.
     * @param errorListener the callback for receiving errors.
     * @param clazz         the Class to which the received JSON must be parsed.
     */
    public void makeGsonRequest(int method, String url, Object object, Listener<T> listener, ErrorListener errorListener, final Class<T> clazz) {
        if (url == null || clazz == null) {
            throw new NullPointerException("Url and class cannot be null");
        }
        Request<T> request = new GsonRequest<>(clazz, method, url, object, listener, errorListener);
        //request.setRetryPolicy(new DefaultRetryPolicy(
        //        10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }

    public enum FileType {
        PNG("image/png"), JPEG("image/jpg"), BMP("image/bmp");
        private String type;

        FileType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public void makeImageRequest(final String url, final String method, final FileType fileType, final byte[] content, final Listener<Integer> listener, final ErrorListener errorListener) {
        final Handler handler = new Handler();
        Runnable download = new Runnable() {
            @Override
            public void run() {
                try {
                    final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod(method.toUpperCase());
                    conn.setRequestProperty("Content-Type", fileType.getType());
                    OutputStream fos = conn.getOutputStream();
                    fos.write(content);
                    final NetworkResponse resp = new NetworkResponse(conn.getResponseCode(), null, null, true);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(resp.statusCode);
                            if (resp.statusCode >= 200 && resp.statusCode < 300) {
                                listener.onResponse(resp.statusCode);
                            } else {

                                VolleyError error = new VolleyError(resp);
                                errorListener.onErrorResponse(error);
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.execute(download);
        ex.shutdown();
    }

    private static class GsonRequest<T> extends JsonRequest<T> {

        private static Gson gson = getGson();
        private Listener<T> listener;

        private static Gson getGson() {
            return new GsonBuilder().setDateFormat("dd MMM yyyy HH:mm:ss").addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getName().equalsIgnoreCase("upvotes") || f.getName().equalsIgnoreCase("upvoted");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            }).setPrettyPrinting().create();
        }

        private Class<T> clazz;

        public GsonRequest(Class<T> clazz, int method, String url, Object requestBody, Listener<T> listener, ErrorListener errorListener) {
            super(method, url, gson.toJson(requestBody), listener, errorListener);
            String s = gson.toJson(requestBody);
            Log.d("VolleyRequest", "request Body" + s);
            Log.d("VolleyRequest", "request url" + url);
            this.clazz = clazz;
            this.listener = listener;
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            String json = new String(response.data);
            Log.d("VolleyRequest", "status: " + response.statusCode);
            Log.d("VolleyRequest", "data: " + json);
            if (response.statusCode >= 200 && response.statusCode < 300) {
                return listener == null ? null : Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
            } else {
                VolleyError v = new VolleyError(response);
                return Response.error(v);
            }
        }
    }
}
