package it.jdark.android.volley;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import it.jdark.android.volley.manager.RequestManager;
import it.jdark.android.volley.pojo.ResponseData;

/**
 * Created by jDark on 16/04/16.
 */
public class VolleyJSonRequestCached {

    private final String TAG = getClass().getSimpleName();

    public static String requestUrl;
    public static AsyncResponseCallBack asyncResponseCallBack;

    public VolleyJSonRequestCached(String url, AsyncResponseCallBack asR){
        requestUrl = url;
        asyncResponseCallBack = asR;
    }


    /**
     * This implementation doen't give to you any control about caching policy (es expired)
     */
    public void executeCachedRequestAsString() {
        Cache cache = RequestManager.getRequestQueue().getCache();
        Cache.Entry entry = cache.get(requestUrl);
        if (entry != null) {
            Log.d(TAG, "Server Date: " + new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date(entry.serverDate)));
            Log.d(TAG, String.valueOf((entry.ttl - System.currentTimeMillis())) + " -- isExpired: " + entry.isExpired());
            Log.d(TAG, String.valueOf(entry.softTtl - System.currentTimeMillis()) + " -- Time to Alive expired: " + entry.refreshNeeded());
            if (entry.refreshNeeded()) {
                if (!entry.isExpired()) {
                    Log.i(TAG, "Time to Alive is over! -- Get data from cache anyway .... refresh it with a new network call (on background - Thanks Volley)");
                    try {
                        String data = new String(entry.data, "UTF-8");
                        parseResponse(data);

                        cache.invalidate(requestUrl, false);
                        // Volley should do it automatically but apparently doesn't do!
                        Request<String> response = getDataOnTheNet();
                        MainApplication.getInstance().addRequestToQueue(response);
                        // Looking the Log message seems like application sends double request!
                        // Logging with Stetho there is only one as aspected! (That is good)
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "Cache is expired! -- get new data with a new networkcall");
                    // Keep cache clean by old data
                    cache.remove(requestUrl);
//                    cache.invalidate(requestUrl, true);

                    Request<String> response = getDataOnTheNet();
                    MainApplication.getInstance().addRequestToQueue(response);
                }
            } else {
                Log.i(TAG, "Time to Alive OK! -- Get data directly from cache without any network call");
                try {
                    String data = new String(entry.data, "UTF-8");
                    parseResponse(data);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Data is NO in the cache
            Log.i(TAG, "Data is not cached! -- Get data from new network call");
            Request<String> response = getDataOnTheNet();
            MainApplication.getInstance().addRequestToQueue(response);
        }
    }

    private Request<String> getDataOnTheNet() {
        return new Request<String>(Request.Method.GET, requestUrl, myErrorListener()) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, parseIgnoreCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(String response) {
                // Here we get own data form the network and it's cached according the policy set in the method "parseIgnoreCacheHeaders"
                Log.d(TAG, "Response: " + response);
                parseResponse(response);
            }
        };
    }

    private void parseResponse(String data) {
        Gson gson = new Gson();
        //converting JSON to ResponseData object
        ResponseData responseData = gson.fromJson(data, ResponseData.class);
        //putting the result on the method of interface, our activity will implements that
        asyncResponseCallBack.processJSonFinish(responseData, requestUrl);
    }

    private Response.ErrorListener myErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " +error.getMessage());
            }
        };
    }

    /**
     * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
     * Cache-control headers are ignored. SoftTtl == 3 mins, ttl == 24 hours.
     * @param response The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        final long cacheHitButRefreshed = 5 * 60 * 1000; // in 5 minutes cache will be hit, but also refreshed on background
        final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }

}
