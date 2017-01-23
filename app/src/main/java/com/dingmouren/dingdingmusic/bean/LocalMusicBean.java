package com.dingmouren.dingdingmusic.bean;

import com.dingmouren.dingdingmusic.base.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dingmouren on 2017/1/17.
 */
@Entity
public class LocalMusicBean extends BaseBean {
    private long id;//音乐id
    private String title;//音乐标题
    private String artist;//艺术家
    private long duration;//时长
    private long size;//文件大小
    private String path ;//文件路径
    @Generated(hash = 1890204524)
    public LocalMusicBean(long id, String title, String artist, long duration, long size, String path) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.size = size;
        this.path = path;
    }

    @Generated(hash = 274262434)
    public LocalMusicBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
