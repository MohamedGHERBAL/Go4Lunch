package com.openclassrooms.go4launch.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.repositories.GooglePlacesRepository;
import com.openclassrooms.go4launch.repositories.UserRepository;
import com.openclassrooms.go4launch.services.RetrofitClient;

import java.util.List;

/**
 * Created by Mohamed GHERBAL (pour OC) on 21/01/2022
 */
public class ViewModel extends AndroidViewModel {

    // For Log.i
    private static final String TAG = ViewModel.class.getSimpleName();

    // For DATAS
    //private UserRepository mUserRepository;
    private GooglePlacesRepository mGooglePlacesRepository = GooglePlacesRepository.getInstance();

    // Variables
    double lat;
    double lnt;

    private MutableLiveData<Result> getDetail = new MutableLiveData<>();


    // Constructors
    public ViewModel(Application application) {
        super(application);
        Log.e(TAG, "ViewModel");
    }

    // For MapFragment
    public LiveData<List<Result>> getRestaurants(double lat, double lnt) {
        Log.i(TAG, "getRestaurants lat lnt");

        this.lat = lat;
        this.lnt = lnt;

        return mGooglePlacesRepository.getNearbyRestaurantsLiveData("restaurant", lat, lnt);
    }

    // For ListFragment
    public LiveData<List<Result>> getRestaurants() {
        Log.i(TAG, "getRestaurants");

        return mGooglePlacesRepository.getNearbyRestaurantsLiveData("restaurant", lat, lnt);
    }

    // For DetailRestaurantActivity
    public MutableLiveData<Result> getDetailRestaurant(String placeId) {
        Log.i(TAG, "getDetailRestaurant");

        return mGooglePlacesRepository.getRestaurantForDetail(placeId);
    }

    public MutableLiveData<Result> getDetail(String placeId) {
        Log.e(TAG, "getDetail");

        getDetail = getDetailRestaurant(placeId);
        return getDetail;
    }


}
