package map.baidu.ar.utils.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Android internal memory cache
 */
public class LRUCache {

    private LruCache<String, Bitmap> mLruCache;
    private static LRUCache mInstance;

    public static LRUCache getInstance() {
        if (mInstance == null) {
            mInstance = new LRUCache();
        }
        return mInstance;
    }

    private LRUCache() {
        int mCacheSize = 8 * 1024 * 1024;
        mLruCache = new LruCache<String, Bitmap>(mCacheSize);
    }

    public LRUCache(int cachesize) {
        mLruCache = new LruCache<String, Bitmap>(cachesize);
    }

    public Bitmap get(String key) {
        synchronized(mLruCache) {
            return mLruCache.get(key);
        }
    }

    public void put(String key, Bitmap obj) {
        synchronized(mLruCache) {
            mLruCache.put(key, obj);
        }
    }

    public void clear() {
        synchronized(mLruCache) {
            mLruCache.evictAll();
        }
    }
}
