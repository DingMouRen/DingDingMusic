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
        this.mList.addAll(list);
    }
    @Override
    public Fragment getItem(int position) {
        String url = null == mList.get(position).getAlbumpic_big() ? "http://7xi8d6.com1.z0.glb.clouddn.com/16123958_1630476787257847_7576387494862651392_n.jpg" :mList.get(position).getAlbumpic_big();
        return AlbumImgFragment.newInstance(url);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 :mList.size() ;
    }
}
