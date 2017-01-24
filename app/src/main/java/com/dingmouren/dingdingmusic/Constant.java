package com.dingmouren.dingdingmusic;

/**
 * Created by dingmouren on 2017/1/19.
 */

public class  Constant {
    public static final String NOTIFICATION_INTENT_ACTION = "notification_intent_action";
    //PlayingActivity的message.what的属性
    public static final int PLAYING_ACTIVITY_PLAY = 0x10001;
    public static final int PLAYING_ACTIVITY_PRE = 0x10002;
    public static final int PLAYING_ACTIVITY_NEXT = 0x10003;
    public static final int PLAYING_ACTIVITY_SINGLE = 0x10004;
    public static final int PLAYING_ACTIVITY = 0x10005;//初始化PlayingActivity的Messenger对象的标识
    public static final int PLAYING_ACTIVITY_CUSTOM_PROGRESS = 0x10006;//用户拖动进度条时，更新音乐播放器的播放进度
    public static final int PLAYING_ACTIVITY_DATA = 0x10007;//向播放器传递的歌曲集合数据
    public static final String PLAYING_ACTIVITY_DATA_KEY = "playing_activity_data_key";//向播放器传递的歌曲集合数据的key

    //MediaPlayerService的message.what的属性值
    public static final int MEDIA_PLAYER_SERVICE_PROGRESS = 0x20001;
    public static final int MEDIA_PLAYER_SERVICE_SONG_PLAYING = 0x20002;
    public static final int MEDIA_PLAYER_PLAY_SINGLEONE = 0x20003;//单曲循环
    public static final int MEDIA_PLAYER_PLAY_ALL = 0x20004;//循环播放
    public static final int MEDIA_PLAYER_SERVICE_IS_PLAYING = 0x20005;//播放器是否在播放音乐，用于修改PlayingActivity的UI
    public static final String MEDIA_PLAYER_SERVICE_MODEL_PLAYING = "song_playing";//服务端正在播放的歌曲
    //LocalMusicActivity
    public static final int LOCAL_MUSIC_ACTIVITY = 0x30001;
    public static final int LOCAL_MUSIC_ACTIVITY_PLAY = 0x30002;//播放点击的歌曲

}
