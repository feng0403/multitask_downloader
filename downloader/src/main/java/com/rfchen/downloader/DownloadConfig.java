package com.rfchen.downloader;

import com.rfchen.downloader.Utilties.FileUtilities;

import java.io.File;

/**
 * Created by feng on 2017/4/29.
 */

public class DownloadConfig {
    private static DownloadConfig mConfig;

    private int max_download_tasks = 3;
    private int max_download_threads = 3;
    private File downloadDir = null;
    private int min_operate_interval = 1000 * 1;
    private boolean recoverDownloadWhenStart = false;
    private String downloadDirName = "downlaoder_feng0403";


    private DownloadConfig() {
    }

    public static DownloadConfig getInstance() {
        if (null == mConfig) {
            mConfig = new DownloadConfig();
        }
        return mConfig;
    }

    public String getDownloadDirName() {
        return downloadDirName;
    }

    public void setDownloadDirName(String downloadDirName) {
        this.downloadDirName = downloadDirName;
    }

    public int getMax_download_tasks() {
        return max_download_tasks;
    }

    public void setMax_download_tasks(int max_download_tasks) {
        this.max_download_tasks = max_download_tasks;
    }

    public int getMax_download_threads() {
        return max_download_threads;
    }

    public void setMax_download_threads(int max_download_threads) {
        this.max_download_threads = max_download_threads;
    }

    public File getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(File downloadDir) {
        this.downloadDir = downloadDir;
    }

    public int getMin_operate_interval() {
        return min_operate_interval;
    }

    public void setMin_operate_interval(int min_operate_interval) {
        this.min_operate_interval = min_operate_interval;
    }

    public boolean isRecoverDownloadWhenStart() {
        return recoverDownloadWhenStart;
    }

    public void setRecoverDownloadWhenStart(boolean recoverDownloadWhenStart) {
        this.recoverDownloadWhenStart = recoverDownloadWhenStart;
    }

    public File getDownloadFile(String url) {
        return new File(FileUtilities.getDownloadStorageDir(getDownloadDirName()), FileUtilities.getMd5FileName(url));
    }
}
