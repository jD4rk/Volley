package it.jdark.android.volley;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import it.jdark.android.volley.pojo.ResponseData;

/**
 * Created by jDark on 14/04/16.
 */
public class VolleyJSonRequest {

    private final String TAG = getClass().getSimpleName();

    public static String requestUrl;
    public static AsyncResponseCallBack asyncResponseCallBack;

    public VolleyJSonRequest(String url, AsyncResponseCallBack asR){
        requestUrl = url;
        asyncResponseCallBack = asR;
    }

    public void executeRequestAsString(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: "+response);

                        //after executing the request if it is successful request then we will get JSON response. here it is ” response”
                        Gson gson = new Gson();
                        //converting JSON to ResponseData object
                        ResponseData responseData = gson.fromJson(response, ResponseData.class);
                        //putting the result on the method of interface, our activity will implements that
                        asyncResponseCallBack.processJSonFinish(responseData, requestUrl);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                    }
                });
        MainApplication.getInstance().addRequestToQueue(stringRequest);
    }
}
