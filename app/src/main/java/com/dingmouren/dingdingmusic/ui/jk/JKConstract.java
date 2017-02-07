package com.dingmouren.dingdingmusic.ui.jk;

import com.dingmouren.dingdingmusic.base.BasePresenter;
import com.dingmouren.dingdingmusic.base.BaseView;
import com.dingmouren.dingdingmusic.bean.MusicBean;

import java.util.List;

/**
 * Created by dingmouren on 2017/2/7.
 */

public interface JKConstract {
    interface View extends BaseView{
        void setData(List<MusicBean> list);
    }

    interface Presenter extends BasePresenter{

    }
}
