package com.cirs.reportit.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cirs.R;
import com.google.gson.Gson;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Utility class to handle exception scenarios from the server
 * @author Rohan Kamat
 */
public final class ErrorUtils {
    private ErrorUtils(){}
	private static class ErrorResponse {
		int statusCode;
		String message;
	}
    /**
     * Shows a toast if devices are not connected to the internet
     * @param context The context where the request is being made
     */
    public static void showErrorIfNotConnected(Context context){
        if(!isConnected(context)){
			Toast.makeText(context.getApplicationContext(), R.string.err_not_connected, LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if connectivity is available
     * @param context the context where the request is being made
     * @return true, if device is connected to a network
     */
	public static boolean isConnected(Context context){
		ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info= manager.getActiveNetworkInfo();
		return info!=null && info.isConnected();
	}

    /**
     * Parses volley error to retrieve error messages.
     * Error responses for CIRS are generally of the form
     *  {
     *      "statusCode":....,
     *      "message":....
     *  }
     *  with images and Upvote being exceptions.
     *  Therefore, this shouldn't be used to parse errors while
     *  retrieving them.
     * @param error
     * @return
     * @throws com.google.gson.JsonSyntaxException if response is not of the form given above.
     */
	public static String parseVolleyError(VolleyError error){
		Gson gson=new Gson();
		return gson.fromJson(new String(error.networkResponse.data), ErrorResponse.class).message;
	}	
}
