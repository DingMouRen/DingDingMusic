package com.dingmouren.dingdingmusic.ui.localmusic;

import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.bean.LocalMusicBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/1/17.
 * 本地歌曲列表
 */

public class LocalMusicActivity extends BaseActivity implements LocalMusicConstract.View{
    private static final String TAG = LocalMusicActivity.class.getName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.swipe_refresh)  SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.recycler) RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private LocalMusicConstract.Presenter mPresenter;
    private LocalMusicAdapter mAdapter;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_localmusic;
    }

    @Override
    public void initView() {
        mToolbar.setTitle(R.string.txt_local_music);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new LocalMusicAdapter(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        mToolbar.setNavigationOnClickListener((view)->finish());//点击箭头返回
        mSwipeRefresh.setOnRefreshListener(()-> mPresenter.requestData());
    }

    @Override
    public void initData() {
        mPresenter = new LocalMusicPresenter((LocalMusicConstract.View) this);
        setRefresh(true);
        mPresenter.requestData();
    }


    @Override
    public void setData(List<LocalMusicBean> list) {
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
        setRefresh(false);
    }

    @Override
    public void setRefresh(boolean refresh) {
           if (refresh){
               mSwipeRefresh.setRefreshing(true);
           }else {
               new Handler().postDelayed(()-> mSwipeRefresh.setRefreshing(false),1000);
           }
    }

}
