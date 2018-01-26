package map.baidu.ar.utils.cache;

import android.content.Context;

import java.io.File;

/**
 * Android file cache by xingdaming
 */
public class FileCache {

    private File mCacheDir;
    private static FileCache mInstance;

    public static FileCache getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new FileCache(context);
        }
        return mInstance;
    }

    private FileCache(Context context) {

        mCacheDir = context.getCacheDir();

        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
    }

    public boolean isExist(String url) {
        String filename = String.valueOf(url.hashCode());
        File f = new File(mCacheDir, filename);
        return f.exists();
    }

    public File getFile(String url) {

        String filename = String.valueOf(url.hashCode());
        // we do not check file exist or not
        // directly return
        File f = new File(mCacheDir, filename);
        return f;
    }

    public void clear() {
        File[] files = mCacheDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            f.delete();
        }
    }

}