package com.bridge.androidtechnicaltest.db;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.bridge.androidtechnicaltest.model.PupilDetails;

import java.lang.ref.WeakReference;
import java.util.List;

/* Custom Async loader to fetch the data faster and asynchronously from local storage */
public class PupilsDatabaseLoader extends AsyncTaskLoader<List<PupilDetails>> {

    private static final String TAG = PupilsDatabaseLoader.class.getName();
    private static final boolean DEBUG = true;
    private static final String INTENT_ACTION_DATA_CHANGE = "DATA_CHANGE";

    private WeakReference<Context> mContext;
    private List<PupilDetails> mPupilsList = null;

    // An observer to notify the Loader when data updated.
    private PupilsDatabaseObserver mDBObserver;

    public PupilsDatabaseLoader(Context ctx) {
        super(ctx);
        mContext = new WeakReference<Context>(ctx);
    }

    @Override
    public List<PupilDetails> loadInBackground() {

        List<PupilDetails> pupilsInfo = null;

        // For Prevention
        try {
            Context ctx = mContext.get();
            DatabaseHelper db = new DatabaseHelper(ctx);
            pupilsInfo = db.getAllPupilList();

            if (DEBUG && pupilsInfo != null) {
                Log.d(TAG, "Pupils Count = " + pupilsInfo.size());
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Unable to fetch pupils data with error = " + e.getMessage());
            }
        }

        return pupilsInfo;
    }

    @Override
    public void deliverResult(List<PupilDetails> data) {
        super.deliverResult(data);
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            return;
        }

        mPupilsList = data;
        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (DEBUG) Log.i(TAG, "OnStartLoading() called!");

        if (mPupilsList != null) {
            // Deliver any previously loaded data immediately.
            if (DEBUG) Log.i(TAG, "Delivering previously loaded data to the client...");
            deliverResult(mPupilsList);
        }

        // Register the observers that will notify the Loader when changes are made.
        if (mDBObserver == null) {
            Context ctx = mContext.get();
            mDBObserver = new PupilsDatabaseObserver(this);
            ctx.registerReceiver(mDBObserver, new IntentFilter(INTENT_ACTION_DATA_CHANGE));
        }

        if (takeContentChanged()) {
            // When the observer detects an update, it will call
            // onContentChanged() on the Loader, which will cause the next call to
            // takeContentChanged() to return true. If this is ever the case (or if
            // the current data is null), we force a new load.
            if (DEBUG) Log.i(TAG, "A content change has been detected... so force load!");
            forceLoad();
            Intent intent = new Intent(INTENT_ACTION_DATA_CHANGE);
            intent.putExtra(INTENT_ACTION_DATA_CHANGE, "Data to be passed");
            getContext().sendBroadcast(intent);
        } else if (mPupilsList == null) {
            // If the current data is null... then we should make it non-null! :)
            if (DEBUG) Log.i(TAG, "The current data is data is null... so force load!");
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        // Send cancel request when stop loader requested
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'apps'.
        if (mPupilsList != null) {
            mPupilsList = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
        if (mDBObserver != null) {
            Context ctx = mContext.get();
            ctx.unregisterReceiver(mDBObserver);
            mDBObserver = null;
        }
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }
}
