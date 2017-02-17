package com.dingmouren.dingdingmusic.ui.rock;

import com.dingmouren.dingdingmusic.base.BasePresenter;
import com.dingmouren.dingdingmusic.base.BaseView;
import com.dingmouren.dingdingmusic.bean.MusicBean;

import java.util.List;

/**
 * Created by dingmouren on 2017/2/15.
 */

public interface RockConstract {
    interface View extends BaseView {
        void setData(List<MusicBean> list);
        void setRefresh(boolean refresh);
    }

    interface Presenter extends BasePresenter {

    }
}
