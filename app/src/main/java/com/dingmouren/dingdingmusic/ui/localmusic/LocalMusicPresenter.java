package com.dingmouren.dingdingmusic.ui.localmusic;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.bean.LocalMusicBean;
import com.dingmouren.greendao.LocalMusicBeanDao;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dingmouren on 2017/1/17.
 */

public class LocalMusicPresenter implements LocalMusicConstract.Presenter {
    private static final String TAG = LocalMusicPresenter.class.getName();
    private LocalMusicConstract.View mView;
    private Cursor mCursor;
    List<LocalMusicBean> list = new ArrayList<>();
    public LocalMusicPresenter(LocalMusicConstract.View view) {
        this.mView = view;
    }

    @Override
    public void requestData() {
        mCursor = ((LocalMusicActivity)mView).getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (null != mCursor) {
            list.clear();
            for (int i = 0; i < mCursor.getCount(); i++) {
                LocalMusicBean bean = new LocalMusicBean();
                mCursor.moveToNext();
                bean.setId(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media._ID)));//音乐id
                bean.setTitle(mCursor.getString((mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));//歌曲名称
                bean.setArtist(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));//歌手
                bean.setDuration(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));//歌曲时间
                bean.setSize(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));//歌曲文件大小
                bean.setPath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));//歌曲路径
                if (0 == MyApplication.getDaoSession().getLocalMusicBeanDao().queryBuilder().where(LocalMusicBeanDao.Properties.Id.eq(bean.getId())).count()) {
                    MyApplication.getDaoSession().getLocalMusicBeanDao().insertOrReplace(bean);
                }
                list.add(bean);
            }
            mView.setData(list);
        }
    }

}
