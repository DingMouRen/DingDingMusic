package com.dingmouren.dingdingmusic.ui.jk;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.CollapsingToolbarLayout;
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
 * Created by dingmouren on 2017/2/7.
 */

public class JKActivity extends BaseActivity implements JKConstract.View{
    private static final String TAG = JKActivity.class.getName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.collapsing) CollapsingToolbarLayout mCollapsing;
    @BindView(R.id.recycler) RecyclerView mRecycler;

    private JKAdapter mAdapter;
    private List<MusicBean> mLiist;
    private Messenger mServiceMessenger;
    private JKPresenter mPresenter;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_jk;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        bindService(new Intent(this, MediaPlayerService.class),mServiceConnection,BIND_AUTO_CREATE);
    }

    @Override
    public void initView() {

        mCollapsing.setTitle("");
        mCollapsing.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mCollapsing.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener((view -> finish()));

        mAdapter = new JKAdapter(this);
        mAdapter.setOnItemClickListener((view, position) -> playSong(position));
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mAdapter);

//        mPresenter = new JKPresenter((JKConstract.View)this);

    }

    @Override
    public void initData() {
//        mPresenter.requestData();
        setData(null);
    }

    @Override
    public void setData(List<MusicBean> list) {
        mLiist = MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.Type.eq(Constant.MUSIC_KOREA)).list();
        if (null != mLiist) {
            mAdapter.setList(mLiist);
            mAdapter.notifyDataSetChanged();
        }
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
                msgToService.what = Constant.JK_MUSIC_ACTIVITY;
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
                case Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING://通过Bundle传递对象,显示正在播放的歌曲
                    JLog.e(TAG,"收到消息了");
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
        intent.putExtra("flag",Constant.MUSIC_KOREA);
        JLog.e(TAG,"点击JK一首音乐");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
