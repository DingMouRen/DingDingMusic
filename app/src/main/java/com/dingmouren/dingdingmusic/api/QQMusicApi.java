package com.dingmouren.dingdingmusic.api;

import com.dingmouren.dingdingmusic.bean.LyricBean;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.bean.QQMusicBody;
import com.dingmouren.dingdingmusic.bean.QQMusicPage;
import com.dingmouren.dingdingmusic.bean.QQMusicResult;
import com.dingmouren.dingdingmusic.bean.SearchBean;
import com.dingmouren.dingdingmusic.bean.SearchPage;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dingmouren on 2017/2/3.
 */

public interface QQMusicApi {

    //请求歌曲
    @GET("213-4")
    Observable<QQMusicResult<QQMusicBody<QQMusicPage<List<MusicBean>>>>>
    getQQMusic(@Query("showapi_appid") String showapi_appid, @Query("showapi_sign")String showapi_sign,@Query("topid")   String topic);

    //搜索歌曲
    @GET("213-1")
    Observable<QQMusicResult<QQMusicBody<SearchPage<List<SearchBean>>>>>
    searchQQMusic(@Query("showapi_appid") String showapi_appid, @Query("showapi_sign")String showapi_sign,@Query("keyword")   String keyword,@Query("page") String page);

    //歌词
    @GET("213-2")
    Observable<QQMusicResult<LyricBean>>
    searchLyric(@Query("showapi_appid") String showapi_appid, @Query("showapi_sign")String showapi_sign,@Query("musicid")   String musicid);

}
