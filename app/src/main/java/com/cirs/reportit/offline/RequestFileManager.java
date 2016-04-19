package com.cirs.reportit.offline;

import android.content.Context;
import android.util.Log;

import com.cirs.entities.Complaint;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest.FileType;
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
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    /**
     * Enqueues image for a complaint whose data is sent successfully.
     *
     * @param complaint  The complaint that was sent to the server
     * @param imgContent the byte[] obtained from the image to be sent.
     */
    public static void createSavedComplaintImageFile(Context context, Complaint complaint, byte[] imgContent, FileType fileType) {
        OutputStream os = null;
        try {
            os = context.openFileOutput(complaint.getId() + "." + fileType.toString().toLowerCase(), Context.MODE_PRIVATE);
            os.write(imgContent);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

            TypeToken<EnqueuedRequest<Complaint>> type = new TypeToken<EnqueuedRequest<Complaint>>() {
            };
            EnqueuedRequest<Complaint> enqueuedRequest = GSON.fromJson(sb.toString(), type.getType());

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


    public static EnqueuedImageRequest getImageRequestFromFile(Context context, String fileName, int fileSize) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(fileName);
            byte[] content = new byte[fileSize];
            fileInputStream.read(content);
            Complaint c = new Complaint();
            c.setId(Long.valueOf(fileName.split("\\.")[0]));
            String url = Generator.getUrltoUploadComplaintPic(c);
            FileType type = FileType.valueOf(fileName.split("\\.")[1].toUpperCase());
            return new EnqueuedImageRequest(url, "PUT", type, content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
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
        }).setDateFormat("dd MMM yyyy HH:mm:ss").setPrettyPrinting().create();
    }
}
