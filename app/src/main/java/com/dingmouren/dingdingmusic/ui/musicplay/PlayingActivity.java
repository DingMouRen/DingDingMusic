package com.dingmouren.dingdingmusic.ui.musicplay;

import android.animation.Animator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.listener.MyOnPageChangeListeger;
import com.dingmouren.dingdingmusic.listener.MyOnSeekBarChangeListeger;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.utils.SPUtil;
import com.dingmouren.greendao.MusicBeanDao;
import com.jiongbull.jlog.JLog;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by dingmouren on 2017/1/17.
 */

public class PlayingActivity extends BaseActivity {

    private static final String TAG = PlayingActivity.class.getName();

    @BindView(R.id.seek_bar) public SeekBar mSeekBar;
//    @BindView(R.id.tv_song_name)TextView mTvSongName;
//    @BindView(R.id.tv_singer)TextView mTvSinger;
    @BindView(R.id.album_viewpager)ViewPager mAlbumViewPager;
    @BindView(R.id.btn_playorpause)ImageButton mBtnPlay;
    @BindView(R.id.btn_single)ImageButton mPlayMode;
    @BindView(R.id.img_bg) public ImageView mImgBg;
    @BindView(R.id.contanier_play_activity)PercentRelativeLayout mRootLayout;
    @BindView(R.id.btn_like)ImageButton mBtnLike;
    @BindView(R.id.btn_share)ImageButton mBtnShare;
    @BindView(R.id.tv_category)TextView mTvCategory;
    @BindView(R.id.line_playing)View mLine;

