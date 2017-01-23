package com.dingmouren.dingdingmusic;

import android.app.Application;
import android.content.Context;

import com.dingmouren.greendao.DaoMaster;
import com.dingmouren.greendao.DaoSession;
import com.jiongbull.jlog.JLog;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.Database;

/**
 * Created by dingmouren on 2017/1/18.
 */

public class MyApplication extends Application {
    public static DaoSession mDaoSession;
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this.getApplicationContext();
        initGreenDao();//初始化数据库
        JLog.init(this).setDebug(BuildConfig.DEBUG);
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"music_db",null);
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession(){
        return mDaoSession;
    }
}
