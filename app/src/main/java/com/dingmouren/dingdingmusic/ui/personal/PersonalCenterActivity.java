package com.dingmouren.dingdingmusic.ui.personal;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.ui.about.AboutActivity;
import com.dingmouren.dingdingmusic.ui.collected.CollectedActivity;
import com.dingmouren.dingdingmusic.ui.home.MainActivity;
import com.dingmouren.dingdingmusic.ui.localmusic.LocalMusicActivity;
import com.dingmouren.dingdingmusic.utils.MyGlideImageLoader;
import com.dingmouren.dingdingmusic.utils.SPUtil;
import com.dingmouren.greendao.MusicBeanDao;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by dingmouren on 2017/1/17.
 */

public class PersonalCenterActivity extends BaseActivity {
    private static final String TAG = PersonalCenterActivity.class.getName();
    @BindView(R.id.img_header) CircleImageView mImgHeader;
    @BindView(R.id.tv_username)TextView mUserName;
    @BindView(R.id.tv_local_music) TextView mLocalMusicCount;
    @BindView(R.id.tv_like) TextView mLikeMusic;
    @BindView(R.id.img_setting)ImageView mSetting;
    @BindView(R.id.container) LinearLayout mRootLayout;
    private Cursor mCursor;
    private long mCountLike;
    private int enterX;//传递过来的x坐标，是点击View的中心点的x坐标，揭露动画
    private int enterY;//传递过来的y坐标，是点击View的中心点的y坐标，揭露动画
    private GalleryConfig mGalleryConfig;//图片选择器的配置
    private List<String> mNavHeaderImgPaths = new ArrayList<>();//记录已选的图片
    private String mName;
    private Observable observable;
    private Subscriber subscriber;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_personal;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        observable = Observable.just(1).delay(1, TimeUnit.SECONDS);
        subscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                finish();
            }
        };

    }

    @Override
    public void initView() {
        mCursor  = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (null != mCursor)mLocalMusicCount.setText("本地歌曲("+mCursor.getCount()+"首)");

        mCountLike  = MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.IsCollected.eq(true)).count();
        if (0 != mCountLike){
            mLikeMusic.setText("收藏歌曲("+mCountLike+"首)");
        }

        //揭露动画
        mRootLayout.post(new Runnable() {
            @Override
            public void run() {
                mRootLayout.setVisibility(View.VISIBLE);
                enterX = getIntent().getIntExtra("x",0);
                enterY = getIntent().getIntExtra("y",0);
                if (0 != enterX && 0 != enterY){
                    Animator animator = createRevealAnimator(false,enterX,enterY);
                    animator.start();
                }
            }
        });


    }

    @Override
    public void initListener() {
        mImgHeader.setOnClickListener((view -> changeHeader()));
        mUserName.setOnClickListener(view -> changeName());
    }


    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        mName = (String) SPUtil.get(this,Constant.USER_NAME,"Your name");
        mUserName.setText(mName);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (subscriber.isUnsubscribed()){
            subscriber.unsubscribe();
        }
        super.onDestroy();
    }

    @OnClick({R.id.card_local_music,R.id.card_like,R.id.img_setting,R.id.card_about})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.card_local_music:
                Intent intent = new Intent(PersonalCenterActivity.this,LocalMusicActivity.class);
                startActivity(intent);
//                new Handler().postDelayed(()-> finish(),1000);
                delayFinish();
                break;
            case R.id.card_like:
                Intent intent1 = new Intent(PersonalCenterActivity.this,CollectedActivity.class);
                startActivity(intent1);
                delayFinish();
                break;
            case R.id.img_setting:
                break;
            case R.id.card_about:
                Intent intent2 = new Intent(PersonalCenterActivity.this,AboutActivity.class);
                startActivity(intent2);
                delayFinish();
                break;
        }
    }


    private void changeName() {
        startActivity(new Intent(this,EditActivity.class));
    }

    private void delayFinish(){
        observable.subscribe(subscriber);
    }

    /**
     * 初始化用户头像
     */
    private void initHeader(){
        if (!TextUtils.isEmpty((String)SPUtil.get(this,Constant.HEADER_IMG_PATH,"")))
            Glide.with(this).load((String)SPUtil.get(this, Constant.HEADER_IMG_PATH, "")).into(mImgHeader);
    }

    /**
     * 揭露动画
     */
    private Animator createRevealAnimator( boolean reversed,int x, int y) {
        float hypot = (float) Math.hypot(mRootLayout.getHeight(),mRootLayout.getWidth());
        float startRadius = reversed ? hypot : 0;
        float endRadius = reversed ? 0 : hypot;

        Animator animator = ViewAnimationUtils.createCircularReveal(mRootLayout,x,y,startRadius,endRadius);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (reversed){
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
        }else {
            super.onBackPressed();
        }
    }

    //更换头像
    private void changeHeader(){
        initGalleryConfig();//初始化图片选择器的配置参数
        initPermissions();//授权管理
    }
    private void initGalleryConfig() {
        mGalleryConfig = new GalleryConfig.Builder()
                .imageLoader(new MyGlideImageLoader())
                .iHandlerCallBack(imgTakeListener)
                .pathList(mNavHeaderImgPaths)
                .multiSelect(false)//是否多选
                .crop(true)//开启快捷裁剪功能
                .isShowCamera(true)//显示相机按钮，默认是false
                .filePath("/EasyMvp")//图片存放路径
                .build();
    }

    //更换头像时的授权管理
    private void initPermissions() {
        if (ContextCompat.checkSelfPermission(PersonalCenterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //需要授权
            if (ActivityCompat.shouldShowRequestPermissionRationale(PersonalCenterActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                //拒绝过了
                Snackbar.make(mRootLayout,"请在 设置-应用管理 中开启此应用的存储权限",Snackbar.LENGTH_SHORT).show();
            }else {
                //进行授权
                ActivityCompat.requestPermissions(PersonalCenterActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},8);//这个8做什么的不知道，以后再研究
            }
        } else{
            //不需要授权
            GalleryPick.getInstance().setGalleryConfig(mGalleryConfig).open(PersonalCenterActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 8){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //同意授权
                GalleryPick.getInstance().setGalleryConfig(mGalleryConfig).open(PersonalCenterActivity.this);
            }else {
                //拒绝授权
            }
        }
    }

    //图片选择器的监听接口
    IHandlerCallBack imgTakeListener = new IHandlerCallBack() {
        @Override
        public void onStart() {

        }
        @Override
        public void onSuccess(List<String> photoList) {
            SPUtil.put(PersonalCenterActivity.this, Constant.HEADER_IMG_PATH,photoList.get(0));//记录选择的头像图片的路径
            Glide.with(PersonalCenterActivity.this).load(photoList.get(0)).into(mImgHeader);
        }
        @Override
        public void onCancel() {
        }

        @Override
        public void onFinish() {
        }
        @Override
        public void onError() {
        }
    };
}
