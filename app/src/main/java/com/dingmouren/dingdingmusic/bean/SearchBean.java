package com.dingmouren.dingdingmusic.bean;

import com.dingmouren.dingdingmusic.base.BaseBean;

/**
 * Created by dingmouren on 2017/2/10.
 */

public class SearchBean extends BaseBean{

    /**
     * m4a : http://ws.stream.qqmusic.qq.com/108554761.m4a?fromtag=46
     * media_mid : 002WyF5y4Rk1jW
     * songid : 108554761
     * singerid : 6370
     * albumname : 蒙面唱将猜猜猜 第1期
     * downUrl : http://dl.stream.qqmusic.qq.com/108554761.mp3?vkey=36792B0E9B4D62913A85D2340FFBFADF66CF898EC1BB6672EFBB1718ED537262CFB6AADC08B4DD6A448C0305D14434E0EC1DDB9942591186&guid=2718671044
     * singername : 谭晶
     * songname : 海阔天空 (Live)
     * strMediaMid : 002WyF5y4Rk1jW
     * albummid : 001LjNh61tMDJ1
     * songmid : 002WyF5y4Rk1jW
     * albumpic_big : http://i.gtimg.cn/music/photo/mid_album_300/J/1/001LjNh61tMDJ1.jpg
     * albumpic_small : http://i.gtimg.cn/music/photo/mid_album_90/J/1/001LjNh61tMDJ1.jpg
     * albumid : 1608672
     */

    private String m4a;
    private String media_mid;
    private int songid;
    private int singerid;
    private String albumname;
    private String downUrl;
    private String singername;
    private String songname;
    private String strMediaMid;
    private String albummid;
    private String songmid;
    private String albumpic_big;
    private String albumpic_small;
    private int albumid;

    public String getM4a() {
        return m4a;
    }

    public void setM4a(String m4a) {
        this.m4a = m4a;
    }

    public String getMedia_mid() {
        return media_mid;
    }

    public void setMedia_mid(String media_mid) {
        this.media_mid = media_mid;
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

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
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

    public String getStrMediaMid() {
        return strMediaMid;
    }

    public void setStrMediaMid(String strMediaMid) {
        this.strMediaMid = strMediaMid;
    }

    public String getAlbummid() {
        return albummid;
    }

    public void setAlbummid(String albummid) {
        this.albummid = albummid;
    }

    public String getSongmid() {
        return songmid;
    }

    public void setSongmid(String songmid) {
        this.songmid = songmid;
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

    public int getAlbumid() {
        return albumid;
    }

    public void setAlbumid(int albumid) {
        this.albumid = albumid;
    }
}
