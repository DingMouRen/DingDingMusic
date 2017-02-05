package com.dingmouren.dingdingmusic.ui.localmusic;

import android.database.Cursor;
import android.provider.MediaStore;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.greendao.MusicBeanDao;
import com.jiongbull.jlog.JLog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dingmouren on 2017/1/17.
 */

public class LocalMusicPresenter implements LocalMusicConstract.Presenter {
    private static final String TAG = LocalMusicPresenter.class.getName();
    private LocalMusicConstract.View mView;
    private Cursor mCursor;
    List<MusicBean> list = new ArrayList<>();
    public LocalMusicPresenter(LocalMusicConstract.View view) {
        this.mView = view;
    }

    @Override
    public void requestData() {
        mCursor = ((LocalMusicActivity)mView).getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (  null != mCursor) {
            list.clear();
            for (int i = 0; i < mCursor.getCount(); i++) {
                MusicBean bean = new MusicBean();
                mCursor.moveToNext();
                bean.setSongid((int) mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media._ID)));//音乐id
                bean.setSongname(mCursor.getString((mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));//歌曲名称
                bean.setSingername(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));//歌手
                bean.setUrl(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));//歌曲路径
                bean.setType(Integer.parseInt(Constant.MUSIC_LOCAL));
                JLog.e(TAG,"本地音乐路径："+bean.getUrl());
                if (0 == MyApplication.getDaoSession().getMusicBeanDao().queryBuilder().where(MusicBeanDao.Properties.Songid.eq(bean.getSongid())).count()) {
                    JLog.e(TAG,""+i);
                    MyApplication.getDaoSession().getMusicBeanDao().insert(bean);
                }
                list.add(bean);
            }
            mView.setData(list);
        }
    }

}
