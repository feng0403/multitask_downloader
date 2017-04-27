package com.rfchen.downloader;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by feng on 2017/4/24.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
