package com.dingmouren.dingdingmusic.bean;

import com.dingmouren.dingdingmusic.base.BaseBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by dingmouren on 2017/2/3.
 */
@Entity
public class MusicBean extends BaseBean{
    /**
     * songname : 舍不得 (《漂亮的李慧珍》电视剧插曲)
     * seconds : 148
     * albummid : 001sP1d63Zutvt
     * songid : 200506552
     * singerid : 1099829
     * albumpic_big : http://i.gtimg.cn/music/photo/mid_album_300/v/t/001sP1d63Zutvt.jpg
     * albumpic_small : http://i.gtimg.cn/music/photo/mid_album_90/v/t/001sP1d63Zutvt.jpg
     * downUrl : http://dl.stream.qqmusic.qq.com/200506552.mp3?vkey=B184A6356B726EDAF0F1A645833D6B4FA939EA201C7984D6F00FDD7FF2556C3C703B23688C0E5143E8306E12A6064E612FB4B9DDC36214E5&guid=2718671044
     * url : http://ws.stream.qqmusic.qq.com/200506552.m4a?fromtag=46
     * singername : 迪丽热巴
     * albumid : 1826345
     */
    private String songname;
    private int seconds;
    private String albummid;
    private int songid;
    private int singerid;
    private String albumpic_big;
    private String albumpic_small;
    private String downUrl;
    private String url;
    private String singername;
    private int albumid;
    private int type;//表示歌曲的类型

    @Generated(hash = 1899243370)
    public MusicBean() {
    }

    @Generated(hash = 1173238035)
    public MusicBean(String songname, int seconds, String albummid, int songid, int singerid, String albumpic_big, String albumpic_small, String downUrl, String url, String singername, int albumid, int type) {
        this.songname = songname;
        this.seconds = seconds;
        this.albummid = albummid;
        this.songid = songid;
        this.singerid = singerid;
        this.albumpic_big = albumpic_big;
        this.albumpic_small = albumpic_small;
        this.downUrl = downUrl;
        this.url = url;
        this.singername = singername;
        this.albumid = albumid;
        this.type = type;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public String getAlbummid() {
        return albummid;
    }

    public void setAlbummid(String albummid) {
        this.albummid = albummid;
    }

    public int getSongid() {
        return songid;
    }

    public void setSongid(int songid) {
        this.songid = songid;
    }

    public int getSingerid() {
        return singerid;
    }

    public void setSingerid(int singerid) {
        this.singerid = singerid;
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

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
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

    public int getAlbumid() {
        return albumid;
    }

    public void setAlbumid(int albumid) {
        this.albumid = albumid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
