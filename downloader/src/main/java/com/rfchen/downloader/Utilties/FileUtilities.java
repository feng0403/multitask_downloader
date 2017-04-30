package com.rfchen.downloader.Utilties;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**
 * Created by feng on 2017/4/29.
 */

public class FileUtilities {
    private static final String LOG_TAG = "FileUtilities";

    public static File getDownloadStorageDir(String downloadDirName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                DIRECTORY_DOWNLOADS), downloadDirName);

        if (!file.mkdirs() && !file.exists()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    public static String getMd5FileName(String url) {
        byte[] md5Bytes = getMD5(url.getBytes());
        BigInteger bigInteger = new BigInteger(1, md5Bytes);
        return bigInteger.toString(16) + url.substring(url.lastIndexOf("/") + 1);
    }


    private static byte[] getMD5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(bytes);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
