package com.dingmouren.dingdingmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dingmouren.dingdingmusic.ui.localmusic.LocalMusicActivity;
import com.dingmouren.dingdingmusic.ui.lock.LockActivity;

import org.androidannotations.annotations.Receiver;

import static com.dingmouren.dingdingmusic.Constant.NOTIFY_SCREEN_OFF;

/**
 * Created by dingmouren on 2017/2/16.
 */

public class ScreenOffReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Intent lockIntent = new Intent(context, LockActivity.class);
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(lockIntent);
        }
    }
}
