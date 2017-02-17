package com.dingmouren.dingdingmusic.ui.personal;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dingmouren.dingdingmusic.Constant;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.utils.SPUtil;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/2/12.
 */

public class EditActivity extends BaseActivity {
    @BindView(R.id.edit_name) EditText mEdit;
    @BindView(R.id.btn_confim)Button mBtnConfirm;
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_edit;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }


    @Override
    public void initView() {
        mBtnConfirm.setOnClickListener(view -> changeName());
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }


    @Override
    public void initData() {

    }


    private void changeName() {
        String newName = mEdit.getText().toString().trim();
        if (!TextUtils.isEmpty(newName)){
            SPUtil.put(this, Constant.USER_NAME,newName);
            Toast.makeText(this,"昵称修改成功",Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this,"昵称不能为空",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher().watch(this);
    }
}
