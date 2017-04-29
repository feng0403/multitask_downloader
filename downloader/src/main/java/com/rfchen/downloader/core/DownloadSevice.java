package com.rfchen.downloader.core;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rfchen.downloader.Utilties.Constants;
import com.rfchen.downloader.db.DBController;
import com.rfchen.downloader.entity.DownloadEntry;
import com.rfchen.downloader.notify.DataChanger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by feng on 17/3/31.
 */

public class DownloadSevice extends Service {
    public static final int MSG_NOTIFY_DOWNLOADING = 0;
    public static final int MSG_NOTIFY_PAUSED = 1;
    public static final int MSG_NOTIFY_CANCELLED = 2;
    public static final int MSG_NOTIFY_COMPLETED = 3;
    public static final int MSG_NOTIFY_CONNECTING = 4;
    public static final int MSG_NOTIFY_PROGRESS_CHANGE = 5;
    public static final int MSG_NOTIFY_ERROR = 6;
    private HashMap<String, DownloadTask> mDownloadingTasks = new HashMap<>();
    private Queue<DownloadEntry> mWaitingQueue = new LinkedList<>();

    private ExecutorService mExecutor;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            DownloadEntry entry = (DownloadEntry) msg.obj;
            switch (msg.what) {
                case MSG_NOTIFY_PAUSED:
                case MSG_NOTIFY_CANCELLED:
                    checkNext();
                    break;
                case MSG_NOTIFY_COMPLETED:
                    mDownloadingTasks.remove(entry.id);
                    checkNext();
                    break;
            }
            mDataChanger.postStatus(entry);
        }
    };
    private DBController mDBController;
    private DataChanger mDataChanger;

    /**
     * 等待队列中取 task 取下载
     */
    private void checkNext() {
        DownloadEntry entry = mWaitingQueue.poll();

        if (entry != null) {
            startDownload(entry);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutor = Executors.newCachedThreadPool();
        mDataChanger = DataChanger.getInstance(getApplicationContext());
        mDBController = DBController.getInstance(getApplicationContext());
        ArrayList<DownloadEntry> entryArrayList = mDBController.queryAll();
        if (entryArrayList != null) {
            Log.d("DownloadSevice", "entryArrayList:" + entryArrayList);
            for (DownloadEntry downloadEntry : entryArrayList) {
                if (downloadEntry.status == DownloadEntry.DownloadStatus.downloading
                        || downloadEntry.status == DownloadEntry.DownloadStatus.waiting) {
//                    downloadEntry.status = DownloadEntry.DownloadStatus.pauesd;
//                    TODO add a config if need to recover download
                    addDownload(downloadEntry);
                }

                mDataChanger.addToOperatedEntryMap(downloadEntry);
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DownloadEntry entry = (DownloadEntry) intent.getSerializableExtra(Constants.KEY_DOWNLOAD_ENTRY);
        int action = intent.getIntExtra(Constants.KEY_DOWNLOAD_ACTION, -1);
        switch (action) {
            case Constants.KEY_DOWNLOAD_ACTION_ADD:
                addDownload(entry);
                break;
            case Constants.KEY_DOWNLOAD_ACTION_PAUSE:
                pauseDownload(entry);
                break;
            case Constants.KEY_DOWNLOAD_ACTION_RESUME:
                resumeDownload(entry);
                break;
            case Constants.KEY_DOWNLOAD_ACTION_CANCEL:
                cancelDownload(entry);
                break;
            case Constants.KEY_DOWNLOAD_ACTION_PAUSEALL:
                pauseAllDownload();
                break;
            case Constants.KEY_DOWNLOAD_ACTION_RECOVERALL:
                recoverAllDownload();
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void cancelDownload(DownloadEntry entry) {
        DownloadTask task = mDownloadingTasks.remove(entry.id);
        if (task != null) {
            //任务正在下载
            task.cancel();
        } else {
            //任务处于等待状态
            mWaitingQueue.remove(entry);
            entry.status = DownloadEntry.DownloadStatus.cancelled;
            mDataChanger.postStatus(entry);
        }
    }

    private void resumeDownload(DownloadEntry entry) {
        addDownload(entry);
    }

    private void pauseDownload(DownloadEntry entry) {
        DownloadTask task = mDownloadingTasks.remove(entry.id);
        if (task != null) {
            task.pause();
        } else {
            //waiting 状态下点击 应该变成 pause
            mWaitingQueue.remove(entry);
            entry.status = DownloadEntry.DownloadStatus.pauesd;
            mDataChanger.postStatus(entry);
        }
    }


    private void pauseAllDownload() {
        //pause mWaitingQueue
        while (mWaitingQueue.iterator().hasNext()) {
            DownloadEntry entry = mWaitingQueue.poll();
            entry.status = DownloadEntry.DownloadStatus.pauesd;
            mDataChanger.postStatus(entry);
        }

        //pause mDownloadingTasks
        for (Map.Entry<String, DownloadTask> downloadTaskEntry : mDownloadingTasks.entrySet()) {
            downloadTaskEntry.getValue().pause();
        }
        mDownloadingTasks.clear();
    }

    private void recoverAllDownload() {
        LinkedList<DownloadEntry> recoverableEntity = mDataChanger.queryRecoverableEntity();
        if (recoverableEntity != null) {
            for (DownloadEntry downloadEntry : recoverableEntity) {
                addDownload(downloadEntry);
            }
        }
    }


    private void startDownload(DownloadEntry entry) {
        DownloadTask task = new DownloadTask(mHandler, entry, mExecutor);
        mDownloadingTasks.put(entry.id, task);
//        mExecutor.execute(task);
        task.start();
    }

    /**
     * 限制最大同时下载任务数
     *
     * @param entry
     */
    private void addDownload(DownloadEntry entry) {
        if (mDownloadingTasks.size() >= Constants.MAX_DOWNLOAD_NUM) {
            mWaitingQueue.offer(entry);
            entry.status = DownloadEntry.DownloadStatus.waiting;
            mDataChanger.postStatus(entry);
        } else {
            startDownload(entry);
        }
    }

}
