package com.rfchen.rfchen_downloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rfchen.downloader.DownloadConfig;
import com.rfchen.downloader.notify.DataWatcher;
import com.rfchen.downloader.entity.DownloadEntry;
import com.rfchen.downloader.DownloadManager;

public class MainActivity extends AppCompatActivity {

    DataWatcher mDataWather = new DataWatcher() {
        @Override
        public void notifyChange(DownloadEntry entry) {
            if (entry.status == DownloadEntry.DownloadStatus.cancelled) {
                downloadEntry = null;
            } else {
                downloadEntry = entry;
            }
            Log.d("MainActivity", "notifyChange: " + entry.toString());
        }
    };

    DownloadEntry downloadEntry;
    DownloadManager mDownloadManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadManager = DownloadManager.getInstance(this);
        DownloadConfig.getInstance().setMax_download_threads(3);

        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == downloadEntry) {
//                    downloadEntry = new DownloadEntry("1", "no.1", "http://wdj-qn-apk.wdjcdn.com/c/32/97f952b79b96a8c791ecbf06e05e732c.apk");
//                    downloadEntry = new DownloadEntry("1", "no.1", "http://www.wandoujia.com/apps/com.UCMobile/download");
//                    downloadEntry = new DownloadEntry("1", "no.1", "http://images.cnitblog.com/blog/288799/201409/061046391107893.jpg");
//                    downloadEntry = new DownloadEntry("1", "no.1", "http://shouji.360tpcdn.com/150723/de6fd89a346e304f66535b6d97907563/com.sina.weibo_2057.apk");
                    downloadEntry = new DownloadEntry("1", "no.1", "http://shouji.360tpcdn.com/170428/92c0ba2f0aeba67693c0239d8cc18df0/com.snda.wifilocating_3115.apk");
                }
                mDownloadManager.add(downloadEntry);
            }
        });

        findViewById(R.id.button_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadEntry.status == DownloadEntry.DownloadStatus.downloading) {
                    mDownloadManager.pause(downloadEntry);
                } else if (downloadEntry.status == DownloadEntry.DownloadStatus.pauesd) {
                    mDownloadManager.resume(downloadEntry);
                }
            }
        });
        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownloadManager.cancel(downloadEntry);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mDownloadManager.addDataWatcher(mDataWather);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDownloadManager.deleteDataWatcher(mDataWather);
    }
}
