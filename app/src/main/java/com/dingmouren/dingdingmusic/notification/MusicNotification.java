package com.dingmouren.dingdingmusic.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.bean.MusicBean;

/**
 * Created by dingmouren on 2017/1/20.
 * 音乐播放的自定义通知栏
 */

public class MusicNotification extends Notification {
    private static final String TAG = MusicNotification.class.getName();
    private static MusicNotification musicNotification = null;//饿汉式实现单例模式加载，加载比较慢，但运行时获取对象速度快，线程安全
    private final int NOTIFICATION_ID = 10001;//通知id
    private Notification notification = null;//通知对象，不知道做什么用的
    private NotificationManager notificationManager = null;//通知管理器
    private Builder builder = null;//建造者对象
    private Context context;//上下文对象
    private RemoteViews remoteViews;//远程视图对象
    private final int REQUEST_CODE = 30000;
    //给service发送的广播
    private final String MUSIC_NOTIFICATION_ACTION_PLAY = "musicnotificaion.To.PLAY";
    private final String MUSIC_NOTIFICATION_ACTION_NEXT = "musicnotificaion.To.NEXT";
    private final String MUSIC_NOTIFICATION_ACTION_CLOSE = "musicnotificaion.To.CLOSE";
    private final int MUSIC_NOTIFICATION_VALUE_PLAY = 30001;
    private final int MUSIC_NOTIFICATION_VALUE_NEXT = 30002;
    private final int MUSIC_NOTIFICATION_VALUE_CLOSE =30003;
    private Intent playIntent = null,nextIntent = null,closeIntent = null;//播放、下一首、关闭的意图对象
    private Bitmap bitmap = null;//歌曲图片
    /**
     * 设置通知管理器
     * @param manager
     */
    public void setManager(NotificationManager manager){
        Log.e(TAG,"setManager:"+ manager.toString());
        this.notificationManager = manager;
    }


    /**
     * 私有化构造函数
     */
    private MusicNotification(){
        Log.e(TAG,"创建MusicNotification对象");
        this.context = MyApplication.mContext;
        remoteViews = new RemoteViews(MyApplication.mContext.getPackageName(), R.layout.notification_layout);//初始化远程视图对象，使用自定义的通知布局
        builder = new Builder(context);//初始化建造者对象，

        //初始化控制的意图intent
        playIntent = new Intent();
        playIntent.setAction(MUSIC_NOTIFICATION_ACTION_PLAY);
        nextIntent = new Intent();
        nextIntent.setAction(MUSIC_NOTIFICATION_ACTION_NEXT);
        closeIntent = new Intent();
        closeIntent.setAction(MUSIC_NOTIFICATION_ACTION_CLOSE);
    }

    /**
     * 获取自定义通知对象，饿汉式实现单例模式
     * @return
     */
    public static MusicNotification getMusicNotification(){
        Log.e(TAG,"获取MusicNotification对象");
        if (musicNotification == null){
            musicNotification = new MusicNotification();
        }
        return musicNotification;
    }

    /**
     * 初始化自定义通知
     */
    public void onCreateMusicNotification(){
        Log.e(TAG,"初始化MusicNotification对象");
        //注册点击事件
        //1.播放事件
        playIntent.putExtra("type",MUSIC_NOTIFICATION_VALUE_PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(context,REQUEST_CODE,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_play,pendingPlayIntent);
        //2.下一首事件
        nextIntent.putExtra("type",MUSIC_NOTIFICATION_VALUE_NEXT);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context,REQUEST_CODE,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_next,pendingNextIntent);
        //3.关闭通知事件
        closeIntent.putExtra("type",MUSIC_NOTIFICATION_VALUE_CLOSE);
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(context,REQUEST_CODE,closeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_close,pendingCloseIntent);

        builder.setContent(remoteViews)
                .setOngoing(true)//表示正在运行的通知，常用于音乐播放或者文件下载
                .setSmallIcon(R.drawable.notification_img_test);
        notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;//将此通知放到通知栏的"Ongoing"，“正在运行”组中
        notificationManager.notify(NOTIFICATION_ID,notification);//弹出通知
    }

    /**
     * 更新通知
     */
    public void onUpdateMusicNotification(MusicBean bean, boolean isplay){
        Log.e(TAG,"更新通知--歌曲名称："+ bean.getSongname()+"--isplay:"+isplay);
        //更新歌曲名称
        remoteViews.setTextViewText(R.id.tv_song_name,(bean.getSongname() == null ? "" : bean.getSongname()));
        //更新歌手名字
        remoteViews.setTextViewText(R.id.tv_singer,(bean.getSingername() == null ? "" : bean.getSingername()));
       /* //更新歌曲图片
        try {
            bitmap = Glide.with(context).load(bean.getPath()).asBitmap().centerCrop().into(300,300).get();
            remoteViews.setImageViewBitmap(R.id.img_song,bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
        //更新播放状态：播放或者暂停
        if (isplay){
            remoteViews.setImageViewResource(R.id.img_play,R.mipmap.notification_play);
        }else {
            remoteViews.setImageViewResource(R.id.img_play,R.mipmap.notification_pause);
        }
        onCreateMusicNotification();//弹出更新的通知
    }

    /**
     * 取消通知栏
     */
    public void onCancelMusicNotification(){
        Log.e(TAG,"销毁通知" );
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
