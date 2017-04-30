package com.rfchen.rfchen_downloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.rfchen.downloader.DownloadManager;
import com.rfchen.downloader.entity.DownloadEntry;
import com.rfchen.downloader.notify.DataWatcher;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ListActivity extends AppCompatActivity {

    private ListView mListView;
    private ListAdapter adapter;
    private List<DownloadEntry> mDataList;
    private List<AppEntry> mAppEntryList;

    private DataWatcher mDataWatcher = new DataWatcher() {
        @Override
        public void notifyChange(DownloadEntry entry) {
            //update UI
            //需要重写hashCode和equals方法
            int index = mDataList.indexOf(entry);
            Log.d("ListActivity", "entry:" + entry);

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
        mDataList = new ArrayList<>();
        adapter = new ListAdapter(this, R.layout.activity_list_item, mDataList);


        /*
        //退出 activity 再进来 恢复数据
        //否则重新执行了一遍generateDownloadEntries，导致数据是乱的
        for (int i = 0; i < mDataList.size(); i++) {
            DownloadEntry latestDownloadEntry = DownloadManager.getInstance(this).queryLatestDownloadEntry(mDataList.get(i).id);
            if (latestDownloadEntry != null) {
                mDataList.remove(i);
                mDataList.add(i, latestDownloadEntry);
            }
        }
        mListView.setAdapter(adapter);*/


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.stay4it.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        AppService service = retrofit.create(AppService.class);

        service.getAppList()
                .map(new Func1<HttpResult<List<AppEntry>>, List<AppEntry>>() {
                    @Override
                    public List<AppEntry> call(HttpResult<List<AppEntry>> httpResult) {
                        if (httpResult.getRet() != 200) {

                        }
                        return httpResult.getData();
                    }
                }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<AppEntry>>() {
                    @Override
                    public void call(List<AppEntry> appEntries) {
                        Log.d("ListActivity", appEntries.toString());
                        mAppEntryList = appEntries;
                        for (AppEntry appEntry : mAppEntryList) {
                            mDataList.add(appEntry.generateDownloadEntry());
                        }
                        //退出 activity 再进来 恢复数据
                        //否则重新执行了一遍generateDownloadEntries，导致数据是乱的
                        for (int i = 0; i < mDataList.size(); i++) {
                            DownloadEntry latestDownloadEntry = DownloadManager.getInstance(ListActivity.this).queryLatestDownloadEntry(mDataList.get(i).id);
                            if (latestDownloadEntry != null) {
                                mDataList.remove(i);
                                mDataList.add(i, latestDownloadEntry);
                            }
                        }
                        adapter = new ListAdapter(ListActivity.this, R.layout.activity_list_item, mDataList);
                        mListView.setAdapter(adapter);
                    }
                });
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
}
