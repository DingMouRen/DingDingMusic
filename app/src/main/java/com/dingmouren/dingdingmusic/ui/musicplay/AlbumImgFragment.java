package com.dingmouren.dingdingmusic.ui.musicplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.api.ApiManager;
import com.dingmouren.dingdingmusic.base.BaseFragment;
import com.dingmouren.dingdingmusic.bean.LyricBean;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.bean.QQMusicResult;
import com.jiongbull.jlog.JLog;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dingmouren on 2017/2/2.
 */

public class AlbumImgFragment extends BaseFragment {
    private static final String TAG = AlbumImgFragment.class.getName();
    private static final String BEAN = "bean";
    @BindView(R.id.img_album) ImageView mImgAlbum;
    @BindView(R.id.tv_lyric)TextView mTvLyric;
    @BindView(R.id.tv_song_name) TextView mSongName;
    @BindView(R.id.tv_singer) TextView mSingerName;
    private MusicBean mBean;

    public static AlbumImgFragment newInstance(MusicBean bean){
        AlbumImgFragment fragment = new AlbumImgFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BEAN,bean);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public int setLayoutResourceID() {
        return R.layout.fragment_cover;
    }

    @Override
    public void initView() {
        if (null != getArguments()){//非空判断
            mBean = (MusicBean) getArguments().getSerializable(BEAN);
            mSongName.setText(mBean.getSongname());
            mSingerName.setText(mBean.getSingername());
        }
    }


    @Override
    public void initData() {
        if (null != mBean){
            Glide.with(MyApplication.mContext).load(mBean.getAlbumpic_big()).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(mImgAlbum);
           /* ApiManager.getApiManager().getQQMusicApiService().searchLyric(Constant.QQ_MUSIC_APP_ID,Constant.QQ_MUSIC_SIGN,String.valueOf(mBean.getSongid()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<QQMusicResult<LyricBean>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(QQMusicResult<LyricBean> lyricBeanQQMusicResult) {

                            parseLrc(lyricBeanQQMusicResult.getShowapi_res_body().getLyric_txt());
                        }
                    });*/
        }
    }

    private void parseLrc(String lrc){
        StringBuilder builder = new StringBuilder();
        if (lrc.trim().substring(0,1).matches("[a-zA-Z]+")){
            lrc = "";
        }
        String[] lrcArr = lrc.trim().split("   ");
        for (String string : lrcArr) {
            if(!string.equals("")){
                builder.append(string.trim() + "\n");
            }
        }
        JLog.e(TAG,builder.toString());
        mTvLyric.setText(builder.toString().equals("") ? "此歌曲为没有填词的音乐，请您欣赏": builder.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        MyApplication.getRefWatcher().watch(this);
    }
}
