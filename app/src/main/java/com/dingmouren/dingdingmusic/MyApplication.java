package com.dingmouren.dingdingmusic;

import android.app.Application;
import android.content.Context;

import com.dingmouren.greendao.DaoMaster;
import com.dingmouren.greendao.DaoSession;
import com.jiongbull.jlog.JLog;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

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
        initLeakCanary();//初始化内存泄漏检测库
        CrashReport.initCrashReport(getApplicationContext());//异常统计
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)){
            JLog.e("一定是发生了什么？");
            return;
        }
        LeakCanary.install(this);
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
