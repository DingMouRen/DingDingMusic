package com.dingmouren.dingdingmusic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.service.MediaPlayerService;
import com.dingmouren.dingdingmusic.ui.musicplay.PlayingActivity;
import com.dingmouren.dingdingmusic.ui.personal.PersonalCenterActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
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

    @OnClick({R.id.img_suixinting,R.id.img_recommend,R.id.img_rock,R.id.img_ballad,R.id.fab_user,R.id.fab_music})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_suixinting:
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,MediaPlayerService.class));
    }
}
