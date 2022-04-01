package com.openclassrooms.go4launch.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.repositories.GooglePlacesRepository;
import com.openclassrooms.go4launch.repositories.UserRepository;
import com.openclassrooms.go4launch.services.CurrentUser;
import com.openclassrooms.go4launch.services.RetrofitClient;
import com.openclassrooms.go4launch.services.UserHelper;

import java.util.List;

/**
 * Created by Mohamed GHERBAL (pour OC) on 21/01/2022
 */
public class ViewModel extends AndroidViewModel {

    // For Log.i - Log.d
    private static final String TAG = ViewModel.class.getSimpleName();

    // For DATAS
    private UserRepository mUserRepository = UserRepository.getInstance();
    private static GooglePlacesRepository mGooglePlacesRepository = GooglePlacesRepository.getInstance();

    private MutableLiveData<Result> getDetail = new MutableLiveData<>();
    private MutableLiveData<List<User>> getListUser = new MutableLiveData<>();

    // Variables
    double lat;
    double lnt;

    // Constructors
    public ViewModel(Application application) {
        super(application);
        Log.i(TAG, "ViewModel");
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
        Log.i(TAG, "getDetail");

        getDetail = getDetailRestaurant(placeId);
        return getDetail;
    }

    public MutableLiveData<List<User>> getFilteredListOfUsers(String restName) {
        return loadFilteredUserData(restName);
    }

    private MutableLiveData<List<User>> loadFilteredUserData(String restName) {
        return mUserRepository.getFilteredUserList(restName);
    }

    public void increaseResultsNumUsers(String currentRestId) {
        mGooglePlacesRepository.increaseResultsNumUsers(currentRestId);
    }

    public void decreaseResultsNumUsers(String currentRestId) {
        Log.i(TAG, "decreaseResultsNumUsers");

        mGooglePlacesRepository.decreaseResultsNumUsers(currentRestId);
    }

    public void updateCurrentUser(String restName, String restId, String uid) {
        Log.i(TAG, "updateCurrentUser");

        UserHelper.updateRestName(restName, uid);
        UserHelper.updateRestId(restId, uid);
        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User currentUser = documentSnapshot.toObject(User.class);
            CurrentUser.set_instance(currentUser);
        });
    }

    // For WorkmatesFragment
    public MutableLiveData<List<User>> getUsersList() {
        Log.i(TAG, "getUsersList");

        getListUser = getListUser();
        return getListUser;
    }

    public MutableLiveData<List<User>> getListUser() {
        Log.i(TAG, "getListUser");

        return mUserRepository.getUserList();
    }


}
