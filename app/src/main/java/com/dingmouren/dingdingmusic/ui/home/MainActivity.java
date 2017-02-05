package com.dingmouren.dingdingmusic.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.api.ApiManager;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.ui.musicplay.PlayingActivity;
import com.dingmouren.dingdingmusic.ui.personal.PersonalCenterActivity;
import com.dingmouren.dingdingmusic.utils.RequestMusicUtil;
import com.jiongbull.jlog.JLog;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getName();
    @BindView(R.id.fab_user)FloatingActionButton mFabUser;

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
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.img_randomn,R.id.img_recommend,R.id.img_rock,R.id.img_ballad,R.id.fab_user,R.id.fab_music})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_randomn:
                playRandom();
                break;
             case R.id.img_recommend:
                break;
             case R.id.img_rock:
                break;
             case R.id.img_ballad:
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
        intent.putExtra("flag",Constant.MUSIC_HOT);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,MediaPlayerService.class));
    }

}
