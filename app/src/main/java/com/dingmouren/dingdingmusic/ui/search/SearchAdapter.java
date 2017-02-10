package com.dingmouren.dingdingmusic.ui.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.bean.SearchBean;
import com.dingmouren.dingdingmusic.ui.localmusic.LocalMusicAdapter;

import java.util.List;

import static com.dingmouren.dingdingmusic.R.color.singer;

/**
 * Created by dingmouren on 2017/2/10.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private static final String TAG = SearchAdapter.class.getName();
    private Context mContext;
    private List<SearchBean> mList;
    private OnItemClickListener onItemClickListener;

    public SearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setList(List<SearchBean> list) {
        this.mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mList.get(position));
        holder.relateive_search.setOnClickListener((view -> {
            if (null != onItemClickListener) {
                onItemClickListener.onItemClickListener(mList.get(position));
            }
        }));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relateive_search;
        TextView songName;
        TextView singer;

        public ViewHolder(View itemView) {
            super(itemView);
            relateive_search = (RelativeLayout) itemView.findViewById(R.id.relateive_search);
            songName = (TextView) itemView.findViewById(R.id.tv_song_name);
            singer = (TextView) itemView.findViewById(R.id.tv_singer);
        }

        private void bindData(SearchBean bean) {
            if (null != bean) {
                songName.setText(bean.getSongname());
                singer.setText(bean.getSingername());

            }
        }
    }


    public interface OnItemClickListener {
        void onItemClickListener(SearchBean bean);
    }
}
