package com.dingmouren.dingdingmusic.ui.volkslied;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.api.ApiManager;
import com.dingmouren.dingdingmusic.bean.MusicBean;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dingmouren on 2017/2/15.
 */

public class VolksliedPresenter implements VolksliedConstract.Presenter {
    private static final String TAG = VolksliedPresenter.class.getName();
    private VolksliedConstract.View mView;
    public VolksliedPresenter(VolksliedConstract.View view) {
        this.mView = view;
    }

    @Override
    public void requestData() {
        ApiManager.getApiManager().getQQMusicApiService()
                .getQQMusic(Constant.QQ_MUSIC_APP_ID, Constant.QQ_MUSIC_SIGN,Constant.MUSIC_VOLKSLIED)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qqMusicBodyQQMusicResult -> parseData(Constant.MUSIC_VOLKSLIED,qqMusicBodyQQMusicResult.getShowapi_res_body().getPagebean().getSonglist()),this:: loadError);
    }

    private  void loadError(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void parseData(String topic,List<MusicBean> list){
        mView.setData(list);
        if (null != topic && null != list) {
            Observable.from(list).observeOn(Schedulers.io()).subscribe(bean -> {
                bean.setType(Integer.parseInt(topic));
                MyApplication.getDaoSession().getMusicBeanDao().insertOrReplace(bean);
            });
        }
    }
}
