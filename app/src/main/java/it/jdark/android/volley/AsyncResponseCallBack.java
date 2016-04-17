package it.jdark.android.volley;

import it.jdark.android.volley.pojo.ResponseData;

/**
 * Created by jDark on 14/04/16.
 */
public interface AsyncResponseCallBack {
    void processJSonFinish(ResponseData responseData, String url);
}
