package com.rfchen.downloader.notify;

import com.rfchen.downloader.entity.DownloadEntry;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by feng on 17/3/31.
 */

public abstract class DataWatcher implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof DownloadEntry) {
            notifyChange(((DownloadEntry) arg));
        }

    }

    public abstract void notifyChange(DownloadEntry entry);
}
