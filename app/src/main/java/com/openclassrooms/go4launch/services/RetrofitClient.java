package com.openclassrooms.go4launch.services;

import android.icu.text.UnicodeSetIterator;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openclassrooms.go4launch.model.NearByAPIResponse;
import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.viewmodel.ViewModel;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

/**
 * Created by Mohamed GHERBAL (pour OC) on 13/01/2022
 */
public class RetrofitClient {

    // For Log.d
    private static final String TAG = RetrofitClient.class.getSimpleName();

    private GoogleMap mMap;
    double latitude;
    double longitude;
    private int PROXIMITY_RADIUS = 10000;

    private static Retrofit retrofit = null;
    private static RetrofitClient retrofitClient;
    private static String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static final String KEY = "AIzaSyCl61H4olbn8hk-whs8j4CYC5KEipU4dcY";

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static <S> S createService(Class<S> retrofitAPI) {
        return retrofit.create(retrofitAPI);
    }

    public synchronized static RetrofitClient getInstance() {
        Log.e(TAG, "getInstance");
        if (retrofitClient == null) {
            if (retrofitClient == null) {
                retrofitClient = new RetrofitClient();
            }
        }
        return retrofitClient;
    }

}
