package com.dingmouren.dingdingmusic.ui.search;

import android.widget.TextView;

import com.dingmouren.dingdingmusic.base.BasePresenter;
import com.dingmouren.dingdingmusic.base.BaseView;
import com.dingmouren.dingdingmusic.bean.MusicBean;
import com.dingmouren.dingdingmusic.bean.SearchBean;

import java.util.List;

/**
 * Created by dingmouren on 2017/2/10.
 */

public interface SearchConstract {

    interface View extends BaseView{
       void setData(List<SearchBean> list);
    }

    interface Presenter {
        void requestData(String search);
    }
}
