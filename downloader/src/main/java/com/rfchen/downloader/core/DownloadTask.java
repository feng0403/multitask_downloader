package com.rfchen.downloader.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rfchen.downloader.DownloadConfig;
import com.rfchen.downloader.entity.DownloadEntry;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * Created by feng on 17/3/31.
 */

public class DownloadTask implements ConnectThread.ConnectListener, DownloadThread.DownloadListener {
    private final DownloadEntry mEntry;
    private final Handler mHandler;
    private final ExecutorService mExecutor;
    private final int max_download_threads;
    private Message message;
    private ConnectThread connectThread;
    private DownloadThread[] mDownloadThreads;
    private long lastTimeStamp;
    private DownloadEntry.DownloadStatus[] threadStatus;


    public DownloadTask(Handler handler, DownloadEntry entry, ExecutorService executor) {
        this.mEntry = entry;
        this.mHandler = handler;
        this.mExecutor = executor;
        max_download_threads = DownloadConfig.getInstance().getMax_download_threads();
    }

    public void start() {
        if (mEntry.totalLength == 0) {
            connectThread = new ConnectThread(mEntry.url, this);
            mExecutor.execute(connectThread);
            mEntry.status = DownloadEntry.DownloadStatus.connecting;
            notifyUpdate(DownloadSevice.MSG_NOTIFY_CONNECTING, mEntry);
        } else {
            startDownload();
        }
    }

    private void notifyUpdate(int what, DownloadEntry downloadEntry) {
        message = mHandler.obtainMessage();
        message.obj = downloadEntry;
        message.what = what;
        mHandler.sendMessage(message);
    }

    public void pause() {
        if (mDownloadThreads != null && mDownloadThreads.length > 0) {
            for (int i = 0; i < mDownloadThreads.length; i++) {
                if (mDownloadThreads[i] != null && threadStatus[i] == DownloadEntry.DownloadStatus.downloading) {
                    mDownloadThreads[i].pause();
                }
            }
        }
    }

    public void cancel() {
        if (mDownloadThreads != null && mDownloadThreads.length > 0) {
            for (int i = 0; i < mDownloadThreads.length; i++) {
                if (mDownloadThreads[i] != null && threadStatus[i] == DownloadEntry.DownloadStatus.downloading) {
                    mDownloadThreads[i].cancel();
                }
            }
        }
    }

    @Override
    public void onConnect(boolean ifSupportRange, int totalLen) {
        Log.d("DownloadTask", "onConnect : ifSupportRange:" + ifSupportRange + " " + "totalLen:" + totalLen);
        mEntry.ifSupportRange = ifSupportRange;
        mEntry.totalLength = totalLen;

        startDownload();
    }

    private void startDownload() {
        if (mEntry.ifSupportRange) {
            startMultiThreadDownload();
        } else {
            startSingleThreadDownload();
        }
    }

    private void startSingleThreadDownload() {
        mEntry.status = DownloadEntry.DownloadStatus.downloading;
        notifyUpdate(DownloadSevice.MSG_NOTIFY_DOWNLOADING, mEntry);


        mDownloadThreads = new DownloadThread[1];
        mDownloadThreads[0] = new DownloadThread(-1, mEntry.url, -1, -1, this);
        mExecutor.execute(mDownloadThreads[0]);

    }

    private void startMultiThreadDownload() {

        mEntry.status = DownloadEntry.DownloadStatus.downloading;
        notifyUpdate(DownloadSevice.MSG_NOTIFY_DOWNLOADING, mEntry);

        int startPos;
        int endPos;
        int blockLen = mEntry.totalLength / max_download_threads;
        if (mEntry.ranges == null) {
            mEntry.ranges = new HashMap<>();
            for (int i = 0; i < max_download_threads; i++) {
                mEntry.ranges.put(i, 0);
            }
        }

        if (threadStatus == null) {
            threadStatus = new DownloadEntry.DownloadStatus[max_download_threads];
        }

        mDownloadThreads = new DownloadThread[max_download_threads];
        for (int i = 0; i < max_download_threads; i++) {
            startPos = i * blockLen + mEntry.ranges.get(i);
            if (i < max_download_threads - 1) {
                endPos = (i + 1) * blockLen - 1;
            } else {
                endPos = mEntry.totalLength;
            }
            Log.d("DownloadTask", "index: " + i + " startPos:" + startPos + " endPos:" + endPos);
            if (startPos < endPos) {
                mDownloadThreads[i] = new DownloadThread(i, mEntry.url, startPos, endPos, this);
                mExecutor.execute(mDownloadThreads[i]);
                threadStatus[i] = DownloadEntry.DownloadStatus.downloading;
            } else {
                threadStatus[i] = DownloadEntry.DownloadStatus.completed;
            }
        }
    }

