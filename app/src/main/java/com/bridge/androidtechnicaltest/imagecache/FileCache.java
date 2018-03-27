package com.bridge.androidtechnicaltest.imagecache;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Provide local disk cache in internal memory of device
 */
public class FileCache {

    private static final String CACHE_FILE_NAME = "Cache";

    private File mCacheDir;

    public FileCache(Context context) {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            mCacheDir = new File(android.os.Environment.getExternalStorageDirectory(), CACHE_FILE_NAME);
        else
            mCacheDir = context.getCacheDir();
        if (!mCacheDir.exists())
            mCacheDir.mkdirs();
    }

    public File getFile(String url) {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename = String.valueOf(url.hashCode());
        File f = new File(mCacheDir, filename);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;

    }

    public void clear() {
        File[] files = mCacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}