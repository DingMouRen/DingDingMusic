package com.dingmouren.dingdingmusic.ui.localmusic;

import android.content.Context;
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
    public LocalMusicAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<LocalMusicBean> list) {
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_local_music,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
         TextView songName;
         TextView singer;
        public ViewHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.tv_song_name);
            singer = (TextView) itemView.findViewById(R.id.tv_singer);
        }

        private void bindData(LocalMusicBean bean){
            if (null != bean){
                songName.setText(bean.getTitle());
                singer.setText(bean.getArtist());
            }
        }
    }
}
