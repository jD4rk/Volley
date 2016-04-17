package it.jdark.android.volley;

import android.app.Application;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.facebook.stetho.Stetho;

import it.jdark.android.volley.manager.ImageCacheManager;
import it.jdark.android.volley.manager.RequestManager;

/**
 * Created by jDark on 13/04/16.
 */
public class MainApplication extends Application {

    private final String TAG = getClass().getSimpleName();

    private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided

    private static MainApplication INSTANCE;
    public final static String DEFAULT_TAG = "VolleyExample";


    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        RequestManager.init(this);
        Stetho.initializeWithDefaults(getApplicationContext());
        createImageCache();
    }

    public static synchronized MainApplication getInstance() {
        return INSTANCE;
    }


    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.
     */
    private void createImageCache(){
        ImageCacheManager.getInstance().init(this,
                this.getPackageCodePath()
                , DISK_IMAGECACHE_SIZE
                , DISK_IMAGECACHE_COMPRESS_FORMAT
                , DISK_IMAGECACHE_QUALITY
                , ImageCacheManager.CacheType.MEMORY);
    }

    public <T> void addRequestToQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? DEFAULT_TAG : tag);
        Log.d(TAG, String.format("Add request %s to queue!", req.getUrl()));
        RequestManager.getRequestQueue().add(req);
    }

    public <T> void addRequestToQueue(Request<T> req) {
        req.setTag(DEFAULT_TAG);
        Log.d(TAG, String.format("Add request %s to queue!", req.getUrl()));
        RequestManager.getRequestQueue().add(req);
    }

    public void cancellPendingRequest(Object tag) {
        RequestManager.getRequestQueue().cancelAll(tag);
    }
}
