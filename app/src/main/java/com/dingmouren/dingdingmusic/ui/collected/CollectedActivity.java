package com.dingmouren.dingdingmusic.ui.collected;

import android.animation.Animator;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.base.BaseBean;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.ui.localmusic.LocalMusicActivity;
import com.dingmouren.dingdingmusic.ui.localmusic.LocalMusicAdapter;
import com.dingmouren.dingdingmusic.ui.musicplay.PlayingActivity;
import com.dingmouren.greendao.MusicBeanDao;
import com.jiongbull.jlog.JLog;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/2/10.
 */

public class CollectedActivity extends BaseActivity {
    private static final String TAG = CollectedActivity.class.getName();
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.recycler) RecyclerView mRecycler;
    @BindView(R.id.tv_empty)TextView mTvEmpty;
    @BindView(R.id.container) RelativeLayout mRootLayout;
    private CollectedAdapter mAdapter;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_collected;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }

    @Override
    public void initView() {
        mToolbar.setTitle("收藏歌曲");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new CollectedAdapter(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mAdapter);

        //揭露动画
        mRootLayout.post(new Runnable() {
            @Override
            public void run() {
                mRootLayout.setVisibility(View.VISIBLE);
                Animator animator = createRevealAnimator(false,0,0);
                animator.start();

            }
        });
    }

    @Override
    public void initListener() {
        mToolbar.setNavigationOnClickListener((view)-> onBackPressed());//点击箭头返回
        mAdapter.setOnItemClickListener((view, position) -> playSong(position));
    }

    @Override
    public void initData() {
        List<MusicBean> list = MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.IsCollected.eq(true)).list();
        if (null != list && 0 < list.size()){
            mTvEmpty.setVisibility(View.GONE);
        }else {
            mTvEmpty.setVisibility(View.VISIBLE);
        }
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher().watch(this);
    }


    /**
     * 播放被点击的歌曲
     * @param position
     */
    private void playSong(int position){
        Intent intent = new Intent(this, PlayingActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("flag",Constant.MUSIC_Like);
        JLog.e(TAG,"点击收藏的一首音乐");
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    /**
     * 揭露动画
     */
    private Animator createRevealAnimator( boolean exit,int x, int y) {
        float hypot = (float) Math.hypot(mRootLayout.getHeight(),mRootLayout.getWidth());
        float startRadius = exit ? hypot : 0;
        float endRadius = exit ? 0 : hypot;

        Animator animator = ViewAnimationUtils.createCircularReveal(mRootLayout,x,y,startRadius,endRadius);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (exit){
            animator.addListener(animatorListenerExit);
        }
        return animator;
    }

    private Animator.AnimatorListener animatorListenerExit = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            //动画结束时，销毁当前Activity
            mRootLayout.setVisibility(View.INVISIBLE);//在finish()的时候会闪屏的现象，先不可见，再销毁就不会闪屏了
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
        Animator animator = createRevealAnimator(true, mRootLayout.getWidth()/2, mRootLayout.getHeight()/2);
        animator.start();
    }
}
