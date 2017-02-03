package com.dingmouren.dingdingmusic.ui.localmusic;

import com.dingmouren.dingdingmusic.base.BasePresenter;
import com.dingmouren.dingdingmusic.base.BaseView;
import com.dingmouren.dingdingmusic.bean.MusicBean;

import java.util.List;

/**
 * Created by dingmouren on 2017/1/17.
 */

public interface LocalMusicConstract {

    interface View extends BaseView{
        void setData(List<MusicBean> list);
    }

    interface Presenter extends BasePresenter{

    }
}
