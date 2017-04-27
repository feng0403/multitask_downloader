package com.rfchen.rfchen_downloader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.rfchen.downloader.DownloadManager;

/**
 * Created by feng on 2017/4/24.
 */

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            jumpTo();
        }
    };

    private void jumpTo() {
        startActivity(new Intent(SplashActivity.this, ListActivity.class));
        finish();
        DownloadManager.getInstance(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.sendEmptyMessageDelayed(0, 500);
    }
}
