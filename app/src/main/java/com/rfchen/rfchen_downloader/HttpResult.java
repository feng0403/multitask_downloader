package com.rfchen.rfchen_downloader;

/**
 * Created by feng on 2017/4/30.
 */

public class HttpResult<T> {
    private int ret;
    private String msg;
    private T data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