    @Override
    public synchronized void onProgressChanged(int index, int progress) {
        if (mEntry.ifSupportRange) {
            int newRange = mEntry.ranges.get(index) + progress;
            mEntry.ranges.put(index, newRange);
        }
        mEntry.currentLength += progress;

        long currentTimeStamp = System.currentTimeMillis();

        //如果不是完成的状态每秒发一次更新状态
        if (mEntry.currentLength == mEntry.totalLength) {
            mEntry.status = DownloadEntry.DownloadStatus.completed;
            notifyUpdate(DownloadSevice.MSG_NOTIFY_COMPLETED, mEntry);
        } else if (currentTimeStamp - lastTimeStamp > 1000) {
            lastTimeStamp = currentTimeStamp;
            mEntry.status = DownloadEntry.DownloadStatus.downloading;
            notifyUpdate(DownloadSevice.MSG_NOTIFY_PROGRESS_CHANGE, mEntry);
        }
    }

    @Override
    public synchronized void onDownloadError(int index, String message) {
        Log.d("DownloadTask", "thread " + index + " error: " + message);
        if (mEntry.ifSupportRange) {
            threadStatus[index] = DownloadEntry.DownloadStatus.error;
            for (int i = 0; i < threadStatus.length; i++) {
                if (threadStatus[i] != DownloadEntry.DownloadStatus.error && threadStatus[i] != DownloadEntry.DownloadStatus.completed) {
                    mDownloadThreads[i].cancelByError();
                    return;
                }
            }
        }

        mEntry.status = DownloadEntry.DownloadStatus.error;
        notifyUpdate(DownloadSevice.MSG_NOTIFY_ERROR, mEntry);
    }


    @Override
    public synchronized void onConnectError(String message) {
        mEntry.status = DownloadEntry.DownloadStatus.error;
        notifyUpdate(DownloadSevice.MSG_NOTIFY_ERROR, mEntry);
    }


    @Override
    public synchronized void onDownloadPaused(int index) {
        Log.d("DownloadTask", "thread " + index + " paused");
        if (mEntry.ifSupportRange) {
            threadStatus[index] = DownloadEntry.DownloadStatus.pauesd;
            for (int i = 0; i < threadStatus.length; i++) {
                if (threadStatus[i] != DownloadEntry.DownloadStatus.pauesd && threadStatus[i] != DownloadEntry.DownloadStatus.completed) {
                    return;
                }
            }
        }

        mEntry.status = DownloadEntry.DownloadStatus.pauesd;
        notifyUpdate(DownloadSevice.MSG_NOTIFY_PAUSED, mEntry);
    }

    @Override
    public synchronized void onDownloadCancel(int index) {
        Log.d("DownloadTask", "thread " + index + " paused");
        if (mEntry.ifSupportRange) {
            threadStatus[index] = DownloadEntry.DownloadStatus.cancelled;
            for (int i = 0; i < threadStatus.length; i++) {
                if (threadStatus[i] != DownloadEntry.DownloadStatus.cancelled && threadStatus[i] != DownloadEntry.DownloadStatus.completed) {
                    return;
                }
            }
        }

        //delete file;
        mEntry.reset();
        mEntry.status = DownloadEntry.DownloadStatus.cancelled;
        notifyUpdate(DownloadSevice.MSG_NOTIFY_CANCELLED, mEntry);
    }

    @Override
    public synchronized void onDownloadCompleted(int index) {
        Log.d("DownloadTask", "thread " + index + " complete");
        if (mEntry.ifSupportRange) {
            threadStatus[index] = DownloadEntry.DownloadStatus.completed;
        }
    }
}
