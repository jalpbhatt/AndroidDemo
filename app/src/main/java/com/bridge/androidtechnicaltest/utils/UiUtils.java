package com.bridge.androidtechnicaltest.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Provide utility methods to support UI operations
 */
public final class UiUtils {

    /**
     * Display toast message to user
     *
     * @param context application context
     * @param resId id of string message
     */
    public static void showToast(Context context, int resId) {

        if (context == null)
            return;

        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
