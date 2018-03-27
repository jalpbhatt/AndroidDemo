package com.bridge.androidtechnicaltest.db;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class PupilsDatabaseObserver extends BroadcastReceiver {

    private static final String TAG = PupilsDatabaseObserver.class.getName();
    private static final boolean DEBUG = true;

    private PupilsDatabaseLoader mDBLoader;
    public PupilsDatabaseObserver(PupilsDatabaseLoader loader) {
        mDBLoader = loader;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEBUG) Log.i(TAG, "The observer has detected an application change!" +
                " Notifying Loader...");

        mDBLoader.onContentChanged();
    }
}
