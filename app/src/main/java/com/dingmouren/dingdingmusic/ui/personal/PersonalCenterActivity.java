package com.dingmouren.dingdingmusic.ui.personal;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.ui.localmusic.LocalMusicActivity;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dingmouren on 2017/1/17.
 */

public class PersonalCenterActivity extends BaseActivity {
    @BindView(R.id.img_header) CircleImageView mImgHeader;
    @BindView(R.id.tv_username)TextView mUserName;
    @BindView(R.id.tv_local_music) TextView mLocalMusicCount;
    @BindView(R.id.tv_like_music) TextView mLikeMusicCount;
    @BindView(R.id.img_setting)ImageView mSetting;
    private Cursor mCursor;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_personal;
    }

    @Override
    public void initView() {
        mCursor  = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (null != mCursor)mLocalMusicCount.setText("本地歌曲("+mCursor.getCount()+"首)");

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.card_local_music,R.id.card_like_music,R.id.img_setting})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.card_local_music:
                startActivity(new Intent(this, LocalMusicActivity.class));
                finish();
                break;
            case R.id.card_like_music:
                break;
            case R.id.img_setting:
                break;
        }
    }
}
