package com.rfchen.downloader;

/**
 * Created by feng on 17/3/31.
 */

public class Constants {
    public static final String KEY_DOWNLOAD_ENTRY = "key_download_entry";
    public static final String KEY_DOWNLOAD_ACTION = "key_download_action";

    public static final int MAX_DOWNLOAD_NUM = 3;

    public static final int KEY_DOWNLOAD_ACTION_ADD = 0;
    public static final int KEY_DOWNLOAD_ACTION_PAUSE = 1;
    public static final int KEY_DOWNLOAD_ACTION_RESUME = 2;
    public static final int KEY_DOWNLOAD_ACTION_CANCEL = 3;
    public static final int KEY_DOWNLOAD_ACTION_PAUSEALL = 4;
    public static final int KEY_DOWNLOAD_ACTION_RECOVERALL = 5;
    public static final int CONNCET_TIME_OUT = 8000;
    public static final int READ_TIME_OUT = 8000;
    public static final int MAX_MULTI_DOWNLOAD_THREAD = 5;
}
