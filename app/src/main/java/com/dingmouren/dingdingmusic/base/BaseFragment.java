package com.dingmouren.dingdingmusic.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by dingmouren on 2017/1/17.
 */

public abstract class BaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(setLayoutResourceID(),container,false);
        ButterKnife.bind(this,rootView);
        init(savedInstanceState);//创建视图前可以进行一些初始化
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
        initData();
    }
    public void init(Bundle savedInstanceState){}//可实现，也可以不实现
    public abstract int setLayoutResourceID();
    public abstract void initView();
    public  void initListener(){}
    public abstract void initData();
}
