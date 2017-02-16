package com.dingmouren.dingdingmusic.listener;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.dingmouren.dingdingmusic.ui.search.SearchActivity;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.lang.ref.WeakReference;

/**
 * Created by dingmouren on 2017/2/13.
 */

public class MyOnSearchActionListener implements MaterialSearchBar.OnSearchActionListener {
    private WeakReference<SearchActivity> weakActivity;
    private InputMethodManager inputMethodManager;
    public MyOnSearchActionListener(SearchActivity activity) {
        weakActivity = new WeakReference<SearchActivity>(activity);
        inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onSearchStateChanged(boolean b) {
        SearchActivity activity = weakActivity.get();
        if (null == activity) return;
        if (TextUtils.isEmpty(activity.mSearchBar.getText().trim())){
            activity.finish();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence charSequence) {
        SearchActivity activity = weakActivity.get();
        if (null == activity) return;
        activity.mProgressBar.setVisibility(View.VISIBLE);
        activity.mPresenter.requestData(String.valueOf(charSequence));
        if (null != inputMethodManager){
            inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(),0);
        }
    }

    @Override
    public void onButtonClicked(int i) {

    }

}
