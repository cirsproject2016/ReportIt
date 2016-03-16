package com.cirs.reportit.offline;

import android.content.Context;
import android.util.Log;

import com.cirs.entities.Complaint;
import com.cirs.reportit.utils.Generator;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.Date;

import static com.android.volley.Request.Method.PUT;

/**
 * Created by Rohan Kamat on 16-03-2016.
 */
class RequestFileManager {
    public static final Gson GSON = getGson();
    private static final String TAG = RequestFileManager.class.getSimpleName();

    public static void createRequestFileForComplaint(Context context, Complaint complaint) {
        final EnqueuedRequest<Complaint> req = new EnqueuedRequest<>(PUT, Generator.getURLtoSendComplaint(), complaint, Complaint.class);
        String json = GSON.toJson(req);

        PrintStream p = null;
        try {
            p = new PrintStream(context.openFileOutput("complaint" + new Date().getTime() + ".json", Context.MODE_APPEND));
            p.println(json);
            p.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.close();
            }
        }
    }

    public static EnqueuedRequest<Complaint> getComplaintRequestFromFile(Context context, String fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.openFileInput(fileName)));
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            Log.i(TAG, "got data " + sb.toString());
            EnqueuedRequest<Complaint> enqueuedRequest = (EnqueuedRequest<Complaint>) GSON.fromJson(sb.toString(), EnqueuedRequest.class);
            Log.i(TAG, "in getComplaintRequestFromFile");
            return enqueuedRequest;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

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
        }).registerTypeAdapter(Class.class, new JsonSerializer<Class<?>>() {

            @Override
            public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.getName());
            }
        }).registerTypeAdapter(Class.class, new JsonDeserializer<Class<?>>() {

            @Override
            public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return Class.forName(json.getAsString());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).setPrettyPrinting().create();
    }
}