    public Messenger mServiceMessenger;//来自服务端的Messenger
    public float mPercent;//进度的百分比
    public AlbumFragmentAdapater mAlbumFragmentAdapater;//专辑图片的适配器
    public int mPosition;//传递过来的的歌曲的位置
    public int mPositionPlaying;//正在播放的歌曲的位置
    public String flag;//歌曲集合的类型
    public int currentTime;//实时当前进度
    public int duration;//歌曲的总进度
    public float mPositionOffset;//viewpager滑动的百分比
    public int mState;//viewpager的滑动状态
    public List<MusicBean> mList = new ArrayList<>();
    public int enterX;//传递过来的x坐标，是点击View的中心点的x坐标，揭露动画
    public int enterY;//传递过来的y坐标，是点击View的中心点的y坐标，揭露动画
    public String shareSongName;
    public String shareSingerName;
    public String shareUrl;
    public String shareContent;
    public String songNamePlaying;
    public String singerNamePlaying;
    public MusicBean beanToCollected;//要被收藏的bean
    public MyOnPageChangeListeger myOnPageChangeListeger;//页面切换监听
    public MyRunnable myRunnable;
    private MyOnSeekBarChangeListeger myOnSeekBarChangeListeger;//seekbar的监听
    private Messenger mPlaygingClientMessenger;
    private MyHandler myHandler;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_musicplayer;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setTransiton();
        bindService(new Intent(getApplicationContext(), MediaPlayerService.class), mServiceConnection, BIND_AUTO_CREATE);
        myOnPageChangeListeger = new MyOnPageChangeListeger(this);
        myRunnable = new MyRunnable(this);
        myOnSeekBarChangeListeger = new MyOnSeekBarChangeListeger(this);
        myHandler = new MyHandler(this);
        mPlaygingClientMessenger = new Messenger(myHandler);
    }


    @Override
    public void initView() {
        updatePlayMode();
        mSeekBar.setMax(100);

        mAlbumFragmentAdapater = new AlbumFragmentAdapater(getSupportFragmentManager());
        mAlbumViewPager.setAdapter(mAlbumFragmentAdapater);
        mAlbumViewPager.setOffscreenPageLimit(6);

        //揭露动画
        mRootLayout.post(myRunnable);

        //左上角显示类别
        showCategory();

    }


    @Override
    public void initListener() {
        //进度条的监听
        mSeekBar.setOnSeekBarChangeListener(myOnSeekBarChangeListeger);

        //滑动播放上/下一首歌曲的监听,实际上传递过去的是歌曲的position
        mAlbumViewPager.addOnPageChangeListener(myOnPageChangeListeger);

        //分享功能
        mBtnShare.setOnClickListener((view -> share()));
        mBtnLike.setOnClickListener((view -> collect()));

    }


    /**
     * 显示是否收藏歌曲
     */
    public void showIsLike() {
        JLog.e(TAG,"showLike:" + songNamePlaying +"--"+ singerNamePlaying);
        if (null == songNamePlaying || null == singerNamePlaying || null == mList) return;
        List<MusicBean> list = MyApplication.getDaoSession().getMusicBeanDao().queryBuilder()
                .where(MusicBeanDao.Properties.Singername.eq(singerNamePlaying),
                        MusicBeanDao.Properties.Songname.eq(songNamePlaying)).list();
        if (null != list && 0 <list.size()){
            boolean isCollected = list.get(0).getIsCollected();
            if (isCollected){
                Glide.with(MyApplication.mContext).load(R.mipmap.collected).crossFade().into(mBtnLike);
            }else {
                Glide.with(MyApplication.mContext).load(R.mipmap.no_collected).crossFade().into(mBtnLike);
            }
        }
    }

    /**
     * 收藏歌曲
     */
    private void collect() {
            if (null == mList) return;
            JLog.e(TAG,"collected:"+songNamePlaying+"--"+ singerNamePlaying);
            MusicBean newBean = null;
            boolean isCollected = false;
            List<MusicBean> listCollected = MyApplication.getDaoSession().getMusicBeanDao().queryBuilder()
                    .where(MusicBeanDao.Properties.Singername.eq(singerNamePlaying),
                            MusicBeanDao.Properties.Songname.eq(songNamePlaying)).list();
            if (null != listCollected && 0 < listCollected.size()) {
                beanToCollected = listCollected.get(0);
                isCollected = beanToCollected.getIsCollected();
            }else {
                newBean = new MusicBean();
                newBean.setSongname(mList.get(mPositionPlaying).getSongname());
                newBean.setSingername(mList.get(mPositionPlaying).getSingername());
                newBean.setAlbumpic_small(mList.get(mPositionPlaying).getAlbumpic_small());
                newBean.setAlbumpic_big(mList.get(mPositionPlaying).getAlbumpic_big());
                newBean.setSeconds(mList.get(mPositionPlaying).getSeconds());
                newBean.setUrl(mList.get(mPositionPlaying).getUrl());
                newBean.setIsCollected(mList.get(mPositionPlaying).getIsCollected());
            }


            if (!isCollected){
                JLog.e(TAG,"收藏歌曲");
                Glide.with(MyApplication.mContext).load(R.mipmap.collected).crossFade().into(mBtnLike);
                if (null != beanToCollected) {
                    beanToCollected.setIsCollected(true);
                    MyApplication.getDaoSession().getMusicBeanDao().update(beanToCollected);
                }
                if (null != newBean){
                    newBean.setIsCollected(true);
                    MyApplication.getDaoSession().getMusicBeanDao().insertOrReplace(newBean);
                }
                Snackbar.make(mRootLayout,"收藏成功",Snackbar.LENGTH_SHORT).show();
            }else {
                JLog.e(TAG,"取消收藏");
                Glide.with(MyApplication.mContext).load(R.mipmap.no_collected).crossFade().into(mBtnLike);
                if (null != beanToCollected) {
                    beanToCollected.setIsCollected(false);
                    MyApplication.getDaoSession().getMusicBeanDao().update(beanToCollected);
                }
                Snackbar.make(mRootLayout,"取消收藏",Snackbar.LENGTH_SHORT).show();
            }
    }

    /**
     * 分享功能
     */
    private void share() {
        if (null != mList) {
            shareSongName = mList.get(mPositionPlaying).getSongname();
            shareSingerName = mList.get(mPositionPlaying).getSingername();
            shareUrl = mList.get(mPositionPlaying).getUrl();
            shareContent = shareSongName  + "--" + shareSingerName + "\n" + shareUrl;
        }
        if ("".equals(shareContent)) {
            Snackbar.make(mRootLayout, "分享失败", Snackbar.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, shareContent);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "分享到"));
        }

    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.btn_playorpause, R.id.btn_single})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_playorpause://播放or暂停
                if (null != mServiceMessenger) {
                    Message msgToServicePlay = Message.obtain();
                    msgToServicePlay.arg1 = 0x40001;//表示这个暂停是由点击按钮造成的，
                    msgToServicePlay.what = Constant.PLAYING_ACTIVITY_PLAY;
                    try {
                        mServiceMessenger.send(msgToServicePlay);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_single://顺序播放还是单曲循环
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
            Log.e(TAG, "onServiceConnected");
            mServiceMessenger = new Messenger(iBinder);
            //用于在服务端初始化来自客户端的Messenger对象,连接成功的时候，就进行初始化
            if (null != mServiceMessenger) {
                Message msgToService = Message.obtain();
                msgToService.replyTo = mPlaygingClientMessenger;
                msgToService.what = Constant.PLAYING_ACTIVITY;
                if (0 != currentTime) {//当前进度不是0，就更新MediaPlayerService的当前进度
                    msgToService.arg1 = currentTime;
                }
                try {
                    mServiceMessenger.send(msgToService);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            //连接成功的时候，
            mPosition = getIntent().getIntExtra("position", 0);
            flag = getIntent().getStringExtra("flag");
            JLog.e(TAG, "传递过来的positon:" + mPosition + " flag:" + flag);
            if (null != mServiceMessenger && null != flag) {
                Message msgToService = Message.obtain();
                msgToService.arg1 = mPosition;
                mList.clear();
                if (flag.equals(Constant.MUSIC_LOCAL)) {
                    mList.addAll(MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.Type.eq(Constant.MUSIC_LOCAL)).list());
                    SPUtil.put(PlayingActivity.this, Constant.CATEGOTY, 1);
                    mTvCategory.setText("本地音乐");
                    mLine.setVisibility(View.VISIBLE);
                } else if (flag.equals(Constant.MAIN_RANDOM)) {
                    mList.addAll(MyApplication.getDaoSession().getMusicBeanDao().loadAll());
                    SPUtil.put(PlayingActivity.this, Constant.CATEGOTY, 2);
                    mTvCategory.setText("随心听");
                    mLine.setVisibility(View.VISIBLE);
                    Collections.shuffle(mList);
                } else if (flag.equals(Constant.MUSIC_KOREA)) {
                    mList.addAll(MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.Type.eq(Constant.MUSIC_KOREA)).list());
                    SPUtil.put(PlayingActivity.this, Constant.CATEGOTY, 3);
                    mTvCategory.setText("韩国");
                    mLine.setVisibility(View.VISIBLE);
                } else if (flag.equals(Constant.MUSIC_ROCK)) {
                    mList.addAll(MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.Type.eq(Constant.MUSIC_ROCK)).list());
                    SPUtil.put(PlayingActivity.this, Constant.CATEGOTY, 4);
                    mTvCategory.setText("摇滚");
                    mLine.setVisibility(View.VISIBLE);
                } else if (flag.equals(Constant.MUSIC_VOLKSLIED)) {
                    mList.addAll(MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.Type.eq(Constant.MUSIC_VOLKSLIED)).list());
                    SPUtil.put(PlayingActivity.this, Constant.CATEGOTY, 5);
                    mTvCategory.setText("民谣");
                    mLine.setVisibility(View.VISIBLE);
                } else if (flag.equals(Constant.MUSIC_SEARCH)) {
                    mList.addAll((List<MusicBean>) getIntent().getSerializableExtra(Constant.SEARCH_ACTIVITY_DATA_KEY));
                    SPUtil.put(PlayingActivity.this, Constant.CATEGOTY, 6);
                    mLine.setVisibility(View.INVISIBLE);
                    mTvCategory.setText("");
                }else if (flag.equals(Constant.MUSIC_Like)){
                    mList.addAll(MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.IsCollected.eq(true)).list());
                    SPUtil.put(PlayingActivity.this, Constant.MUSIC_Like, 7);
                    mLine.setVisibility(View.VISIBLE);
                    mTvCategory.setText("My Love");
                }
                if (null != mList) {
                  /*  for (int i = 0; i < list.size(); i++) {
                        JLog.e(TAG, list.get(i).getSongname() + "--" + list.get(i).getUrl());
                    }*/
                    //更新专辑图片
                    mAlbumFragmentAdapater.addList(mList);
                    mAlbumFragmentAdapater.notifyDataSetChanged();
                    //显示是否收藏了这首歌曲
                    showIsLike();
                    //传递歌曲集合数据
                    Bundle songsData = new Bundle();
                    songsData.putSerializable(Constant.PLAYING_ACTIVITY_DATA_KEY, (Serializable) mList);
                    msgToService.setData(songsData);
                    msgToService.what = Constant.PLAYING_ACTIVITY_INIT;
                    try {
                        mServiceMessenger.send(msgToService);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            JLog.e(TAG, "onServiceDisconnected");
        }
    };


    /**
     * 修改播放模式的UI
     */
    private void updatePlayMode() {
        int playMode = (int) SPUtil.get(MyApplication.mContext, Constant.SP_PLAY_MODE, 0);
        if (0 == playMode) {
            mPlayMode.setImageResource(R.mipmap.order_mode);
        } else if (1 == playMode) {
            mPlayMode.setImageResource(R.mipmap.single_mode);
        }
    }


    private void setTransiton() {
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(700);
        getWindow().setEnterTransition(slide);

        Slide slide1 = new Slide();
        slide1.setDuration(700);
        slide1.setSlideEdge(Gravity.TOP);
        getWindow().setReturnTransition(slide1);
    }

    private void showCategory() {
        int category = (int) SPUtil.get(PlayingActivity.this, Constant.CATEGOTY, 0);
        switch (category) {
            case 1:
                mTvCategory.setText("本地音乐");
                break;
            case 2:
                mTvCategory.setText("随心听");
                break;
            case 3:
                mTvCategory.setText("韩国");
                break;
            case 4:
                mTvCategory.setText("摇滚");
                break;
            case 5:
                mTvCategory.setText("民谣");
                break;
            case 6:
                mLine.setVisibility(View.INVISIBLE);
                mTvCategory.setText("");
                break;
            case 7:
                mLine.setVisibility(View.VISIBLE);
                mTvCategory.setText("My Love");
                break;

        }
    }

    /**
     * 揭露动画
     */
    private Animator createRevealAnimator(boolean reversed, int x, int y) {
        float hypot = (float) Math.hypot(mRootLayout.getHeight(), mRootLayout.getWidth());
        float startRadius = reversed ? hypot : 0;
        float endRadius = reversed ? 0 : hypot;

        Animator animator = ViewAnimationUtils.createCircularReveal(mRootLayout, x, y, startRadius, endRadius);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (reversed) {
            animator.addListener(animatorListener);
        }
        return animator;
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mRootLayout.setVisibility(View.INVISIBLE);
            finish();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    @Override
    public void onBackPressed() {
        if (enterX != 0 && enterY != 0) {
            Animator animator = createRevealAnimator(true, enterX, enterY);
            animator.start();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        JLog.e(TAG, "onDestroy");
        unbindService(mServiceConnection);
        if (null != mAlbumViewPager && null != myOnPageChangeListeger) {
            JLog.e(TAG, "清空ViewPager所有的子View");
            mAlbumViewPager.removeAllViews();
            mAlbumViewPager.removeOnPageChangeListener(myOnPageChangeListeger);
        }
        if (null != mRootLayout) {
            mRootLayout.removeCallbacks(myRunnable);
            mRootLayout.removeAllViews();
        }
        if (null != myHandler){
            myHandler.removeCallbacksAndMessages(null);//移除消息队列中所有的消息和所有的Runnable
            myHandler = null;
        }
        if (null != myRunnable){
            myRunnable = null;
        }
        System.gc();
        super.onDestroy();
//        MyApplication.getRefWatcher().watch(this);
    }

    static class MyRunnable implements Runnable{
        private WeakReference<PlayingActivity> weakActivity;
        public MyRunnable(PlayingActivity activity) {
            weakActivity = new WeakReference<PlayingActivity>(activity);
        }

        @Override
        public void run() {
            PlayingActivity activity = weakActivity.get();
            if (null != activity) {
                activity.enterX = activity.getIntent().getIntExtra("x", 0);
                activity.enterY = activity.getIntent().getIntExtra("y", 0);
                if (0 != activity.enterX && 0 != activity.enterY) {
                    Animator animator = activity.createRevealAnimator(false, activity.enterX, activity.enterY);
                    animator.start();
                }
            }
        }
    }

    static class MyHandler extends Handler{
        private WeakReference<PlayingActivity> weakActivity;
        public MyHandler(PlayingActivity activity) {
            weakActivity = new WeakReference<PlayingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msgFromService) {
            PlayingActivity activity = weakActivity.get();
            if (null == activity) return;
            switch (msgFromService.what) {
                case Constant.MEDIA_PLAYER_SERVICE_PROGRESS://更新进度条
                    activity.currentTime = msgFromService.arg1;
                    activity.duration = msgFromService.arg2;
                    if (0 == activity.duration) break;
                    activity.mSeekBar.setProgress(activity.currentTime * 100 / activity.duration);
                    break;
                case Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING:
                    Bundle bundle = msgFromService.getData();
                    activity.mList.clear();
                    activity.mList.addAll((List<MusicBean>) bundle.getSerializable(Constant.MEDIA_PLAYER_SERVICE_MODEL_PLAYING));
                    if (null != activity.mList && 0 < activity.mList.size()) {
//                        activity.mTvSongName.setText(activity.mList.get(msgFromService.arg1).getSongname());
//                        activity.mTvSinger.setText(activity.mList.get(msgFromService.arg1).getSingername());

                        //更新专辑图片
                        activity.mAlbumFragmentAdapater.addList(activity.mList);
                        activity.mAlbumFragmentAdapater.notifyDataSetChanged();
                        activity.mAlbumViewPager.setCurrentItem(msgFromService.arg1, false);
                    }
                    break;
                case Constant.MEDIA_PLAYER_SERVICE_IS_PLAYING:
                    if (1 == msgFromService.arg1) {//正在播放
                        activity.mBtnPlay.setImageResource(R.mipmap.play);
                    } else {
                        activity.mBtnPlay.setImageResource(R.mipmap.pause);
                    }
                    break;
                case Constant.PLAYING_ACTIVITY_PLAY_MODE://显示播放器的播放模式
                    activity.updatePlayMode();
                    break;
                case Constant.MEDIA_PLAYER_SERVICE_UPDATE_SONG://播放完成自动播放下一首时，更新正在播放UI
                    int positionPlaying = msgFromService.arg1;
                    activity.mAlbumViewPager.setCurrentItem(positionPlaying, false);
                    JLog.e(TAG, "更新正在播放的UI");

            }
            super.handleMessage(msgFromService);
        }
    }
}
