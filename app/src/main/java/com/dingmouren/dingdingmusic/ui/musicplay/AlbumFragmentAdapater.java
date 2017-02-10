package com.dingmouren.dingdingmusic.ui.musicplay;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dingmouren.dingdingmusic.bean.MusicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingmouren on 2017/2/2.
 */

public class AlbumFragmentAdapater extends FragmentStatePagerAdapter {
    private List<MusicBean> mList = new ArrayList<>();
    public AlbumFragmentAdapater(FragmentManager fm) {
        super(fm);
    }

    public void addList(List<MusicBean> list){
        mList.clear();
        this.mList.addAll(list);
    }
    @Override
    public Fragment getItem(int position) {
        String url = null == mList.get(position).getAlbumpic_big() ? "" :mList.get(position).getAlbumpic_big();
        return AlbumImgFragment.newInstance(url);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 :mList.size() ;
    }
}
