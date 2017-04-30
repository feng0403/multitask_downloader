package com.rfchen.downloader;

import android.util.Log;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by feng on 2017/4/29.
 */
public class DownloadConfigTest {
    @Test
    public void getDownloadFile() throws Exception {
        String url = "http://shouji.360tpcdn.com/170428/92c0ba2f0aeba67693c0239d8cc18df0/com.snda.wifilocating_3115.apk";
        File downloadFile = DownloadConfig.getInstance().getDownloadFile(url);
        FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
        byte[] bytes = new byte[10];
        fileOutputStream.write(bytes);
        Log.d("DownloadConfigTest", "downloadFile.exists():" + downloadFile.exists());
    }

}