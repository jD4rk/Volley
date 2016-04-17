package it.jdark.android.volley;

import android.util.Log;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import it.jdark.android.volley.manager.ImageCacheManager;

/**
 * Created by jDark on 14/04/16.
 */
public class VolleyImageRequest {

    private final String TAG = getClass().getSimpleName();

    private ImageLoader imageLoader;
    private String requestUrl;

    public VolleyImageRequest(String url) {
        requestUrl = url;
    }


    public void executeImageRequest(final ImageView imgView) {
        imageLoader= ImageCacheManager.getInstance().getImageLoader();


        // THERE ARE PLENTY DIFFERENT METHODS TO REQUEST AND SET AN IMAGE
        // - FIRST: allow you to set also a placeHolder and Error Image
        Log.i(TAG, "-- Set ImageView with loading image (#1)");
        imageLoader.get(requestUrl, imageLoader.getImageListener(imgView, R.drawable.ico_loading, R.drawable.ico_error));

        // - SECOND: allow you only to set the Error Image (Not the loading one)
        // The code complexity is also bigger
//        imageLoader.get(requestUrl, new ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                Log.i(TAG, "-- Update ImageView");
//                imgView.setImageBitmap(response.getBitmap());
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Image Load Error: " + error.getMessage());
//                imgView.setImageResource(R.drawable.ico_error);
//            }
//        });

        // THIRD: As se second we can set the Image and the error one
        // This time we use a method form ImageCacheManager (Not so far from the second way)
//        ImageCacheManager.getInstance().getImage(requestUrl, new ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                Log.i(TAG, "-- Update ImageView");
//                imgView.setImageBitmap(response.getBitmap());
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Image Load Error: " + error.getMessage());
//                imgView.setImageResource(R.drawable.ico_error);
//            }
//        });
    }

    public void executeImageRequest(NetworkImageView ntwImgView) {
        imageLoader= ImageCacheManager.getInstance().getImageLoader();

        Log.i(TAG, "-- Set ImageNetworkView with loading image (#2)");
        // Set PlaceHolder and error Image
        imageLoader.get(requestUrl, imageLoader.getImageListener(ntwImgView, R.drawable.ico_loading, R.drawable.ico_error));

        // For "NetworkImageView we have to set image manually with the method "setImageUrl"
        ntwImgView.setImageUrl(requestUrl, imageLoader);

    }
}
