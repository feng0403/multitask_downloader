package com.rfchen.downloader;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by feng on 17/3/31.
 */

@DatabaseTable(tableName = "downloadentry")
public class DownloadEntry implements Serializable, Cloneable {
    @DatabaseField(id = true)
    public String id;
    @DatabaseField
    public String name;
    @DatabaseField
    public String url;
    @DatabaseField
    public DownloadStatus status = DownloadStatus.idle;
    @DatabaseField
    public int currentLength;
    @DatabaseField
    public int totalLength;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public HashMap<Integer, Integer> ranges;
    @DatabaseField
    public boolean ifSupportRange;


    public DownloadEntry(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public DownloadEntry() {
    }

    public void reset() {
        totalLength = 0;
        currentLength = 0;
        status = DownloadStatus.idle;
    }

    public enum DownloadStatus {
        idle, connecting, downloading, waiting, pauesd, resumed, cancelled, error, completed
    }

    @Override
    public String toString() {
        return "DownloadEntry{" +
                ",id=" + id +
                ", status=" + status +
                ", " + currentLength + "/" + totalLength +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadEntry that = (DownloadEntry) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
