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
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.utils.SPUtil;
import com.dingmouren.greendao.MusicBeanDao;
import com.jiongbull.jlog.JLog;

import java.io.Serializable;
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

    @BindView(R.id.seek_bar)
    SeekBar mSeekBar;
    @BindView(R.id.tv_song_name)
    TextView mTvSongName;
    @BindView(R.id.tv_singer)
    TextView mTvSinger;
    @BindView(R.id.album_viewpager)
    ViewPager mAlbumViewPager;
    @BindView(R.id.btn_playorpause)
    ImageButton mBtnPlay;
    @BindView(R.id.btn_single)
    ImageButton mPlayMode;
    @BindView(R.id.img_bg)
    ImageView mImgBg;
    @BindView(R.id.contanier_play_activity)
    PercentRelativeLayout mRootLayout;
    @BindView(R.id.btn_like)
    ImageButton mBtnLike;
    @BindView(R.id.btn_share)
    ImageButton mBtnShare;
    @BindView(R.id.tv_category)
    TextView mTvCategory;
    @BindView(R.id.line_playing)
    View mLine;

    public Messenger mServiceMessenger;//来自服务端的Messenger
    private boolean isConnected = false;//标记是否连接上了服务端
    private float mPercent;//进度的百分比
    private AlbumFragmentAdapater mAlbumFragmentAdapater;//专辑图片的适配器
    public int mPosition;//传递过来的的歌曲的位置
    public int mPositionPlaying;//正在播放的歌曲的位置
    public String flag;//歌曲集合的类型
    public int currentTime;//实时当前进度
    public int duration;//歌曲的总进度
    private float mPositionOffset;//viewpager滑动的百分比
    private int mState;//viewpager的滑动状态
    private List<MusicBean> mList = new ArrayList<>();
    private int enterX;//传递过来的x坐标，是点击View的中心点的x坐标，揭露动画
    private int enterY;//传递过来的y坐标，是点击View的中心点的y坐标，揭露动画
    private String shareSongName;
    private String shareSingerName;
    private String shareUrl;
    private String shareContent;
    private String songNamePlaying;
    private String singerNamePlaying;
    private MusicBean beanToCollected;

    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_musicplayer;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setTransiton();
        bindService(new Intent(getApplicationContext(), MediaPlayerService.class), mServiceConnection, BIND_AUTO_CREATE);
    }


    @Override
    public void initView() {
        updatePlayMode();
        mSeekBar.setMax(100);

        mAlbumFragmentAdapater = new AlbumFragmentAdapater(getSupportFragmentManager());
        mAlbumViewPager.setAdapter(mAlbumFragmentAdapater);
        mAlbumViewPager.setOffscreenPageLimit(6);


        //揭露动画
        mRootLayout.post(new Runnable() {
            @Override
            public void run() {
                enterX = getIntent().getIntExtra("x", 0);
                enterY = getIntent().getIntExtra("y", 0);
                if (0 != enterX && 0 != enterY) {
                    Animator animator = createRevealAnimator(false, enterX, enterY);
                    animator.start();
                }
            }
        });

        //左上角显示类别
        showCategory();

    }


    @Override
    public void initListener() {
        //进度条的监听
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {//判断来自用户的滑动
                    mPercent = (float) progress * 100 / (float) mSeekBar.getMax();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //用户松开SeekBar，通知MediaPlayerService更新播放器的进度，解决拖动过程中卡顿的问题
                Message msgToMediaPlayerService = Message.obtain();
                msgToMediaPlayerService.what = Constant.PLAYING_ACTIVITY_CUSTOM_PROGRESS;
                msgToMediaPlayerService.arg1 = (int) mPercent;
                try {
                    mServiceMessenger.send(msgToMediaPlayerService);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //滑动播放上/下一首歌曲的监听,实际上传递过去的是歌曲的position
        mAlbumViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {//arg1:当前页面的位置，也就是position;     arg2:当前页面偏移的百分比;     arg3当前页面偏移的像素位置
//                JLog.e(TAG,"onPageScrolled--postion:" + position +" positionOffset:"+positionOffset+" positionOffsetPixels:"+positionOffsetPixels);
                mPositionOffset = positionOffset;
                if (position == 0) {//解决第一次进入的时候没有显示模糊效果
                    Glide.with(MyApplication.mContext)//底部的模糊效果
                            .load(mList.get(position).getAlbumpic_big())
                            .bitmapTransform(new BlurTransformation(PlayingActivity.this, 99))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .crossFade()
                            .into(mImgBg);
                    //首次进入获取正在播放歌曲的信息
                    songNamePlaying = mList.get(0).getSongname();
                    singerNamePlaying = mList.get(0).getSingername();
                    showIsLike();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {//state==1表示正在滑动，state==2表示滑动完毕，state==0表示没有动作
                JLog.e(TAG, "onPageScrollStateChanged--state:" + state);
                mState = state;
            }

            @Override
            public void onPageSelected(int position) {
                mPositionPlaying =  position;
                songNamePlaying = mList.get(mPositionPlaying).getSongname();
                singerNamePlaying = mList.get(mPositionPlaying).getSingername();
                JLog.e(TAG,songNamePlaying+"--------"+ singerNamePlaying);
                showIsLike();
                if (2 == mState && 0 < mPositionOffset) {
                    Message msgToService = Message.obtain();
                    msgToService.arg1 = position;
                    msgToService.what = Constant.PLAYING_ACTIVITY_PLAYING_POSITION;
                    if (null != mServiceMessenger) {
                        try {
                            mServiceMessenger.send(msgToService);
                            JLog.e(TAG, "onPageSelected--postion:" + position + " 发送播放上/下一首歌曲的消息");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }
                if (position < mList.size()) {
                    Glide.with(PlayingActivity.this)//底部的模糊效果
                            .load(mList.get(position).getAlbumpic_big() == null ? R.mipmap.native_1 : mList.get(position).getAlbumpic_big())
                            .bitmapTransform(new BlurTransformation(PlayingActivity.this, 99))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .crossFade()
                            .into(mImgBg);
                }

            }

        });

        //分享功能
        mBtnShare.setOnClickListener((view -> share()));
        mBtnLike.setOnClickListener((view -> collect()));

    }


    private void showIsLike() {
        JLog.e(TAG,"showLike:" + songNamePlaying +"--"+ singerNamePlaying);
        if (null == songNamePlaying || null == singerNamePlaying || null == mList) return;
        List<MusicBean> list = MyApplication.getDaoSession().getMusicBeanDao().queryBuilder()
                .where(MusicBeanDao.Properties.Singername.eq(singerNamePlaying),
                        MusicBeanDao.Properties.Songname.eq(songNamePlaying)).list();
        if (null != list && 0 <list.size()){
            boolean isCollected = list.get(0).getIsCollected();
            if (isCollected){
                Glide.with(this).load(R.mipmap.collected).crossFade().into(mBtnLike);
            }else {
                Glide.with(this).load(R.mipmap.no_collected).crossFade().into(mBtnLike);
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
                Glide.with(this).load(R.mipmap.collected).crossFade().into(mBtnLike);
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
                Glide.with(this).load(R.mipmap.no_collected).crossFade().into(mBtnLike);
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
            isConnected = true;
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
            isConnected = false;
        }
    };

    Messenger mPlaygingClientMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgFromService) {
            switch (msgFromService.what) {
                case Constant.MEDIA_PLAYER_SERVICE_PROGRESS://更新进度条
                    currentTime = msgFromService.arg1;
                    duration = msgFromService.arg2;
                    if (0 == duration) break;
                    mSeekBar.setProgress(currentTime * 100 / duration);
                    break;
                case Constant.MEDIA_PLAYER_SERVICE_SONG_PLAYING:
                    Bundle bundle = msgFromService.getData();
                    mList.clear();
                    mList.addAll((List<MusicBean>) bundle.getSerializable(Constant.MEDIA_PLAYER_SERVICE_MODEL_PLAYING));
                    if (null != mList && 0 < mList.size()) {
                        mTvSongName.setText(mList.get(msgFromService.arg1).getSongname());
                        mTvSinger.setText(mList.get(msgFromService.arg1).getSingername());

                        //更新专辑图片
                        mAlbumFragmentAdapater.addList(mList);
                        mAlbumFragmentAdapater.notifyDataSetChanged();
                        mAlbumViewPager.setCurrentItem(msgFromService.arg1, false);
                    }
                    break;
                case Constant.MEDIA_PLAYER_SERVICE_IS_PLAYING:
                    if (1 == msgFromService.arg1) {//正在播放
                        mBtnPlay.setImageResource(R.mipmap.play);
                    } else {
                        mBtnPlay.setImageResource(R.mipmap.pause);
                    }
                    break;
                case Constant.PLAYING_ACTIVITY_PLAY_MODE://显示播放器的播放模式
                    updatePlayMode();
                    break;
                case Constant.MEDIA_PLAYER_SERVICE_UPDATE_SONG://播放完成自动播放下一首时，更新正在播放UI
                    int positionPlaying = msgFromService.arg1;
                    mAlbumViewPager.setCurrentItem(positionPlaying, false);
                    JLog.e(TAG, "更新正在播放的UI");

            }
            super.handleMessage(msgFromService);
        }
    });

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
        slide1.setDuration(900);
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
        unbindService(mServiceConnection);
        JLog.e(TAG, "onDestroy");
        super.onDestroy();
    }
}
