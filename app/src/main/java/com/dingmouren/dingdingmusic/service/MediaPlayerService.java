package com.dingmouren.dingdingmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.notification.MusicNotification;
import com.dingmouren.dingdingmusic.receiver.ScreenOffReceiver;
import com.dingmouren.dingdingmusic.utils.NetworkUtil;
import com.dingmouren.dingdingmusic.utils.SPUtil;
import com.jiongbull.jlog.JLog;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by dingmouren on 2017/1/18.
 */

public class MediaPlayerService extends Service implements OnPreparedListener, OnCompletionListener, OnErrorListener {
    private static final String TAG = MediaPlayerService.class.getName();
    //音乐列表
    private  List<MusicBean> musicsList = new ArrayList<>();
    private int musicsListSize;
    //通知栏
    private MusicNotification musicNotification;
    private MusicBean bean;
    //MediaPlayer
    private MediaPlayer mediaPlayer;
    private int currentTime = 0;//记录当前播放时间
    //广播接收者
    private MusicBroadCast musicBroadCast;
    //来自通知栏的action
    private final String MUSIC_NOTIFICATION_ACTION_PLAY = "musicnotificaion.To.PLAY";
    private final String MUSIC_NOTIFICATION_ACTION_NEXT = "musicnotificaion.To.NEXT";
    private final String MUSIC_NOTIFICATION_ACTION_CLOSE = "musicnotificaion.To.CLOSE";
    //播放的位置
    private int position = 0;
    //PlayingActivity的Messenger对象
    private Messenger mMessengerPlayingActivity;
    //LocalMusicActivity的Messenger对象
    private Messenger mMessengerLocalMusicActivity;
    //JKActivity的Messenger对象
    private Messenger mMessengerJKMusicActivity;
    //RockActivity的Messenger对象
    private Messenger mMessengerRockMusicActivity;
    //VolksliedActivity的Messenger对象
    private Messenger mMessengerVolksliedMusicActivity;
    //MainActivity的Messenger对象
    private Messenger mMessengerMainActivity;
    //LockActivity的Messenger对象
    private Messenger mMessengerLockActivity;
    //音频管理对象
    private AudioManager mAudioManager;
    //
    public Messenger mServiceMessenger;
    private MyHandler myHandler;
    //息屏的广播
    private ScreenOffReceiver mSreenOffReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        //初始化通知栏
        musicNotification = MusicNotification.getMusicNotification();
        musicNotification.setManager((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        musicNotification.setService(this);
        //初始化MediaPlayer,设置监听事件
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        //注册广播,用于跟通知栏进行通信
        musicBroadCast = new MusicBroadCast(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_NOTIFICATION_ACTION_PLAY);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_NEXT);
        filter.addAction(MUSIC_NOTIFICATION_ACTION_CLOSE);
        registerReceiver(musicBroadCast, filter);
        //初始化音频管理对象
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        requestAudioFocus();
        //初始化服务端的Messenger
        myHandler = new MyHandler(this);
        mServiceMessenger = new Messenger(myHandler);
        //注册息屏的监听广播
        mSreenOffReceiver = new ScreenOffReceiver();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mSreenOffReceiver,filter1);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JLog.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        JLog.e(TAG, "onBind");
        return mServiceMessenger.getBinder();
    }


    @Override
    public boolean onUnbind(Intent intent) {
       JLog.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        JLog.e(TAG, "onDestroy");
        //释放资源
        if (null != mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        //取消通知
        if (null != musicNotification) musicNotification.onCancelMusicNotification();

        if (null != mSreenOffReceiver) unregisterReceiver(mSreenOffReceiver);//注销锁屏的广播
        if (null != musicBroadCast) unregisterReceiver(musicBroadCast);//注销通知的广播
        super.onDestroy();
//        MyApplication.getRefWatcher().watch(this);
    }

    static class MyHandler extends Handler {
        private WeakReference<MediaPlayerService> weakService;
        public MyHandler(MediaPlayerService service) {
            weakService = new WeakReference<MediaPlayerService>(service);
        }

        @Override
        public void handleMessage(Message msgFromClient) {
            MediaPlayerService service = weakService.get();
            if (null == service) return;
            switch (msgFromClient.what) {
                case Constant.PLAYING_ACTIVITY:
                    service.mMessengerPlayingActivity = msgFromClient.replyTo;
                    JLog.e(TAG,"mMessengerPlayingActivity初始化--positon:"+ service.position +" currentTime:"+service.currentTime+" isPlaying:"+service.mediaPlayer.isPlaying()+" isLooping:"+ service.mediaPlayer.isLooping());
                    if (0 != msgFromClient.arg1){
                        service.currentTime = msgFromClient.arg1;
                    }
                    //将现在播放的歌曲发送给PlayingActivity
                    service.updateSongName();
                    break;
                case Constant.PLAYING_ACTIVITY_PLAY:
                    service.playSong(service.position,msgFromClient.arg1);
                    JLog.e(TAG,"点击播放/暂停按钮时执行playSong()");
                    break;
                case Constant.PLAYING_ACTIVITY_NEXT://顺序播放模式下，自动播放下一曲，
                    service.nextSong();
                    break;
                case Constant.PLAYING_ACTIVITY_SINGLE://是否单曲循环
                    int playMode = (int) SPUtil.get(MyApplication.mContext,Constant.SP_PLAY_MODE,0);
                    if (0 == playMode){
                        service.mediaPlayer.setLooping(true);
                        SPUtil.put(MyApplication.mContext,Constant.SP_PLAY_MODE,1);
                        service.sendPlayModeMsgToPlayingActivity();
                    }else if (1 == playMode){
                        service.mediaPlayer.setLooping(false);
                        SPUtil.put(MyApplication.mContext,Constant.SP_PLAY_MODE,0);
                        service.sendPlayModeMsgToPlayingActivity();
                    }
                    break;
                case Constant.PLAYING_ACTIVITY_CUSTOM_PROGRESS://在用户拖动进度条的位置播放
                    int percent = msgFromClient.arg1;
                    service.currentTime = percent * service.mediaPlayer.getDuration() / 100;
                    service.mediaPlayer.seekTo(service.currentTime);
                    break;
                case Constant.LOCAL_MUSIC_ACTIVITY:
                    service.mMessengerLocalMusicActivity = msgFromClient.replyTo;
                    service.updateSongPosition(service.mMessengerLocalMusicActivity);
                    break;
                case Constant.LOCK_ACTIVITY:
                    service.mMessengerLockActivity = msgFromClient.replyTo;
                    service.updateSongPosition(service.mMessengerLockActivity);
                    break;
                case Constant.LOCK_ACTIVITY_PRE:
                    service.preSong();
                    break;
                case Constant.LOCK_ACTIVITY_PLAY:
                    service.playSong(service.position,msgFromClient.arg1);
                    break;
                case Constant.LOCK_ACTIVITY_NEXT:
                    service.nextSong();
                    break;
                case Constant.JK_MUSIC_ACTIVITY:
                    service.mMessengerJKMusicActivity = msgFromClient.replyTo;
                    service.updateSongPosition(service.mMessengerJKMusicActivity);
                    break;
                case Constant.ROCK_MUSIC_ACTIVITY:
                    service.mMessengerRockMusicActivity = msgFromClient.replyTo;
                    service.updateSongPosition(service.mMessengerRockMusicActivity);
                    break;
                case Constant.VOLKSLIED_MUSIC_ACTIVITY:
                    service.mMessengerVolksliedMusicActivity = msgFromClient.replyTo;
                    service.updateSongPosition(service.mMessengerVolksliedMusicActivity);
                    break;
                case Constant.MAIN_ACTIVITY:
                    service.mMessengerMainActivity = msgFromClient.replyTo;
                    break;
                case Constant.PLAYING_ACTIVITY_PLAYING_POSITION:
                    int newPosition = msgFromClient.arg1;
                    service.playSong(newPosition,-1);
                    JLog.e(TAG,"上一首或下一首执行playSong()");
                    break;
                case Constant.PLAYING_ACTIVITY_INIT:
                    Bundle songsData = msgFromClient.getData();
                    service.musicsList.clear();
                    List<MusicBean> clientList = (List<MusicBean>) songsData.getSerializable(Constant.PLAYING_ACTIVITY_DATA_KEY);
                    if (0 == service.musicsList.size() && null != clientList) {//当歌曲集合没有数据的时候
                        service.musicsList.addAll(clientList);
                    }
                    if (null != service.musicsList) service.musicsListSize = service.musicsList.size();
                    if (null != service.musicsList) {
                        JLog.e(TAG, "musicsList--" + service.musicsList.toString());
                        JLog.e(TAG, "musicsListSize--" + service.musicsListSize);
                        JLog.e(TAG, "接收到来自PlayingActivity客户端的数据"  );
                    }
                    service.position = msgFromClient.arg1;
                    JLog.e(TAG,"positon:"+service.position);
                    service.playCustomSong(service.position);
                    break;

            }
            super.handleMessage(msgFromClient);
        }
    }

    /**
     * 将播放器的播放模式返回给PlayingActivity
     */
    private void sendPlayModeMsgToPlayingActivity() {
        if (null != mediaPlayer && null != mMessengerPlayingActivity) {
            Message msgToClient = Message.obtain();
            msgToClient.what = Constant.PLAYING_ACTIVITY_PLAY_MODE;
            try {
                mMessengerPlayingActivity.send(msgToClient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 音乐播放
     *
     * @param musicUrl
     */
    private void play(String musicUrl) {
        //给予无网络提示
        if (!NetworkUtil.isAvailable(MyApplication.mContext)){
            if (bean.getType() != Integer.valueOf(Constant.MUSIC_LOCAL)){
                Toast.makeText(MyApplication.mContext,"没有网络了哟，请检查网络设置",Toast.LENGTH_SHORT).show();
            }
        }
        JLog.e(TAG,"play(String musicUrl)--musicUrl" + musicUrl);
        if (null == mediaPlayer) return;
        mediaPlayer.reset();//停止音乐后，不重置的话就会崩溃
        try {
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(musicUrl));
            mediaPlayer.prepareAsync();
            musicNotification.onUpdateMusicNotification(bean, mediaPlayer.isPlaying());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 音乐暂停
     */
    private void pause() {
        JLog.e(TAG, "pause()");
        if (null == mediaPlayer) return;
        if (mediaPlayer.isPlaying()) {
            currentTime = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            sendIsPlayingMsg();//发送播放器是否在播放的状态
            musicNotification.onUpdateMusicNotification(bean, mediaPlayer.isPlaying());
        }
    }

    /**
     * 音乐继续播放
     */
    public void resume() {
        JLog.e(TAG, "resume()");
        if (null == mediaPlayer) return;
        mediaPlayer.start();
        //播放的同时，更新进度条
        updateSeekBarProgress(mediaPlayer.isPlaying());
        //将现在播放的歌曲发送给PlayingActivity，并将播放的集合传递过去
        updateSongName();
        //将现在播放的歌曲发送给指定Activity
        if (bean.getType() == Integer.valueOf(Constant.MUSIC_LOCAL)) {
            updateSongPosition(mMessengerLocalMusicActivity);
        }else if (bean.getType() == Integer.valueOf(Constant.MUSIC_KOREA)){
            updateSongPosition(mMessengerJKMusicActivity);
        }else if (bean.getType() == Integer.valueOf(Constant.MUSIC_ROCK)){
            updateSongPosition(mMessengerRockMusicActivity);
        }else if (bean.getType() == Integer.valueOf(Constant.MUSIC_VOLKSLIED)) {
            updateSongPosition(mMessengerVolksliedMusicActivity);
        }
        //发送正在播放的歌曲发送给LockActivity
        updateSongPosition(mMessengerLockActivity);
        if (currentTime > 0) {
            mediaPlayer.seekTo(currentTime);
        }
        musicNotification.onUpdateMusicNotification(bean, mediaPlayer.isPlaying());

    }

    /**
     * 停止音乐
     */
    private void stop() {
        JLog.e(TAG, "stop()");
        if (null == mediaPlayer) return;
        mediaPlayer.stop();
        currentTime = 0;//停止音乐，将当前播放时间置为0

    }
    //--------------监听listener
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.e(TAG, "onCompletion");
        if (!mediaPlayer.isLooping()){
            Message msgToServiceNext = Message.obtain();
            msgToServiceNext.what = Constant.PLAYING_ACTIVITY_NEXT;
            try {
                mServiceMessenger.send(msgToServiceNext);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        JLog.e(TAG, "onError--i:" + i + "  i1:" + i1);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        JLog.e(TAG, "onPrepared");
        //准备加载的时候
        resume();
    }


    /**
     * 播放
     */
    private void playSong(int newPosition,int isOnClick) {
        requestAudioFocus();//请求音频焦点
        JLog.e(TAG, "playSong()");
        if (null == musicsList && 0 == musicsList.size()) return;//数据为空直接返回
        if (position != newPosition && newPosition < musicsListSize){//由滑动操作传递过来的歌曲position，如果跟当前的播放的不同的话，就将MediaPlayer重置
            JLog.e(TAG, "playSong()--position:" +position+" newPosition:"+ newPosition);
            mediaPlayer.reset();
            currentTime = 0;
            position = newPosition;
        }
        if (null != musicsList && 0 < musicsList.size()) bean = musicsList.get(position);
        JLog.e(TAG, "playSong()--position:" + position +" currentTime:"+ currentTime);
        if (mediaPlayer.isPlaying() && 0x40001 == isOnClick) {//如果是正在播放状态的话，就暂停
            pause();
        } else {
            if (currentTime > 0) {//currentTime>0说明当前是暂停状态，直接播放
                resume();
            } else {
                if (null != bean) {
                    play(bean.getUrl());
                    //每当从头开始播放一首歌曲时，每秒发送一条播放进度的消息，
                    if (null != mServiceMessenger) {
                        Message msgFromServiceProgress = Message.obtain();
                        msgFromServiceProgress.what = Constant.MEDIA_PLAYER_SERVICE_PROGRESS;
                        try {
                            mServiceMessenger.send(msgFromServiceProgress);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }



    /**
     * 下一首
     */
    private void nextSong() {
        JLog.e(TAG, "nextSong()");
        currentTime = 0;
        if (position < 0) {
            position = 0;
        }
        if (musicsListSize > 0) {
            position++;
            if (position < musicsListSize) {//当前歌曲的索引小于歌曲集合的长度
                bean = musicsList.get(position);
                play(bean.getUrl());
            } else {
                position = 0;
                bean = musicsList.get(position);
                play(bean.getUrl());
            }
            //通知PalyingActivity跟换专辑图片  歌曲信息等
            if (null != mMessengerPlayingActivity){
                Message msgToClient = Message.obtain();
                msgToClient.arg1 = position;
                msgToClient.what = Constant.MEDIA_PLAYER_SERVICE_UPDATE_SONG;
                try {
                    mMessengerPlayingActivity.send(msgToClient);
                    JLog.e(TAG,"自动播放下一首，发送更新UI的消息");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 上一首
     */
    private void preSong() {
        JLog.e(TAG, "preSong()");
        currentTime = 0;
        if (position < 0) {
            position = 0;
        }
        if (musicsListSize > 0) {
            position--;
            if (position >= 0) {//大于等于0的情况
                bean = musicsList.get(position);
                play(bean.getUrl());
            } else {
                bean = musicsList.get(0);//小于0时，播放第一首歌
                play(bean.getUrl());
            }
        }
    }


    /**
     * 更新进度条进度，通过发送消息的方式
     * @param playing
     */
    private void updateSeekBarProgress(boolean playing) {
        JLog.e(TAG,"updateSeekBarProgress更新进度条");
        Observable.interval(1,1, TimeUnit.SECONDS, Schedulers.computation()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                sendUpdateProgressMsg();
            }
        });
    }

    //发送更新进度的消息
    private void sendUpdateProgressMsg(){
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            Message msgToPlayingAcitvity = Message.obtain();
            msgToPlayingAcitvity.what = Constant.MEDIA_PLAYER_SERVICE_PROGRESS;
            msgToPlayingAcitvity.arg1 = mediaPlayer.getCurrentPosition();
            msgToPlayingAcitvity.arg2 = mediaPlayer.getDuration();
//            JLog.e(TAG, "发给客户端的时间--getCurrentPosition:" + mediaPlayer.getCurrentPosition() + " getDuration" + mediaPlayer.getDuration());
            try {
                if (null != mMessengerPlayingActivity) {
                    mMessengerPlayingActivity.send(msgToPlayingAcitvity);
//                    Log.e(TAG, "发消息了");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将正在播放的歌曲名称发送给PlayingActivity,并将播放的集合传递过去
     */
    private void  updateSongName(){
        sendIsPlayingMsg();//发送播放器是否在播放的状态
        if (null != mMessengerPlayingActivity && null != this.musicsList) {
            Message msgToCLient = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MEDIA_PLAYER_SERVICE_MODEL_PLAYING, (Serializable) this.musicsList);
            msgToCLient.setData(bundle);
            msgToCLient.arg1 = position;
            msgToCLient.what = Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING;
            JLog.e(TAG,"updateSongName()--position:"+ position);
            try {
                mMessengerPlayingActivity.send(msgToCLient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将正在播放的歌曲的position发送给Activity
     */
    private void updateSongPosition(Messenger messenger){
        if (null != messenger && null != this.bean){
            JLog.e(TAG,"updateSongPosition--发送消息");
            Message msgToCLient = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.MEDIA_PLAYER_SERVICE_MODEL_PLAYING,this.bean);
            msgToCLient.setData(bundle);
            msgToCLient.what = Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING;
            try {
                messenger.send(msgToCLient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放本地音乐列表中被点击的歌曲
     * @param position
     */
    private void playCustomSong(int position){
        if (null != bean && null != musicsList) {
            if (!musicsList.get(position).getSongname().equals(bean.getSongname())) {
                this.currentTime = 0;
                this.position = position;
            }
        }
        bean = musicsList.get(position);
        JLog.e(TAG,"position:" + position +" musiclist:"+musicsList.toString() );
        if (null != bean){
            play(bean.getUrl());

        }
    }

    /**
     * 发送播放器是否在播放的状态，更新PlayingActivity的UI
     */
    private void sendIsPlayingMsg(){
        JLog.e("lala","发送播放状态的消息");
        if (null != mMessengerPlayingActivity) {
            Message msgToClient = Message.obtain();//发送给PlayingActivity
            msgToClient.arg1 = mediaPlayer.isPlaying() ? 1 : 0;//1表示在播放，0 表示没有播放
            msgToClient.what = Constant.MEDIA_PLAYER_SERVICE_IS_PLAYING;
            Message msgToMain = Message.obtain();//发给MainActivity
            msgToMain.arg1 = mediaPlayer.isPlaying() ? 1 : 0;//1表示在播放，0 表示没有播放
            msgToMain.what = Constant.MEDIA_PLAYER_SERVICE_IS_PLAYING;
            Message msgToLock = Message.obtain();//发送给LockActivity
            msgToLock.arg1 = mediaPlayer.isPlaying() ? 1 : 0;//1表示在播放，0 表示没有播放
            msgToLock.what = Constant.MEDIA_PLAYER_SERVICE_IS_PLAYING;
            try {
                mMessengerPlayingActivity.send(msgToClient);
                if (null != mMessengerMainActivity) {
                    mMessengerMainActivity.send(msgToMain);
                }
                if (null != mMessengerLockActivity){
                    mMessengerLockActivity.send(msgToLock);
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 广播接收者:与通知栏进行通信，通知栏控制歌曲的播放、下一首取、取消通知栏（停止音乐播放）
     */
    static class MusicBroadCast extends BroadcastReceiver {
        private final String TAG_BRAODCAST = MusicBroadCast.class.getName();
        private int valueFromNotification = 0;//来自通知的extra中的值
        private WeakReference<MediaPlayerService> weakService;
        public MusicBroadCast(MediaPlayerService service) {
            weakService = new WeakReference<MediaPlayerService>(service);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //MusicNotification的控制
            valueFromNotification = intent.getIntExtra("type", -1);
            if (valueFromNotification > 0) {
                musicNotificationService(valueFromNotification);
            }

        }


        /**
         * 来自通知的控制
         *
         * @param value
         */
        private void musicNotificationService(int value) {
            Log.e(TAG_BRAODCAST, "musicNotificationService");
            MediaPlayerService service = weakService.get();
            if (null == service) return;
            switch (value) {
                case 30001:
                    service.playSong(service.position,0x40001); //播放or暂停
                    break;
                case 30002:
                    service.nextSong();//下一首
                    break;
                case 30003:
                    service.musicNotification.onCancelMusicNotification();//关闭通知栏
                    service.stop();//停止音乐
                    break;
            }
        }

    }

    //音频焦点监听处理
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange){
                case AudioManager.AUDIOFOCUS_GAIN:
                    //获取音频焦点
                    JLog.e(TAG,"AUDIOFOCUS_GAIN");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //永久失去 音频焦点
                    JLog.e(TAG,"AUDIOFOCUS_LOSS");
                    pause();
                    abandonFocus();//放弃音频焦点
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //暂时失去 音频焦点，并会很快再次获得。必须停止Audio的播放，但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源
                    JLog.e(TAG,"AUDIOFOCUS_LOSS_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //暂时失去 音频焦点 ，但是可以继续播放，不过要在降低音量。
                    JLog.e(TAG,"AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    break;
                default:
                    JLog.e(TAG,"default"+ focusChange);
                    break;
            }
        }
    };

    /**
     * 放弃音频焦点
     */
    private void abandonFocus() {
       if (null != onAudioFocusChangeListener){
           mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
       }
    }

    /**
     * 申请音频焦点
     */
    private void requestAudioFocus(){
        JLog.e(TAG,"请求音频焦点requestAudioFocus");
        if (null != onAudioFocusChangeListener){
            int result = mAudioManager.requestAudioFocus(onAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                JLog.e(TAG,"请求音频焦点成功");
            }else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED){
                JLog.e(TAG,"请求音频焦点失败");
            }
        }
    }
}
