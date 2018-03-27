package com.bridge.androidtechnicaltest.imagecache;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provide internal mCache in application memory
 */
public class MemoryCache {

    private Map<String, SoftReference<Bitmap>> mCache = Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());

    public Bitmap get(String id) {
        if (!mCache.containsKey(id))
            return null;
        SoftReference<Bitmap> ref = mCache.get(id);
        return ref.get();
    }

    public void put(String id, Bitmap bitmap) {
        mCache.put(id, new SoftReference<Bitmap>(bitmap));
    }

    public void clear() {
        mCache.clear();
    }
}