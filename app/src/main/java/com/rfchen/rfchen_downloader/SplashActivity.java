package com.rfchen.rfchen_downloader;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.rfchen.downloader.DownloadManager;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by feng on 2017/4/24.
 */

@RuntimePermissions
public class SplashActivity extends AppCompatActivity {


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivityPermissionsDispatcher.getWriteExternalStoragePermissionWithCheck(SplashActivity.this);
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


    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void getWriteExternalStoragePermission() {
        jumpTo();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}