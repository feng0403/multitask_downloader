package com.rfchen.rfchen_downloader;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.rfchen.downloader.DataWatcher;
import com.rfchen.downloader.DownloadEntry;
import com.rfchen.downloader.DownloadManager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView mListView;
    private ListAdapter adapter;
    private List<DownloadEntry> mDataList;

    private DataWatcher mDataWatcher = new DataWatcher() {
        @Override
        public void notifyChange(DownloadEntry entry) {
            //update UI
            //需要重写hashCode和equals方法
            int index = mDataList.indexOf(entry);

            if (index != -1) {
                mDataList.remove(index);
                mDataList.add(index, entry);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = (ListView) findViewById(R.id.listView);
        mDataList = generateDownloadEntries();
        adapter = new ListAdapter(this, R.layout.activity_list_item, mDataList);


        //退出 activity 再进来 恢复数据
        //否则重新执行了一遍generateDownloadEntries，导致数据是乱的
        for (int i = 0; i < mDataList.size(); i++) {
            DownloadEntry latestDownloadEntry = DownloadManager.getInstance(this).queryLatestDownloadEntry(mDataList.get(i).id);
            if (latestDownloadEntry != null) {
                mDataList.remove(i);
                mDataList.add(i, latestDownloadEntry);
            }
        }



        mListView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DownloadManager.getInstance(this).addDataWatcher(mDataWatcher);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DownloadManager.getInstance(this).deleteDataWatcher(mDataWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ListActivity", "onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (item.getTitle().equals(getResources().getString(R.string.action_pause_all))) {
                    item.setTitle(R.string.action_resume_all);
                    DownloadManager.getInstance(this).pauseAll();
                } else {
                    item.setTitle(R.string.action_pause_all);
                    DownloadManager.getInstance(this).recoverAll();
                }
                return false;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private List<DownloadEntry> generateDownloadEntries() {
        List<DownloadEntry> dataList = new ArrayList<>();
        DownloadEntry downloadEntry1 = new DownloadEntry("1", "NO.1", "www,url.com");
        DownloadEntry downloadEntry2 = new DownloadEntry("2", "NO.2", "www,url.com");
        DownloadEntry downloadEntry3 = new DownloadEntry("3", "NO.3", "www,url.com");
        DownloadEntry downloadEntry4 = new DownloadEntry("4", "NO.4", "www,url.com");
        DownloadEntry downloadEntry5 = new DownloadEntry("5", "NO.5", "www,url.com");
        DownloadEntry downloadEntry11 = new DownloadEntry("11", "NO.11", "www,url.com");
        DownloadEntry downloadEntry22 = new DownloadEntry("22", "NO.22", "www,url.com");
        DownloadEntry downloadEntry33 = new DownloadEntry("33", "NO.33", "www,url.com");
        DownloadEntry downloadEntry44 = new DownloadEntry("44", "NO.44", "www,url.com");
        DownloadEntry downloadEntry55 = new DownloadEntry("55", "NO.55", "www,url.com");
        DownloadEntry downloadEntry111 = new DownloadEntry("111", "NO.111", "www,url.com");
        DownloadEntry downloadEntry222 = new DownloadEntry("222", "NO.222", "www,url.com");
        DownloadEntry downloadEntry333 = new DownloadEntry("333", "NO.333", "www,url.com");
        DownloadEntry downloadEntry444 = new DownloadEntry("444", "NO.444", "www,url.com");
        DownloadEntry downloadEntry555 = new DownloadEntry("555", "NO.555", "www,url.com");
        dataList.add(downloadEntry1);
        dataList.add(downloadEntry2);
        dataList.add(downloadEntry3);
        dataList.add(downloadEntry4);
        dataList.add(downloadEntry5);
        dataList.add(downloadEntry11);
        dataList.add(downloadEntry22);
        dataList.add(downloadEntry33);
        dataList.add(downloadEntry44);
        dataList.add(downloadEntry55);
        dataList.add(downloadEntry111);
        dataList.add(downloadEntry222);
        dataList.add(downloadEntry333);
        dataList.add(downloadEntry444);
        dataList.add(downloadEntry555);
        return dataList;
    }
}
