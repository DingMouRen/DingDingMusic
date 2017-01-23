package com.dingmouren.dingdingmusic.ui.localmusic;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.bean.LocalMusicBean;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dingmouren on 2017/1/17.
 */

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {
    private static final String TAG = LocalMusicAdapter.class.getName();
    private List<LocalMusicBean> mList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private LocalMusicBean playingBean;//正在播放的歌曲
    public LocalMusicAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<LocalMusicBean> list) {
        this.mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public void showPlaying(LocalMusicBean bean){
        this.playingBean = bean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_local_music,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mList.get(position),playingBean);
        holder.cardView.setOnClickListener((view -> {
            if (null != onItemClickListener){
                onItemClickListener.onItemClickListener(holder.cardView,position);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
         TextView songName;
         TextView singer;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            songName = (TextView) itemView.findViewById(R.id.tv_song_name);
            singer = (TextView) itemView.findViewById(R.id.tv_singer);
        }

        private void bindData(LocalMusicBean bean,LocalMusicBean playingBean){
            if (null != bean){
                songName.setText(bean.getTitle());
                singer.setText(bean.getArtist());

            }
            if (null != bean && null != playingBean) {
                if (!bean.getTitle().equals(playingBean.getTitle()) && bean.getId() != (playingBean.getId())) {//先设置没有播放的歌曲的样式，随后设置正在播放歌曲的样式，有点覆盖的意思
                    songName.setTextColor(itemView.getResources().getColor(android.R.color.black));
                }else if (bean.getId() == playingBean.getId()
                        && bean.getTitle().equals(playingBean.getTitle())
                        && bean.getDuration() == playingBean.getDuration()){
                    songName.setTextColor(itemView.getResources().getColor(android.R.color.holo_red_light));
                }
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClickListener(View view,int position);
    }
}
