package com.rfchen.rfchen_downloader;

import com.rfchen.downloader.entity.DownloadEntry;

/**
 * Created by feng on 2017/4/30.
 */

public class AppEntry {

    /**
     * url : http://shouji.360tpcdn.com/150807/42ac3ad85a189125701e69ccff36ad7a/com.eg.android.AlipayGphone_78.apk
     * name : 支付宝
     * icon : http://p17.qhimg.com/t0144812feb68f1b7bc.png
     * desc : 吃美食用支付宝，最高减100元！
     * size : 35.59M
     */

    private String url;
    private String name;
    private String icon;
    private String desc;
    private String size;


    public DownloadEntry generateDownloadEntry() {
        DownloadEntry downloadEntry = new DownloadEntry();
        downloadEntry.id = url;
        downloadEntry.url = url;
        downloadEntry.name = name;
        return downloadEntry;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "AppEntry{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", desc='" + desc + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
