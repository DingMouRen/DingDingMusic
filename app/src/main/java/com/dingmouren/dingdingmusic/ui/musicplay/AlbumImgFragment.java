package com.dingmouren.dingdingmusic.ui.musicplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseFragment;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/2/2.
 */

public class AlbumImgFragment extends BaseFragment {
    private static final String ALBUM_URL = "cover_url";
    @BindView(R.id.img_album) ImageView mImgAlbum;
    private String mAlbumUrl;
    public static AlbumImgFragment newInstance(String url){
        AlbumImgFragment fragment = new AlbumImgFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_URL,url);
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
        }
    }

    @Override
    public void initData() {
        if (null != mAlbumUrl){
            Glide.with(this).load(mAlbumUrl).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.NONE)/*.placeholder(R.mipmap.native_1)*/.into(mImgAlbum);
        }
    }
}
