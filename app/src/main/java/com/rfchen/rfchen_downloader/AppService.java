package com.rfchen.rfchen_downloader;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by feng on 2017/4/30.
 */

public interface AppService {

    //http://api.stay4it.com/v1/public/core/?service=downloader.applist


//    @GET("users/{user}/repos")
//    Call<List<AppEntry>> listRepos(@Path("user") String user);

//    @GET("/v1/public/core/?service=downloader.applist")
//    Call<List<AppEntry>> getAppList();

    @GET("/v1/public/core/?service=downloader.applist")
    Observable<HttpResult<List<AppEntry>>> getAppList();
}
