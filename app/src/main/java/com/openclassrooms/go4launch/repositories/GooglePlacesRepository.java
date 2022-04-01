package com.openclassrooms.go4launch.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4launch.model.NearByAPIResponse;
import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.services.RetroInterface;
import com.openclassrooms.go4launch.services.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Mohamed GHERBAL (pour OC) on 31/01/2022
 */
public class GooglePlacesRepository {

    // For Log.d
    private static final String TAG = GooglePlacesRepository.class.getSimpleName();

    // For DATAS
    private static GooglePlacesRepository GOOGLE_PLACES_REPOSITORY;
    private MutableLiveData<Result> mDetailRestaurant = new MutableLiveData<Result>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MutableLiveData<List<Result>> results = new MutableLiveData<>();
    private List<Result> mResultsList = new ArrayList<>();

    // Constructors
    public GooglePlacesRepository() {
    }

    public static GooglePlacesRepository getInstance() {
        Log.d(TAG, "getInstance");

        if (GOOGLE_PLACES_REPOSITORY == null) {
            if (GOOGLE_PLACES_REPOSITORY == null) {
                GOOGLE_PLACES_REPOSITORY = new GooglePlacesRepository();
            }
        }
        return GOOGLE_PLACES_REPOSITORY;
    }

    // For Fragements
    public LiveData<List<Result>> getNearbyRestaurantsLiveData(String type, double latitude, double longitude) {
        Log.d(TAG, "getNearbyRestaurantsLiveData");
        final MutableLiveData<List<Result>> mNearbyRestaurants = new MutableLiveData<>();

        String url = "https://maps.googleapis.com/maps/";

        int PROXIMITY_RADIUS = 10000;

        Retrofit retrofit = RetrofitClient.getClient();
        RetroInterface service = RetrofitClient.createService(RetroInterface.class);
        Call<NearByAPIResponse> call = service.getNearbyPlaces(type, latitude + "," + longitude, PROXIMITY_RADIUS);
        call.enqueue(new Callback<NearByAPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearByAPIResponse> call, @NonNull Response<NearByAPIResponse> response) {

                for (int i = 0; i < response.body().getResults().size(); i++) {
                    Log.i(TAG, "getNearbyRestaurantsLiveData FOR");

                    Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                    Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();

                    String placeName = response.body().getResults().get(i).getName();
                    String vicinity = response.body().getResults().get(i).getVicinity();

                    LatLng latLng = new LatLng(lat, lng);

                    int numUsers = response.body().getResults().get(i).getNumUsers();
                }
                mNearbyRestaurants.setValue(mResultsList);
                mNearbyRestaurants.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<NearByAPIResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
                mNearbyRestaurants.postValue(null);
            }
        });
        return mNearbyRestaurants;
    }

    // For DetailRestaurantActivity
    public MutableLiveData<Result> getRestaurantForDetail(String placeId) {
        Log.d(TAG, "getRestaurantForDetail");
        Log.i(TAG, "PlaceID = " + placeId);

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = RetrofitClient.getClient();
        RetroInterface service = RetrofitClient.createService(RetroInterface.class);

        Call<NearByAPIResponse> call = service.getDetailResult(placeId);
        call.enqueue(new Callback<NearByAPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearByAPIResponse> call, @NonNull Response<NearByAPIResponse> response) { ;
                mDetailRestaurant.postValue(response.body().getResult());

                if (response.body() != null) {
                    Log.d(TAG, "getRestaurantForDetail response.body is not null !");
                    mDetailRestaurant.setValue(response.body().getResult());
                }
            }

            @Override
            public void onFailure(Call<NearByAPIResponse> call, Throwable t) {
                mDetailRestaurant.postValue(null);
            }
        });
        Log.d(TAG, "getRestaurantForDetail the end !");
        return mDetailRestaurant;
    }

}
