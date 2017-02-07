package com.dingmouren.dingdingmusic.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by dingmouren on 2017/1/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        init(savedInstanceState);//初始化view之前，进行的操作
        super.onCreate(savedInstanceState);
        setContentView(setLayoutResourceID());
        ButterKnife.bind(this);
        initView();
        initListener();
        initData();
    }
    public void init(Bundle savedInstanceState){}//可实现，也可以不实现
    public abstract int setLayoutResourceID();
    public abstract void initView();
    public  void initListener(){}
    public abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
