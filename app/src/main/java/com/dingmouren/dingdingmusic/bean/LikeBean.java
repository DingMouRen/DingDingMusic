package com.dingmouren.dingdingmusic.bean;

import com.dingmouren.dingdingmusic.base.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by dingmouren on 2017/2/10.
 * 收藏的歌曲
 */
@Entity
public class LikeBean  extends BaseBean{
    @Id(autoincrement = true)
    private Long id;
    private int seconds;
    private String albumpic_big;
    private String albumpic_small;
    private String url;
    private String singername;
    private String songname;
    private int type;//表示歌曲的类型

    @Generated(hash = 1258777425)
    public LikeBean() {
    }
    @Generated(hash = 744897691)
    public LikeBean(Long id, int seconds, String albumpic_big, String albumpic_small, String url, String singername, String songname, int type) {
        this.id = id;
        this.seconds = seconds;
        this.albumpic_big = albumpic_big;
        this.albumpic_small = albumpic_small;
        this.url = url;
        this.singername = singername;
        this.songname = songname;
        this.type = type;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public String getAlbumpic_big() {
        return albumpic_big;
    }

    public void setAlbumpic_big(String albumpic_big) {
        this.albumpic_big = albumpic_big;
    }

    public String getAlbumpic_small() {
        return albumpic_small;
    }

    public void setAlbumpic_small(String albumpic_small) {
        this.albumpic_small = albumpic_small;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSingername() {
        return singername;
    }

    public void setSingername(String singername) {
        this.singername = singername;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
