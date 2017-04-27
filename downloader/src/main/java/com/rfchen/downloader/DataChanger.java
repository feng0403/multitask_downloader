package com.rfchen.downloader;

import android.content.Context;

import com.rfchen.downloader.db.DBController;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by feng on 17/3/31.
 */

public class DataChanger extends Observable {

    private volatile static DataChanger INSTANCE;
    private final Context mContext;

    private LinkedHashMap<String, DownloadEntry> mOperatedEntries;

    private DataChanger(Context context) {
        mOperatedEntries = new LinkedHashMap<>();
        mContext = context;
    }

    public static DataChanger getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DataChanger.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataChanger(context);
                }
            }
        }
        return INSTANCE;
    }

    public void postStatus(DownloadEntry entry) {
        mOperatedEntries.put(entry.id, entry);
        DBController.getInstance(mContext).newOrUpdate(entry);
        setChanged();
        notifyObservers(entry);
    }

    public LinkedList<DownloadEntry> queryRecoverableEntity() {
        LinkedList<DownloadEntry> mRecovereEntries = null;
        for (Map.Entry<String, DownloadEntry> downloadEntry : mOperatedEntries.entrySet()) {
            if (downloadEntry.getValue().status == DownloadEntry.DownloadStatus.pauesd) {
                if (mRecovereEntries == null) {
                    mRecovereEntries = new LinkedList<>();
                }
                mRecovereEntries.add(downloadEntry.getValue());
            }
        }
        return mRecovereEntries;
    }

    public DownloadEntry queryLatestDownloadEntry(String id) {
        return mOperatedEntries.get(id);
    }

    public void addToOperatedEntryMap(DownloadEntry downloadEntry) {
        mOperatedEntries.put(downloadEntry.id, downloadEntry);
    }
}
