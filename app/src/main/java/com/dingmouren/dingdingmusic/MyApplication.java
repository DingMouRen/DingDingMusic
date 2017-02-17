package com.dingmouren.dingdingmusic;

import android.app.Application;
import android.content.Context;

import com.dingmouren.greendao.DaoMaster;
import com.dingmouren.greendao.DaoSession;
import com.jiongbull.jlog.JLog;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.Database;

/**
 * Created by dingmouren on 2017/1/18.
 */

public class MyApplication extends Application {
    public static DaoSession mDaoSession;
    public static Context mContext;
    private static RefWatcher mRefWatcher;
    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this.getApplicationContext();
        initGreenDao();//初始化数据库
        JLog.init(this).setDebug(BuildConfig.DEBUG);
//        mRefWatcher = LeakCanary.install(this);//使用RefWathcer检测内存泄漏
        CrashReport.initCrashReport(getApplicationContext());//异常统计
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"music_db",null);
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession(){
        return mDaoSession;
    }

    public static RefWatcher getRefWatcher(){
        return mRefWatcher;
    }
}
