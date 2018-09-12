package map.baidu.ar.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import map.baidu.ar.logic.IAsyncImageLoader;
import map.baidu.ar.utils.cache.FileCache;
import map.baidu.ar.utils.cache.LRUCache;

/**
 * 异步图片加载
 */
public class AsyncImageLoader {

    private LRUCache mLRUCache;
    private FileCache mFileCache;

    private Map<String, Integer> mLoadingMap;

    private Context mContext;

    private static AsyncImageLoader mInstance;

    public static AsyncImageLoader getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AsyncImageLoader(context);
        }

        return mInstance;
    }

    private AsyncImageLoader(Context context) {

        mContext = context;
        mLRUCache = LRUCache.getInstance();
        mFileCache = FileCache.getInstance(context);
        mLoadingMap = new HashMap<String, Integer>();

    }

    public Bitmap loadImage(String url, IAsyncImageLoader callback) {

        // load bitmap from LRUCache
        Bitmap bitmap;
        synchronized(mLRUCache) {
            bitmap = mLRUCache.get(url);
        }

        if (bitmap == null) {

            if (!mLoadingMap.containsKey(url)) {
                mLoadingMap.put(url, 1);
                queueTask(url, callback);
            }

        }

        return bitmap;
    }

    //add image load task (for net or file cache)
    private void queueTask(String url, IAsyncImageLoader callback) {

        BitmapWorkerTask task = new BitmapWorkerTask(callback);
        task.execute(url);
    }

    public void clearCache() {
        mLRUCache.clear();
        mFileCache.clear();
    }

    private class BitmapWorkerTask extends AsyncTask<String, Integer, Bitmap> {

        private IAsyncImageLoader mCallback;

        private String mURL;

        public BitmapWorkerTask(IAsyncImageLoader callback) {
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            mURL = params[0];
            Bitmap bitmap = HttpGetBitmap(params[0]);
            return bitmap;
        }

        /**
         * this method is used for get image from server and add to file cache
         *
         * @param httpURL
         *
         * @return
         */
        private Bitmap HttpGetBitmap(String httpURL) {

//            if (httpURL.equals("") || httpURL == null || httpURL.equals("NULL")) {
//                return null;
//            }

            //find the file named httpURL hash str
            File file = mFileCache.getFile(httpURL);

            //if file not found load from net
            if (!file.exists()) {

                HttpUtils.getBytesToFile(httpURL, file);

            }

            Bitmap bitmap = IOUtils.decodeFile(file);

            if (mLRUCache.get(httpURL) == null && bitmap != null) {

                mLRUCache.put(httpURL, bitmap);
            } else {
                file.delete();
            }

            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {

            super.onProgressUpdate(progresses);
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            mCallback.onImageLoaded(result);
            mLoadingMap.remove(mURL);
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {

            super.onCancelled();
        }

    }

}