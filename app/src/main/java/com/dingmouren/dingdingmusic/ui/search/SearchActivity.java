package com.dingmouren.dingdingmusic.ui.search;

import android.app.ActivityOptions;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.bean.SearchBean;
import com.dingmouren.dingdingmusic.listener.MyOnSearchActionListener;
import com.dingmouren.dingdingmusic.listener.MyOnSeekBarChangeListeger;
import com.dingmouren.dingdingmusic.ui.musicplay.PlayingActivity;
import com.jiongbull.jlog.JLog;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/2/10.
 */

public class SearchActivity extends BaseActivity  implements SearchConstract.View{
    private static final String TAG = SearchActivity.class.getName();
    @BindView(R.id.search_bar) public MaterialSearchBar mSearchBar;
    @BindView(R.id.recycler) RecyclerView mRecycler;
    @BindView(R.id.tv_empty) TextView mTvEmpty;

    private SearchAdapter mAdapter;
    public SearchPresenter mPresenter;
    public MusicBean mMusicBean;
    public List<MusicBean> mList;
    private MyOnSearchActionListener myOnSearchActionListener;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_search;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        myOnSearchActionListener = new MyOnSearchActionListener(this);
    }

    @Override
    public void initView() {
        mSearchBar.enableSearch();//输入状态

        mAdapter = new SearchAdapter(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mAdapter);
        mPresenter = new SearchPresenter((SearchConstract.View)this);

    }


    @Override
    public void initListener() {


        mAdapter.setOnItemClickListener(bean -> playSong(bean));

        mSearchBar.setOnSearchActionListener(myOnSearchActionListener);
    }



    @Override
    public void initData() {

    }

    @Override
    public void setData(List<SearchBean> list) {
        JLog.e(TAG,"setData--"+ list.toString());
        if (0 == list.size()){
            mTvEmpty.setVisibility(View.VISIBLE);
        }else {
            mTvEmpty.setVisibility(View.GONE);
        }
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
    }



    /**
     * 播放被点击的歌曲
     */
    private void playSong(SearchBean bean){
            if (null != bean){
                mMusicBean = new MusicBean();
                mList = new ArrayList<>();
                mMusicBean.setUrl(bean.getM4a());
                mMusicBean.setSeconds(0);
                mMusicBean.setAlbumpic_big(bean.getAlbumpic_big());
                mMusicBean.setAlbumpic_small(bean.getAlbumpic_small());
                mMusicBean.setSongname(bean.getSongname());
                mMusicBean.setSingername(bean.getSingername());
                mList.add(mMusicBean);
                if (null != mList && 0 < mList.size()) {
                    Intent intent = new Intent(this, PlayingActivity.class);
                    intent.putExtra(Constant.SEARCH_ACTIVITY_DATA_KEY, (Serializable) mList);
                    intent.putExtra("flag", Constant.MUSIC_SEARCH);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    new Handler().postDelayed(() -> finish(), 800);
                }
            }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher().watch(this);
    }
}
