package map.baidu.ar.utils.cache;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * java memory cache by xingdaming
 */
public class MemoryCache {

    private Map<String, SoftReference<Bitmap>> mCache;

    private static MemoryCache mInstance;

    public static MemoryCache getInstance() {

        if (mInstance == null) {
            mInstance = new MemoryCache();
        }
        return mInstance;
    }

    private MemoryCache() {
        mCache = Collections
                .synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());
    }

    public Bitmap get(String key) {
        if (!mCache.containsKey(key)) {
            return null;
        }
        SoftReference<Bitmap> ref = mCache.get(key);
        return ref.get();
    }

    public void put(String key, Bitmap bitmap) {
        mCache.put(key, new SoftReference<Bitmap>(bitmap));
    }

    public void clear() {
        mCache.clear();
    }

}