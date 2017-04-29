package com.rfchen.downloader;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by feng on 2017/4/26.
 */

public class ConnectThread implements Runnable {
    private final String urlStr;
    private final ConnectListener connectListener;

    public ConnectThread(String url, ConnectListener connectListener) {
        this.urlStr = url;
        this.connectListener = connectListener;
    }


    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(Constants.CONNCET_TIME_OUT);
            urlConnection.setReadTimeout(Constants.READ_TIME_OUT);
            urlConnection.setInstanceFollowRedirects(false);
            int contentLength = urlConnection.getContentLength();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (urlConnection.getHeaderField("Accept-Ranges").equals("bytes")) {
                    connectListener.onConnect(true, contentLength);
                } else {
                    connectListener.onConnect(false, contentLength);
                }
            } else {
                connectListener.onConnectError("network error " + responseCode);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            connectListener.onConnectError(e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    public void cancel() {

    }


    interface ConnectListener {
        void onConnect(boolean ifSupportRange, int totalLen);

        void onConnectError(String message);
    }
}
