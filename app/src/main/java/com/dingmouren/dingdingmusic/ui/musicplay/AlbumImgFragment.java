package com.dingmouren.dingdingmusic.ui.musicplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.api.ApiManager;
import com.dingmouren.dingdingmusic.base.BaseFragment;
import com.jiongbull.jlog.JLog;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dingmouren on 2017/2/2.
 */

public class AlbumImgFragment extends BaseFragment {
    private static final String TAG = AlbumImgFragment.class.getName();
    private static final String ALBUM_URL = "cover_url";
    private static final String SONG_ID = "song_id";
    @BindView(R.id.img_album) ImageView mImgAlbum;
    @BindView(R.id.tv_lyric)
    TextView mTvLyric;
    private String mAlbumUrl;
    private int mSongId;
    public static AlbumImgFragment newInstance(String url,int songId){
        AlbumImgFragment fragment = new AlbumImgFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_URL,url);
        bundle.putInt(SONG_ID,songId);
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
            mAlbumUrl = getArguments().getString(ALBUM_URL);
            mSongId = getArguments().getInt(SONG_ID);
        }
    }

    @Override
    public void initData() {
        if (null != mAlbumUrl){
            Glide.with(MyApplication.mContext).load(mAlbumUrl).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.NONE)/*.placeholder(R.mipmap.native_1)*/.into(mImgAlbum);
        }
        ApiManager.getApiManager().getQQMusicApiService().searchLyric(Constant.QQ_MUSIC_APP_ID,Constant.QQ_MUSIC_SIGN,String.valueOf(mSongId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lyricBeanQQMusicResult -> {
                    parseLryric(lyricBeanQQMusicResult.getShowapi_res_body().getLyric());
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher().watch(this);
    }

    private void parseLryric(String lrc){
        try {
            InputStream inputStream = new ByteArrayInputStream(lrc.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
            String line = null;
            int count = 1;
            while ((line = bufferedReader.readLine()) != null){
                JLog.e(TAG,line);
            }
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
