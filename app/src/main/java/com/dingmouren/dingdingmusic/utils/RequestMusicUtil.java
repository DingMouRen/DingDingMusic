package com.dingmouren.dingdingmusic.utils;

import android.app.Activity;
import android.content.Context;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.api.ApiManager;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.greendao.MusicBeanDao;
import com.jiongbull.jlog.JLog;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dingmouren on 2017/2/4.
 */

public class RequestMusicUtil {
    private static final String TAG = RequestMusicUtil.class.getName();
    public  void requestMusic(String topic) {
        ApiManager.getApiManager().getQQMusicApiService()
                .getQQMusic(Constant.QQ_MUSIC_APP_ID, Constant.QQ_MUSIC_SIGN, topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qqMusicBodyQQMusicResult -> parseData(topic,  qqMusicBodyQQMusicResult.getShowapi_res_body().getPagebean().getSonglist()),this::loadError);
    }

    /**
     * 将数据保存到数据库
     * @param topic
     * @param list
     */
    private static void parseData(String topic, List<MusicBean> list) {
        if (null != topic && null != list) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setType(Integer.parseInt(topic));
//                if (0 == MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.Songid.eq(list.get(i).getSongid())).count()) {
                    MyApplication.getDaoSession().getMusicBeanDao().insertOrReplace(list.get(i));
//                }
            }
        }
    }

    private  void loadError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
