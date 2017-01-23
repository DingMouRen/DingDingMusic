package com.dingmouren.dingdingmusic.ui.localmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.bean.LocalMusicBean;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;

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
    private Messenger mServiceMessenger;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_localmusic;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        bindService(new Intent(this, MediaPlayerService.class),mServiceConnection,BIND_AUTO_CREATE);
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
        mAdapter.setOnItemClickListener((view, position) -> playSong(position));
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
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    @Override
    public void setRefresh(boolean refresh) {
           if (refresh){
               mSwipeRefresh.setRefreshing(true);
           }else {
               new Handler().postDelayed(()-> mSwipeRefresh.setRefreshing(false),1000);
           }
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceMessenger = new Messenger(iBinder);
            //连接到服务
            if (null != mServiceMessenger){
                Message msgToService = Message.obtain();
                msgToService.replyTo = mMessengerClient;
                msgToService.what = Constant.LOCAL_MUSIC_ACTIVITY;
                try {
                    mServiceMessenger.send(msgToService);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    Messenger mMessengerClient = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msgFromService) {
            switch (msgFromService.what){
                case Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING:
                    TextView tv = (TextView) mRecycler.getChildAt((Integer) msgFromService.obj).findViewById(R.id.tv_song_name);
                    tv.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    break;
            }
            super.handleMessage(msgFromService);
        }
    });

    /**
     * 播放被点击的歌曲
     * @param position
     */
    private void playSong(int position){
        Message msgToService = Message.obtain();
        msgToService.arg1 = position;
        msgToService.what = Constant.LOCAL_MUSIC_ACTIVITY_PLAY;
        try {
            mServiceMessenger.send(msgToService);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
