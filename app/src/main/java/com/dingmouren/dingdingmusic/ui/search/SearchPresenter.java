package com.dingmouren.dingdingmusic.ui.search;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.api.ApiManager;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.bean.SearchBean;
import com.jiongbull.jlog.JLog;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dingmouren on 2017/2/10.
 */

public class SearchPresenter implements SearchConstract.Presenter {
    private static final String TAG = SearchPresenter.class.getName();
    private SearchConstract.View mView;
    public SearchPresenter(SearchConstract.View view) {
        this.mView = view;
    }

    @Override
    public void requestData(String search) {
        JLog.e(TAG,"requestData--"+ search );
        if (search.equals("")){
            Toast.makeText(MyApplication.mContext,"你就不能写上歌曲名儿呀╭(╯^╰)╮",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ApiManager.getApiManager().getQQMusicApiService().searchQQMusic(Constant.QQ_MUSIC_APP_ID,Constant.QQ_MUSIC_SIGN,search,"1")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(qqMusicBodyQQMusicResult ->{
                        parseData(qqMusicBodyQQMusicResult.getShowapi_res_body().getPagebean().getContentlist());
                    },this::loadError );
        } catch (Exception e) {
            JLog.e(TAG,e.getMessage());
        }

    }

    private  void loadError(Throwable throwable) {
        throwable.printStackTrace();
        JLog.e(TAG,throwable.getMessage());
            Toast.makeText(MyApplication.mContext,"没有网络了哟，请检查网络设置",Toast.LENGTH_SHORT).show();
    }

    private void parseData(List<SearchBean> list){
        mView.setData(list);
    }
}
