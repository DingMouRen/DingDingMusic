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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.ui.musicplay.PlayingActivity;
import com.dingmouren.greendao.MusicBeanDao;
import com.jiongbull.jlog.JLog;

import java.util.List;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/1/17.
 * 本地歌曲列表
 */

public class LocalMusicActivity extends BaseActivity implements LocalMusicConstract.View{
    private static final String TAG = LocalMusicActivity.class.getName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
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
        mAdapter.setOnItemClickListener((view, position) -> playSong(position));
    }

    @Override
    public void initData() {
        mPresenter = new LocalMusicPresenter((LocalMusicConstract.View) this);
            mPresenter.requestData();
    }


    @Override
    public void setData(List<MusicBean> list) {
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
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
                case Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING://通过Bundle传递对象,显示正在播放的本地歌曲
                    Bundle bundle = msgFromService.getData();
                    mAdapter.showPlaying((MusicBean) bundle.getSerializable(Constant.MEDIA_PLAYER_SERVICE_MODEL_PLAYING));
                    mAdapter.notifyDataSetChanged();
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
       Intent intent = new Intent(this, PlayingActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("flag",Constant.MUSIC_LOCAL);
        JLog.e(TAG,"点击本地的一首音乐");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
