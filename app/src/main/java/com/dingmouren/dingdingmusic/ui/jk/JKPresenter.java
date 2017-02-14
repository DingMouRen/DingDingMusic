package com.dingmouren.dingdingmusic.ui.jk;

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
 * Created by dingmouren on 2017/2/7.
 * 从数据库取数据，就不用这个类了，后期考虑可能分页加载什么的，先不删除了
 */

public class JKPresenter implements JKConstract.Presenter {
    private static final String TAG = JKPresenter.class.getName();
    private JKConstract.View mView;
    public JKPresenter(JKConstract.View view) {
        this.mView = view;
    }

    @Override
    public void requestData() {
        ApiManager.getApiManager().getQQMusicApiService()
                .getQQMusic(Constant.QQ_MUSIC_APP_ID,Constant.QQ_MUSIC_SIGN,Constant.MUSIC_KOREA)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qqMusicBodyQQMusicResult -> parseData(Constant.MUSIC_KOREA,qqMusicBodyQQMusicResult.getShowapi_res_body().getPagebean().getSonglist()),this:: loadError);
    }

    private  void loadError(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void parseData(String topic,List<MusicBean> list){
        mView.setData(list);
        if (null != topic && null != list) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setType(Integer.parseInt(topic));
                    MyApplication.getDaoSession().getMusicBeanDao().insertOrReplace(list.get(i));
            }
        }
    }
}
