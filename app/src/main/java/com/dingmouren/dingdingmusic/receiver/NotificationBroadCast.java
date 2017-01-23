package com.dingmouren.dingdingmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dingmouren.dingdingmusic.Constant;

/**
 * Created by dingmouren on 2017/1/19.
 */

public class NotificationBroadCast extends BroadcastReceiver {
    private static final String TAG = NotificationBroadCast.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra(Constant.NOTIFICATION_INTENT_ACTION,-1);
        Log.e(TAG,"action:"+ action  );
    }
}
