package com.rfchen.rfchen_downloader;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.rfchen.downloader.entity.DownloadEntry;
import com.rfchen.downloader.DownloadManager;

import java.util.List;

/**
 * Created by feng on 17/3/31.
 */

public class ListAdapter extends ArrayAdapter<DownloadEntry> {


    private final int resource;
    private final List<DownloadEntry> dataList;
    private final DownloadManager mDownloadManager;

    public ListAdapter(@NonNull Context context, @LayoutRes int resource,
                       @NonNull List<DownloadEntry> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.dataList = objects;
        mDownloadManager = DownloadManager.getInstance(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final DownloadEntry downloadEntry = dataList.get(position);
        View view;
        ViewHolder viewHolder = null;
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.mDownloadLabel);
            viewHolder.button = (Button) view.findViewById(R.id.mDownloadBtn);
            view.setTag(viewHolder);
        }

        viewHolder.textView.setText(downloadEntry.toString());
        viewHolder.button.setText(downloadEntry.status.name());
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadEntry.status == DownloadEntry.DownloadStatus.idle) {
                    mDownloadManager.add(downloadEntry);
                } else if (downloadEntry.status == DownloadEntry.DownloadStatus.downloading || downloadEntry.status == DownloadEntry.DownloadStatus.waiting) {
                    mDownloadManager.pause(downloadEntry);
                } else if (downloadEntry.status == DownloadEntry.DownloadStatus.pauesd) {
                    mDownloadManager.resume(downloadEntry);
                }
            }
        });
        return view;
    }

    private class ViewHolder {
        private TextView textView;
        private Button button;
    }
}
