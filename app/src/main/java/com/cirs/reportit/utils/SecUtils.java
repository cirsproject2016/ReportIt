package com.cirs.reportit.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Rohan Kamat on 13-03-2016.
 */
public class SecUtils {
    private static MessageDigest SHA;
    private static final String TAG = SecUtils.class.getSimpleName();

    static {
        try {
            SHA = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new AssertionError("SECUTILS: sha-512 must exist");
        }
    }

    public static String hash(String plainText) {
        byte[] hashBytes = SHA.digest(plainText.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(Integer.toHexString((b & 0xFF) + 0x100).substring(1));
        }
        Log.d(TAG, "Generated hash " + sb.toString());
        return sb.toString();
    }
}
