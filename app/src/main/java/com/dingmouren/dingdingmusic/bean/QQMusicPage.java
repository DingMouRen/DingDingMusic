package com.dingmouren.dingdingmusic.bean;

import java.util.List;

/**
 * Created by dingmouren on 2017/2/3.
 */

public class QQMusicPage<T> {
    private int total_song_num;
    private int ret_code;
    private String update_time;
    private int color;
    private int cur_song_num;
    private int comment_num;
    private int currentPage;
    private int song_begin;
    private int totalpage;
    private T songlist;

    public int getTotal_song_num() {
        return total_song_num;
    }

    public void setTotal_song_num(int total_song_num) {
        this.total_song_num = total_song_num;
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCur_song_num() {
        return cur_song_num;
    }

    public void setCur_song_num(int cur_song_num) {
        this.cur_song_num = cur_song_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getSong_begin() {
        return song_begin;
    }

    public void setSong_begin(int song_begin) {
        this.song_begin = song_begin;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

    public T getSonglist() {
        return songlist;
    }

    public void setSonglist(T songlist) {
        this.songlist = songlist;
    }

    @Override
    public String toString() {
        return "QQMusicPage{" +
                "total_song_num=" + total_song_num +
                ", ret_code=" + ret_code +
                ", update_time='" + update_time + '\'' +
                ", color=" + color +
                ", cur_song_num=" + cur_song_num +
                ", comment_num=" + comment_num +
                ", currentPage=" + currentPage +
                ", song_begin=" + song_begin +
                ", totalpage=" + totalpage +
                ", songlist=" + songlist +
                '}';
    }
}
