package com.dingmouren.dingdingmusic.api;

import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.bean.QQMusicBody;
import com.dingmouren.dingdingmusic.bean.QQMusicPage;
import com.dingmouren.dingdingmusic.bean.QQMusicResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dingmouren on 2017/2/3.
 */

public interface QQMusicApi {

    @GET("213-4")
    Observable<QQMusicResult<QQMusicBody<QQMusicPage<MusicBean>>>>
    getQQMusic(@Query("showapi_appid") String showapi_appid, @Query("showapi_sign")String showapi_sign,@Query("topid")   String topic);

}
