package com.dingmouren.dingdingmusic.ui.collected;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.base.BaseBean;
import com.dingmouren.dingdingmusic.ui.localmusic.LocalMusicActivity;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/2/10.
 */

public class CollectedActivity extends BaseActivity {
    private static final String TAG = CollectedActivity.class.getName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_collected;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}
