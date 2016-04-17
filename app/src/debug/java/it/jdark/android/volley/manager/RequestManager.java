package it.jdark.android.volley.manager;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import it.jdark.android.volley.MyStethoHttp3Stack;

/**
 * Created by jDark on 15/04/16.
 */
public class RequestManager {

    private final String TAG = getClass().getSimpleName();

    private static RequestQueue mRequestQueue;

    private RequestManager() {
    }

    /**
     * @param context
     * 			application context
     */
    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context, new MyStethoHttp3Stack());
    }
    

    /**
     * @return
     * 		instance of the queue
     */
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("Request Manager not initialized - Call init method fist!");
        }
    }
}
