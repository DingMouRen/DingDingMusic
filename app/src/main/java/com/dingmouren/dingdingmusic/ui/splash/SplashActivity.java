package com.dingmouren.dingdingmusic.ui.splash;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.transition.Fade;
import android.widget.TextView;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.ui.home.MainActivity;
import com.dingmouren.dingdingmusic.utils.RequestMusicUtil;
import com.dingmouren.greendao.MusicBeanDao;
import com.jiongbull.jlog.JLog;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;

/**
 3;//欧美
 5;//内地
 6;//港台
 16;//韩国
 17;//日本
 18;//民谣
 19;//摇滚
 23;//销量
 26;//热歌
 */

public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getName();
    @BindView(R.id.author)
    TextView mTvAuthor;
    private RequestMusicUtil mRequestMusicUtil;
    private String[] topics = new String[]{Constant.MUSIC_HONGKANG,Constant.MUSIC_HOT,Constant.MUSIC_INLAND,Constant.MUSIC_JAPAN,Constant.MUSIC_KOREA,Constant.MUSIC_ROCK,Constant.MUSIC_SALES,Constant.MUSIC_VOLKSLIED,Constant.MUSIC_WEST};
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        mTvAuthor.setText("@钉某人");
        mTvAuthor.setTextColor(Color.WHITE);
        new Handler().postDelayed(()->{
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        },2500);
    }

    @Override
    public void initData() {

        long count = MyApplication.getDaoSession().getMusicBeanDao() .queryBuilder()
                .whereOr(MusicBeanDao.Properties.Type.eq(Integer.valueOf(Constant.MUSIC_WEST)),
                        MusicBeanDao.Properties.Type.eq(Integer.valueOf(Constant.MUSIC_INLAND)),
                        MusicBeanDao.Properties.Type.eq(Integer.valueOf(Constant.MUSIC_HONGKANG)),
                        MusicBeanDao.Properties.Type.eq(Integer.valueOf(Constant.MUSIC_KOREA)),
                        MusicBeanDao.Properties.Type.eq(Integer.valueOf(Constant.MUSIC_JAPAN)),
                        MusicBeanDao.Properties.Type.eq(Integer.valueOf(Constant.MUSIC_VOLKSLIED)),
                        MusicBeanDao.Properties.Type.eq(Integer.valueOf(Constant.MUSIC_ROCK)),
                        MusicBeanDao.Properties.Type.eq(Integer.valueOf(Constant.MUSIC_SALES)),
                        MusicBeanDao.Properties.Type.eq(Integer.valueOf(Constant.MUSIC_HOT))).count();

        JLog.e(TAG,"count:" + count);

        //请求歌曲数据
        if (100 > count) {//当数据库中的网络歌曲小于100首时，去请求新数据
            mRequestMusicUtil = new RequestMusicUtil();
            Observable.interval(1000, 1500, TimeUnit.MILLISECONDS).limit(9)
                    .subscribe(aLong -> mRequestMusicUtil.requestMusic(topics[aLong.intValue()]));
        }
    }
}
