package it.jdark.android.volley.viewModel;

import it.jdark.android.volley.pojo.ResponseData;

/**
 * Created by jDark on 14/04/16.
 */
public class ResponseDataViewModel {

    private ResponseData data;

    public ResponseDataViewModel(ResponseData data) {
        this.data = data;
    }

    public String getName() {
        return data == null ? "" : data.getName();
//        return data.getName();
    }

    public String getStatus() {
        return data == null ? "" : data.getWeather().get(0).getDescription();
//        return data.getWeather().get(0).getDescription();
    }

    public String getHumidity() {
        return data == null ? "" : data.getMain().getHumidity().toString();
//        return data.getMain().getHumidity().toString();
    }

    public String getPressure() {
        return data == null ? "" : data.getMain().getPressure().toString();
//        return data.getMain().getPressure().toString();
    }

    public ResponseData getData() {
        return data == null ? null : data;
    }
}
