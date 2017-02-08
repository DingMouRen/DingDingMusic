package com.dingmouren.dingdingmusic.ui.volkslied;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.ui.localmusic.LocalMusicAdapter;
import com.dingmouren.dingdingmusic.ui.rock.RockAdapter;

import java.util.List;

/**
 * Created by mouren on 2017/2/7.
 */

public class VolksliedAdapter extends RecyclerView.Adapter<VolksliedAdapter.ViewHolder> {
    private List<MusicBean> mList;
    private Context mContext;
    private LocalMusicAdapter.OnItemClickListener onItemClickListener;
    private MusicBean playingBean;//正在播放的歌曲

    public VolksliedAdapter(Context context){
        this.mContext = context;
    }

    public void setList(List<MusicBean> list){
        this.mList = list;
    }
    public void setOnItemClickListener(LocalMusicAdapter.OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public void showPlaying(MusicBean bean){
        this.playingBean = bean;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_volkslied,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mList.get(position),playingBean);
        holder.relative_volkslied.setOnClickListener((view -> {
            if (null != onItemClickListener){
                onItemClickListener.onItemClickListener(holder.relative_volkslied,position);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName;
        TextView tvSingerName;
        ImageView imgAlbum;
        ImageView fabIsPlay;
        RelativeLayout relative_volkslied;
        public ViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tv_songname);
            tvSingerName = (TextView) itemView.findViewById(R.id.tv_singername);
            imgAlbum = (ImageView) itemView.findViewById(R.id.img_album);
            fabIsPlay = (ImageView) itemView.findViewById(R.id.fab_isPlay);
            relative_volkslied = (RelativeLayout) itemView.findViewById(R.id.relative_volkslied);
        }
        private void bindData(MusicBean bean,MusicBean playingBean){
            if (null != bean){
                tvSongName.setText(bean.getSongname());
                tvSingerName.setText(bean.getSingername());
                Glide.with(mContext).load(bean.getAlbumpic_small()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgAlbum);
            }
            if (null != bean && null != playingBean) {
                if (!bean.getSongname().equals(playingBean.getSongname()) && bean.getSongid() !=playingBean.getSongid()) {//先设置没有播放的歌曲的样式，随后设置正在播放歌曲的样式，有点覆盖的意思
                    fabIsPlay.setVisibility(View.GONE);
                }else if (bean.getSongname().equals(playingBean.getSongname())
                        && bean.getSingername().equals(playingBean.getSingername())){
                    fabIsPlay.setVisibility(View.VISIBLE);
                    Glide.with(MyApplication.mContext).load(R.mipmap.playing).asGif().diskCacheStrategy(DiskCacheStrategy.NONE).into(fabIsPlay);
                }
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClickListener(View view,int position);
    }
}
