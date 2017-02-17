package com.dingmouren.dingdingmusic.ui.lock;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.transition.Fade;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.view.SildingFinishLayout;
import com.jiongbull.jlog.JLog;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by dingmouren on 2017/2/16.
 */

public class LockActivity extends BaseActivity {

    private static final String TAG = LockActivity.class.getName();

    @BindView(R.id.container) SildingFinishLayout mRootLayout;
    @BindView(R.id.img_bg)  ImageView mImgBg;
    @BindView(R.id.img_album)  ImageView mImgAlbum;
    @BindView(R.id.tv_song_name)TextView mTvSongName;
    @BindView(R.id.img_pre) ImageButton mImgPre;
    @BindView(R.id.img_paly) ImageButton mImgPlay;
    @BindView(R.id.img_next) ImageButton mImgNext;
    private Messenger mServiceMessenger;
    private Messenger mMessengerClient;
    private MyHandler myHandler;

    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_lock;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        //隐藏系统锁屏
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        bindService(new Intent(getApplicationContext(), MediaPlayerService.class), mServiceConnection, BIND_AUTO_CREATE);
        myHandler = new MyHandler(this);
        mMessengerClient = new Messenger(myHandler);

    }

    @Override
    public void initView() {
        mRootLayout.setTouchView(getWindow().getDecorView());
        mRootLayout.setOnSildingFinishListener(()-> finish());
        setTransition();
    }

    private void setTransition() {
        getWindow().setExitTransition(new Fade());
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.img_pre,R.id.img_paly,R.id.img_next})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_pre:
                if (null != mServiceMessenger) {
                    Message msgToServicePlay = Message.obtain();
                    msgToServicePlay.what = Constant.LOCK_ACTIVITY_PRE;
                    try {
                        mServiceMessenger.send(msgToServicePlay);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
              case R.id.img_paly:
                  if (null != mServiceMessenger) {
                      Message msgToServicePlay = Message.obtain();
                      msgToServicePlay.arg1 = 0x40001;//表示这个暂停是由点击按钮造成的，
                      msgToServicePlay.what = Constant.LOCK_ACTIVITY_PLAY;
                      try {
                          mServiceMessenger.send(msgToServicePlay);
                      } catch (RemoteException e) {
                          e.printStackTrace();
                      }
                  }
                break;
              case R.id.img_next:
                  if (null != mServiceMessenger) {
                      Message msgToServicePlay = Message.obtain();
                      msgToServicePlay.what = Constant.LOCK_ACTIVITY_NEXT;
                      try {
                          mServiceMessenger.send(msgToServicePlay);
                      } catch (RemoteException e) {
                          e.printStackTrace();
                      }
                  }
                break;

        }
    }

    //屏蔽按键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_BACK: {
                return true;
            }
            case KeyEvent.KEYCODE_MENU:{
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mRootLayout.removeAllViews();
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
                msgToService.what = Constant.LOCK_ACTIVITY;
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

    static class MyHandler extends Handler {
        private WeakReference<LockActivity> weakActivity;
        private MusicBean mBean;
        public MyHandler(LockActivity activity) {
            weakActivity = new WeakReference<LockActivity>(activity);
        }

        @Override
        public void handleMessage(Message msgFromService) {
            LockActivity activity = weakActivity.get();
            if (null == activity) return;
            switch (msgFromService.what){
                case Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING://通过Bundle传递对象,显示正在播放的歌曲
                    JLog.e(TAG,"收到消息了");
                    Bundle bundle = msgFromService.getData();
                    mBean = (MusicBean) bundle.getSerializable(Constant.MEDIA_PLAYER_SERVICE_MODEL_PLAYING);
                    activity.mTvSongName.setText(mBean.getSongname());
                    Glide.with(activity).load(mBean.getAlbumpic_big()).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.mipmap.dingding_icon).into(activity.mImgAlbum);
                    Glide.with(MyApplication.mContext)//底部的模糊效果
                            .load(mBean.getAlbumpic_big())
                            .bitmapTransform(new BlurTransformation(activity, 99))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .crossFade()
                            .placeholder(R.mipmap.bg2)
                            .into(activity.mImgBg);
                    break;
                case Constant.MEDIA_PLAYER_SERVICE_IS_PLAYING:
                    JLog.e(TAG,"收到更新播放状态的消息");
                    if (1 == msgFromService.arg1) {//正在播放
                        activity.mImgPlay.setImageResource(R.mipmap.play);
                    } else {
                        activity.mImgPlay.setImageResource(R.mipmap.pause);
                    }
            }
            super.handleMessage(msgFromService);
        }
    }
}
