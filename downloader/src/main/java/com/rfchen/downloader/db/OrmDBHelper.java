package com.rfchen.downloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rfchen.downloader.entity.DownloadEntry;

import java.sql.SQLException;

/**
 * Created by feng on 2017/4/24.
 */

public class OrmDBHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "feng_downlaoder";
    private static final int DB_VERSION = 1;

    public OrmDBHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    public OrmDBHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DownloadEntry.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
