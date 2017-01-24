package com.dingmouren.dingdingmusic.ui.musicplay;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.bean.LocalMusicBean;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.ui.MainActivity;
import com.jiongbull.jlog.JLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dingmouren on 2017/1/17.
 */

public class PlayingActivity extends BaseActivity {

    private static final String TAG = PlayingActivity.class.getName();

    @BindView(R.id.seek_bar)  SeekBar mSeekBar;
    @BindView(R.id.tv_song_name) TextView mTvSongName;
    @BindView(R.id.tv_singer) TextView mTvSinger;
    @BindView(R.id.img_cover) ImageView mImgCover;
    @BindView(R.id.btn_playorpause) ImageButton mBtnPlay;

    public Messenger mServiceMessenger;//来自服务端的Messenger
    private boolean isConnected = false;//标记是否连接上了服务端
    private float mPercent;//进度的百分比

    int[] imgsArr = {R.mipmap.native_1,R.mipmap.native_2,R.mipmap.native_3,R.mipmap.native_4,R.mipmap.native_5,R.mipmap.native_6,R.mipmap.native_7,R.mipmap.native_8,R.mipmap.native_9};
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_musicplayer;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        bindService(new Intent(getApplicationContext(),MediaPlayerService.class),mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void initView() {
        mSeekBar.setProgress(0);
        mSeekBar.setMax(100);
    }

    @Override
    public void initListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){//判断来自用户的滑动
                    mPercent = (float) progress * 100 /(float) mSeekBar.getMax();
//                    Log.e(TAG,"onProgressChanged--mPercent:"+mPercent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //用户松开SeekBar，通知MediaPlayerService更新播放器的进度，解决拖动过程中卡顿的问题
                Message msgToMediaPlayerService = Message.obtain();
                msgToMediaPlayerService.what  = Constant.PLAYING_ACTIVITY_CUSTOM_PROGRESS;
                msgToMediaPlayerService.arg1 = (int) mPercent;
//                Log.e(TAG,"onStopTrackingTouch--mPercent:"+(int) mPercent);
                try {
                    mServiceMessenger.send(msgToMediaPlayerService);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.btn_pre, R.id.btn_playorpause, R.id.btn_next,R.id.btn_single})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pre://上一首
                Message msgToServicePre = Message.obtain();
                msgToServicePre.what = Constant.PLAYING_ACTIVITY_PRE;
                try {
                    mServiceMessenger.send(msgToServicePre);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mImgCover.setImageResource(imgsArr[new Random().nextInt(9)]);//更换封面
                break;
            case R.id.btn_playorpause://播放or暂停
                Message msgToServicePlay = Message.obtain();
                msgToServicePlay.what = Constant.PLAYING_ACTIVITY_PLAY;
                try {
                    mServiceMessenger.send(msgToServicePlay);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mImgCover.setImageResource(imgsArr[new Random().nextInt(9)]);//更换封面
                break;
            case R.id.btn_next://下一首
                 Message msgToServiceNext = Message.obtain();
                msgToServiceNext.what = Constant.PLAYING_ACTIVITY_NEXT;
                try {
                    mServiceMessenger.send(msgToServiceNext);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mImgCover.setImageResource(imgsArr[new Random().nextInt(9)]);//更换封面
                break;
            case R.id.btn_single:
                Message msgToServceSingle = Message.obtain();
                msgToServceSingle.what = Constant.PLAYING_ACTIVITY_SINGLE;
                try {
                    mServiceMessenger.send(msgToServceSingle);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG,"onServiceConnected");
            mServiceMessenger = new Messenger(iBinder);
            isConnected = true;
            //用于在服务端初始化来自客户端的Messenger对象,连接成功的时候，就进行初始化
            if (null != mServiceMessenger){
                Message msgToService = Message.obtain();
                msgToService.replyTo = mPlaygingClientMessenger;
                msgToService.what = Constant.PLAYING_ACTIVITY;
                try {
                    mServiceMessenger.send(msgToService);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            //连接成功的时候吗，将歌曲的数据集合传递给播放器
            if (null != mServiceMessenger){
                Message msgToServiceData = Message.obtain();
                Bundle songsData = new Bundle();
                songsData.putSerializable(Constant.PLAYING_ACTIVITY_DATA_KEY, (Serializable) MyApplication.getDaoSession().getLocalMusicBeanDao().loadAll());
                msgToServiceData.setData(songsData);
                msgToServiceData.what = Constant.PLAYING_ACTIVITY_DATA;
                try {
                    mServiceMessenger.send(msgToServiceData);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG,"onServiceDisconnected");
            isConnected = false;
        }
    };

    Messenger mPlaygingClientMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msgFromService) {
            switch (msgFromService.what){
                case Constant.MEDIA_PLAYER_SERVICE_PROGRESS://更新进度条
                    int position = msgFromService.arg1;
                    int duration = msgFromService.arg2;
                    if (0 == duration) break;
                    mSeekBar.setProgress(position * 100 /duration);
                    break;
                case Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING:
                    Bundle bundle = msgFromService.getData();
                    LocalMusicBean bean = (LocalMusicBean) bundle.getSerializable(Constant.MEDIA_PLAYER_SERVICE_MODEL_PLAYING);
                    mTvSongName.setText(bean.getTitle());
                    mTvSinger.setText(bean.getArtist());
                    break;
                case Constant.MEDIA_PLAYER_SERVICE_IS_PLAYING:
                    if (1 == msgFromService.arg1){//正在播放
                        mBtnPlay.setImageResource(R.mipmap.play);
                    }else {
                        mBtnPlay.setImageResource(R.mipmap.pause);
                    }
                    break;
            }
            super.handleMessage(msgFromService);
        }
    });

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
