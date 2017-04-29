package com.rfchen.downloader.core;


import android.util.Log;

import com.rfchen.downloader.Utilties.Constants;
import com.rfchen.downloader.Utilties.FileUtility;
import com.rfchen.downloader.entity.DownloadEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by feng on 17/3/31.
 */

public class DownloadThread implements Runnable {
    private final String urlStr;
    private final int startPos;
    private final int endPos;
    private final DownloadListener downloadListener;
    private final int index;
    private final String fileName;
    private DownloadEntry.DownloadStatus mStatus;


    public DownloadThread(int index, String url, int startPos, int endPos, DownloadListener downloadListener) {
        this.index = index;
        this.urlStr = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.downloadListener = downloadListener;
        this.fileName = url.substring(url.lastIndexOf("/") + 1);
    }

    @Override
    public void run() {
        mStatus = DownloadEntry.DownloadStatus.downloading;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        RandomAccessFile raf = null;
        FileOutputStream fos = null;
        try {


            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(Constants.CONNCET_TIME_OUT);
            urlConnection.setReadTimeout(Constants.READ_TIME_OUT);
            if (startPos != -1 && endPos != -1) {
                urlConnection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            }
            File file = new File(FileUtility.getDownloadStorageDir("Downloader_feng0403"), fileName);

            int responseCode = urlConnection.getResponseCode();
            Log.d("DownloadThread", "responseCode: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                inputStream = urlConnection.getInputStream();

                raf = new RandomAccessFile(file, "rw");
                raf.seek(startPos);
                byte[] buffer = new byte[2048];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    if (mStatus == DownloadEntry.DownloadStatus.pauesd
                            || mStatus == DownloadEntry.DownloadStatus.cancelled
                            || mStatus == DownloadEntry.DownloadStatus.error) {
                        break;
                    }
                    raf.write(buffer, 0, len);
                    downloadListener.onProgressChanged(index, len);
                }
                if (raf != null) {
                    raf.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } else if (responseCode == HttpURLConnection.HTTP_OK) {
                fos = new FileOutputStream(file);
                inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[2048];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    if (mStatus == DownloadEntry.DownloadStatus.pauesd
                            || mStatus == DownloadEntry.DownloadStatus.cancelled
                            || mStatus == DownloadEntry.DownloadStatus.error) {
                        break;
                    }
                    fos.write(buffer, 0, len);
                    downloadListener.onProgressChanged(index, len);
                }
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } else {
                downloadListener.onDownloadError(index, responseCode + ":" + urlConnection.getResponseMessage());
            }
            if (mStatus == DownloadEntry.DownloadStatus.pauesd) {
                downloadListener.onDownloadPaused(index);
            } else if (mStatus == DownloadEntry.DownloadStatus.cancelled) {
                downloadListener.onDownloadCancel(index);
            } else if (mStatus == DownloadEntry.DownloadStatus.error) {
                downloadListener.onDownloadError(index, "cancel manually by error");
            } else {
                mStatus = DownloadEntry.DownloadStatus.completed;
                downloadListener.onDownloadCompleted(index);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            downloadListener.onDownloadError(index, e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public void pause() {
        mStatus = DownloadEntry.DownloadStatus.pauesd;
    }

    public boolean isPaused() {
        return mStatus == DownloadEntry.DownloadStatus.pauesd || mStatus == DownloadEntry.DownloadStatus.completed;
    }

    public boolean isRunning() {
        return mStatus == DownloadEntry.DownloadStatus.downloading;
    }

    public boolean isCompleted() {
        return mStatus == DownloadEntry.DownloadStatus.completed;
    }

    public void cancel() {
        mStatus = DownloadEntry.DownloadStatus.cancelled;
    }

    public void cancelByError() {
        mStatus = DownloadEntry.DownloadStatus.error;
    }

    public boolean isError() {
        return mStatus == DownloadEntry.DownloadStatus.error;
    }


    interface DownloadListener {
        void onProgressChanged(int index, int progress);

        void onDownloadError(int index, String message);

        void onDownloadPaused(int index);

        void onDownloadCancel(int index);

        void onDownloadCompleted(int index);
    }
}
