package com.dingmouren.dingdingmusic.ui.about;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.ui.collected.CollectedActivity;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dingmouren on 2017/2/14.
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.toolbar)  Toolbar mToolbar;
    @BindView(R.id.container)  LinearLayout mRootLayout;
    private MyRunnable myRunnable;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_about;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        myRunnable = new MyRunnable(this);
    }

    @Override
    public void initView() {
        mToolbar.setTitle("关于");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener((view -> onBackPressed()));

        //揭露动画
        mRootLayout.post(myRunnable);
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.img_tip)
    public void onClick(View view){
        Toast.makeText(this,"打赏",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (null != mRootLayout){
            mRootLayout.removeAllViews();
        }
        super.onDestroy();
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
    static class MyRunnable implements Runnable{
        private WeakReference<AboutActivity> weakActivity;
        public MyRunnable(AboutActivity activity) {
            weakActivity = new WeakReference<AboutActivity>(activity);
        }

        @Override
        public void run() {
            AboutActivity activity = weakActivity.get();
            if (null != activity) {
                activity.mRootLayout.setVisibility(View.VISIBLE);
                Animator animator = activity.createRevealAnimator(false, 0, 0);
                animator.start();
            }
        }
    }
}
