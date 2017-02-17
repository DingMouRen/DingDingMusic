package com.dingmouren.dingdingmusic.ui.jk;

import android.app.ActivityOptions;
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
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.ui.musicplay.PlayingActivity;
import com.dingmouren.greendao.MusicBeanDao;
import com.jiongbull.jlog.JLog;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;

/**
 * Created by dingmouren on 2017/2/7.
 */

public class JKActivity extends BaseActivity implements JKConstract.View{
    private static final String TAG = JKActivity.class.getName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.collapsing) CollapsingToolbarLayout mCollapsing;
    @BindView(R.id.recycler) RecyclerView mRecycler;
    @BindView(R.id.swipe_refersh) SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.coordinator_jk) CoordinatorLayout mRootLayout;
    private JKAdapter mAdapter;
    private List<MusicBean> mList;
    private Messenger mServiceMessenger;
    private JKPresenter mPresenter;
    Messenger mMessengerClient;
    private MyHandler myHandler;
    private MyRunnbale myRunnbale;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_jk;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setTransiton();
        bindService(new Intent(this, MediaPlayerService.class),mServiceConnection,BIND_AUTO_CREATE);
        myHandler = new MyHandler(this);
        myRunnbale = new MyRunnbale(this);
        mMessengerClient = new Messenger(myHandler);
    }

    private void setTransiton() {
        getWindow().setEnterTransition(new Fade());
    }

    @Override
    public void initView() {

        mCollapsing.setTitle("");
        mCollapsing.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mCollapsing.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener((view -> onBackPressed()));

        mAdapter = new JKAdapter(this);
        mAdapter.setOnItemClickListener((view, position) -> playSong(position));
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mAdapter);

        mPresenter = new JKPresenter((JKConstract.View)this);

    }

    @Override
    public void initListener() {
        mSwipeRefresh.setOnRefreshListener(()-> mPresenter.requestData());
    }

    @Override
    public void initData() {
        mList = MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.Type.eq(Constant.MUSIC_KOREA)).list();
        if (null != mList) {
            mAdapter.setList(mList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setData(List<MusicBean> list) {
            Collections.shuffle(list);
            mAdapter.setList(list);
            mAdapter.notifyDataSetChanged();
            setRefresh(false);
    }

    @Override
    public void setRefresh(boolean refresh) {
        if (refresh){
            mSwipeRefresh.setRefreshing(true);
        }else {
            myHandler.postDelayed(myRunnbale,1500);
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        mRootLayout.removeAllViews();
        super.onDestroy();
//        MyApplication.getRefWatcher().watch(this);
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


    /**
     * 播放被点击的歌曲
     * @param position
     */
    private void playSong(int position){
        Intent intent = new Intent(this, PlayingActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("flag",Constant.MUSIC_KOREA);
        JLog.e(TAG,"点击JK一首音乐");
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    static class MyHandler extends Handler{
        private WeakReference<JKActivity> weakActivity;
        public MyHandler(JKActivity activity) {
            weakActivity = new WeakReference<JKActivity>(activity);
        }

        @Override
        public void handleMessage(Message msgFromService) {
            JKActivity activity = weakActivity.get();
            if (null == activity) return;
            switch (msgFromService.what){
                case Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING://通过Bundle传递对象,显示正在播放的歌曲
                    JLog.e(TAG,"收到消息了");
                    Bundle bundle = msgFromService.getData();
                    activity.mAdapter.showPlaying((MusicBean) bundle.getSerializable(Constant.MEDIA_PLAYER_SERVICE_MODEL_PLAYING));
                    activity.mAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msgFromService);
        }
    }

    static class MyRunnbale implements Runnable{
        private WeakReference<JKActivity> weakActivity;
        public MyRunnbale(JKActivity activity) {
            weakActivity = new WeakReference<JKActivity>(activity);
        }

        @Override
        public void run() {
            JKActivity activity = weakActivity.get() ;
            if (activity != null) {
                activity.mSwipeRefresh.setRefreshing(false);
            }
        }
    }
}
