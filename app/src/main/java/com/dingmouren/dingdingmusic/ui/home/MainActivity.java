package com.dingmouren.dingdingmusic.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.ui.jk.JKActivity;
import com.dingmouren.dingdingmusic.ui.musicplay.PlayingActivity;
import com.dingmouren.dingdingmusic.ui.personal.PersonalCenterActivity;
import com.dingmouren.dingdingmusic.ui.rock.RockActivity;
import com.dingmouren.dingdingmusic.ui.volkslied.VolksliedActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getName();
    @BindView(R.id.fab_user)FloatingActionButton mFabUser;
    @BindView(R.id.fab_music)FloatingActionButton mFabMusic;


  @Override
    public int setLayoutResourceID() {
        return R.layout.activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        startService(new Intent(this,MediaPlayerService.class));//开启MediaPlayerService服务
    }

    @Override
    public void initView() {
        Glide.with(this).load(R.mipmap.playing).asGif().diskCacheStrategy(DiskCacheStrategy.NONE).into(mFabMusic);
        setupWindowAnimation();
    }

    private void setupWindowAnimation() {
       Slide slide = new Slide();
        slide.setSlideEdge(Gravity.BOTTOM);
        slide.setDuration(1000);
        Explode explode = new Explode();
        explode.setDuration(1000);

        getWindow().setReenterTransition(explode);
        getWindow().setExitTransition(explode);

        getWindow().setSharedElementExitTransition(new ChangeImageTransform());
        getWindow().setSharedElementReenterTransition(new ChangeImageTransform());

    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.img_randomn,R.id.img_jk,R.id.img_rock,R.id.img_volkslied,R.id.fab_user,R.id.fab_music})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_randomn:
                playRandom();
                break;
             case R.id.img_jk:
                 turnToJK(view);
                break;
             case R.id.img_rock:
                 turnToRock(view);
                break;
             case R.id.img_volkslied:
                 turnToVolkslied(view);
                break;
            case R.id.fab_user:
                startActivity(new Intent(this, PersonalCenterActivity.class));
                break;
            case R.id.fab_music:
                startActivity(new Intent(this, PlayingActivity.class));
                break;


        }
    }



    /**
     * 随心听
     */
    private void playRandom() {
        Intent intent = new Intent(this, PlayingActivity.class);
        intent.putExtra("flag",Constant.MAIN_RANDOM);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    /**
     * 日韩歌曲
     */
    private void turnToJK(View view) {
        startActivity(new Intent(this, JKActivity.class),ActivityOptions.makeSceneTransitionAnimation(this,view,"share_jk").toBundle());
    }

    /**
     * 摇滚歌曲
     */
    private void turnToRock(View view) {
        startActivity(new Intent(this, RockActivity.class),ActivityOptions.makeSceneTransitionAnimation(this,view,"share_rock").toBundle());
    }

    /**
     * 民谣歌曲
     */
    private void turnToVolkslied(View view){
        startActivity(new Intent(this, VolksliedActivity.class),ActivityOptions.makeSceneTransitionAnimation(this,view,"share_volkslied").toBundle());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,MediaPlayerService.class));
    }

}
