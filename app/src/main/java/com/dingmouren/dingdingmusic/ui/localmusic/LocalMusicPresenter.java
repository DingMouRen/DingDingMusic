package com.dingmouren.dingdingmusic.ui.localmusic;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.greendao.MusicBeanDao;
import com.jiongbull.jlog.JLog;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


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
            Observable.just(mCursor).flatMap(new Func1<Cursor, Observable<?>>() {
                @Override
                public Observable<List<MusicBean>> call(Cursor cursor) {
                    for (int i = 0; i < mCursor.getCount(); i++) {
                        MusicBean bean = new MusicBean();
                        mCursor.moveToNext();
                        bean.setSongid((int) mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media._ID)));//音乐id
                        bean.setSongname(mCursor.getString((mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));//歌曲名称
                        bean.setSingername(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));//歌手
                        bean.setUrl(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));//歌曲路径
                        bean.setType(Integer.parseInt(Constant.MUSIC_LOCAL));
                        MyApplication.getDaoSession().getMusicBeanDao().insertOrReplace(bean);
                        list.add(bean);
                    }
                    return Observable.just(list);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Object>() {
                        @Override
                        public void onCompleted() {
                            Toast.makeText(MyApplication.mContext,"扫描到"+ mCursor.getCount()+"首本地歌曲",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            JLog.e(TAG,e.getMessage());
                        }

                        @Override
                        public void onNext(Object o) {
                            mView.setData((List<MusicBean>)o);
                        }
                    });
          /*  for (int i = 0; i < mCursor.getCount(); i++) {
                MusicBean bean = new MusicBean();
                mCursor.moveToNext();
                bean.setSongid((int) mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media._ID)));//音乐id
                bean.setSongname(mCursor.getString((mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));//歌曲名称
                bean.setSingername(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));//歌手
                bean.setUrl(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));//歌曲路径
                bean.setType(Integer.parseInt(Constant.MUSIC_LOCAL));
                JLog.e(TAG,"本地音乐路径："+bean.getUrl());
                MyApplication.getDaoSession().getMusicBeanDao().insertOrReplace(bean);
                list.add(bean);
            }
            mView.setData(list);*/
        }
    }

}
