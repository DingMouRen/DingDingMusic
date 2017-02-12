package com.dingmouren.dingdingmusic.ui.collected;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingmouren on 2017/2/10.
 */

public class CollectedAdapter extends RecyclerView.Adapter<CollectedAdapter.ViewHolder> {
    private static final String TAG = LocalMusicAdapter.class.getName();
    private List<MusicBean> mList = new ArrayList<>();
    private Context mContext;
    private LocalMusicAdapter.OnItemClickListener onItemClickListener;

    public CollectedAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setList(List<MusicBean> list) {
        mList.clear();
        this.mList = list;
    }

    public void setOnItemClickListener(LocalMusicAdapter.OnItemClickListener listener){
        this.onItemClickListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_collected,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mList.get(position));
        holder.relateive_collected.setOnClickListener((view -> {
            if (null != onItemClickListener){
                onItemClickListener.onItemClickListener(holder.relateive_collected,position);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return mList==null ? 0 : mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relateive_collected;
        TextView songName;
        TextView singer;
        ImageView fab_isPlay;
        public ViewHolder(View itemView) {
            super(itemView);
            relateive_collected = (RelativeLayout) itemView.findViewById(R.id.relateive_collected);
            songName = (TextView) itemView.findViewById(R.id.tv_song_name);
            singer = (TextView) itemView.findViewById(R.id.tv_singer);
            fab_isPlay = (ImageView) itemView.findViewById(R.id.fab_isPlay);
        }
        private void bindData(MusicBean bean ){
            if (null != bean){
                songName.setText(bean.getSongname());
                singer.setText(bean.getSingername());

            }

        }
    }

    public interface OnItemClickListener{
        void onItemClickListener(View view,int position);
    }
}
