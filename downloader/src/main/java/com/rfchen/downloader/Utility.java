package com.rfchen.downloader;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**
 * Created by feng on 2017/4/29.
 */

public class Utility {
    private static final String LOG_TAG = "Utility";

    public static File getDownloadStorageDir(String downloadName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                DIRECTORY_DOWNLOADS), downloadName);

        if (!file.mkdirs() && !file.exists()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }
}
