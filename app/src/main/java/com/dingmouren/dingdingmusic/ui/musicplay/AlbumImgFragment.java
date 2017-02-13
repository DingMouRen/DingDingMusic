package com.dingmouren.dingdingmusic.ui.musicplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseFragment;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/2/2.
 */

public class AlbumImgFragment extends BaseFragment {
    private static final String ALBUM_URL = "cover_url";
    @BindView(R.id.img_album) ImageView mImgAlbum;
    @BindView(R.id.tv_lyric)
    TextView mTvLyric;
    private String mAlbumUrl;
    String str = "[ti:海阔天空 (Edited Version)]\n" +
            "[ar:BEYOND]\n" +
            "[al:Words & Music Final Live With 家驹]\n" +
            "[by:]\n" +
            "[offset:0]\n" +
            "[00:00.92]海阔天空  - BEYOND\n" +
            "[00:02.27]词：黄家驹\n" +
            "[00:03.32]曲：黄家驹\n" +
            "[00:04.30]\n" +
            "[00:19.17]今天我 寒夜里看雪飘过\n" +
            "[00:25.75]怀着冷却了的心窝飘远方\n" +
            "[00:30.77]\n" +
            "[00:31.60]风雨里追赶 雾里分不清影踪\n" +
            "[00:37.82]天空海阔你与我 可会变\n" +
            "[00:43.27]\n" +
            "[00:44.14]多少次迎着冷眼与嘲笑\n" +
            "[00:50.55]从没有放弃过心中的理想\n" +
            "[00:56.02]\n" +
            "[00:56.67]一刹那恍惚 若有所失的感觉\n" +
            "[01:02.65]不知不觉已变淡 心里爱\n" +
            "[01:08.64]\n" +
            "[01:09.66]原谅我这一生不羁放纵爱自由\n" +
            "[01:15.56]\n" +
            "[01:16.40]也会怕有一天会跌倒\n" +
            "[01:22.72]背弃了理想谁人都可以\n" +
            "[01:27.84]\n" +
            "[01:28.51]哪会怕有一天只你共我\n" +
            "[01:33.89]\n" +
            "[01:43.41]今天我 寒夜里看雪飘过\n" +
            "[01:49.76]怀着冷却了的心窝飘远方\n" +
            "[01:54.86]\n" +
            "[01:55.60]风雨里追赶 雾里分不清影踪\n" +
            "[02:01.92]天空海阔你与我 可会变\n" +
            "[02:06.61]\n" +
            "[02:08.70]原谅我这一生不羁放纵爱自由\n" +
            "[02:14.86]\n" +
            "[02:15.55]也会怕有一天会跌倒\n" +
            "[02:21.30]\n" +
            "[02:21.83]背弃了理想谁人都可以\n" +
            "[02:27.17]\n" +
            "[02:28.08]哪会怕有一天只你共我\n" +
            "[02:33.08]\n" +
            "[02:38.06]仍然自由自我\n" +
            "[02:40.57]\n" +
            "[02:41.42]永远高唱我歌\n" +
            "[02:44.42]走遍千里 原谅我这一生不羁放纵爱自由\n" +
            "[02:55.20]\n" +
            "[02:56.14]也会怕有一天会跌倒\n" +
            "[03:02.26]背弃了理想 谁人都可以\n" +
            "[03:07.48]\n" +
            "[03:08.67]哪会怕有一天只你共我\n" +
            "[03:13.58]\n" +
            "[03:14.51]原谅我这一生不羁放纵爱自由\n" +
            "[03:21.27]也会怕有一天会跌倒\n" +
            "[03:26.00]\n" +
            "[03:27.38]背弃了理想谁人都可以\n" +
            "[03:31.94]\n" +
            "[03:33.61]哪会怕有一天只你共我";
    public static AlbumImgFragment newInstance(String url){
        AlbumImgFragment fragment = new AlbumImgFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_URL,url);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public int setLayoutResourceID() {
        return R.layout.fragment_cover;
    }

    @Override
    public void initView() {
        if (null != getArguments()){//非空判断
            mAlbumUrl = getArguments().getString(ALBUM_URL);
            mTvLyric.setText(str);
        }
    }

    @Override
    public void initData() {
        if (null != mAlbumUrl){
            Glide.with(MyApplication.mContext).load(mAlbumUrl).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.NONE)/*.placeholder(R.mipmap.native_1)*/.into(mImgAlbum);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher().watch(this);
    }
}
