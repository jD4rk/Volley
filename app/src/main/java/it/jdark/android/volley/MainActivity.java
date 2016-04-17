package it.jdark.android.volley;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import it.jdark.android.volley.databinding.MainActivityBinding;
import it.jdark.android.volley.manager.RequestManager;
import it.jdark.android.volley.pojo.ResponseData;
import it.jdark.android.volley.viewModel.ResponseDataViewModel;

public class MainActivity extends AppCompatActivity implements AsyncResponseCallBack {

    private final String TAG = getClass().getSimpleName();

    private final String URL = "http://api.openweathermap.org/data/2.5/";
    private final String URL_ARG = "Rimini,it";
    private final String API_KEY = "931d25ea8d06c2a5cc3bed22c2a0cdac";
    private final String URL_IMAGE = "http://rlv.zcache.com/table_number_1_photo_statuette-r483eb68376aa458cb281c4762fb0cd47_x7saw_8byvr_324.jpg";
    private final String URL_IMAGE_2 = "http://mureadritta.it/negozio/images/Due_nero_Nacra17.jpg";

    private AsyncResponseCallBack mCallback= MainActivity.this;

    MainActivityBinding binding;
    ResponseData data;
    ResponseDataViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        data = null;
        viewModel = new ResponseDataViewModel(data);
        binding.setViewModel(viewModel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    @Override
    protected void onResume() {
        super.onResume();
        // DEMONSTRATION PURPOSE ONLY - SEND QUERY WITH SOME DALAY JUST TO CHECK IF "UI" IS UPDATE PROPERLY
        // Wait 5s for JSonQuery
        Handler handlerJSon = new Handler();
        handlerJSon.postDelayed(new Runnable() {
            public void run() {
                Log.v(TAG, "Execute JSon Query");
//                VolleyJSonRequest request = new VolleyJSonRequest(URL+"weather?q="+URL_ARG+"&appid="+API_KEY, mCallback);
//                request.executeRequestAsString();

                // Implementation with result caching!
                VolleyJSonRequestCached requestCached = new VolleyJSonRequestCached(URL + "weather?q=" + URL_ARG + "&appid=" + API_KEY, mCallback);
                requestCached.executeCachedRequestAsString();

            }
        }, 5000);

        // Wait 10s for ImageQuery
        Handler handlerImage = new Handler();
        handlerImage.postDelayed(new Runnable() {
            public void run() {
                VolleyImageRequest imgRequest = new VolleyImageRequest(URL_IMAGE);
                Log.v(TAG, "Execute Image Query - ImageView (#1)");
                imgRequest.executeImageRequest(binding.imgView);

                imgRequest = new VolleyImageRequest(URL_IMAGE_2);
                Log.v(TAG, "Execute Image Query - NetworkImageView (#2)");
                imgRequest.executeImageRequest(binding.imgNetwork);
            }
        }, 10000);
    }

    @Override
    public void processJSonFinish(ResponseData responseData, String url) {
        Log.i(TAG, "-- Updating JSon Data!");
        viewModel = new ResponseDataViewModel(responseData);
        binding.setViewModel(viewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.getInstance().cancellPendingRequest(MainApplication.DEFAULT_TAG);
    }
}
