package com.openclassrooms.go4launch.repositories;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Mohamed GHERBAL (pour OC) on 29/12/2021
 */
public final class UserRepository {

    // For Log.d
    private static final String TAG = UserRepository.class.getSimpleName();

    // For DATAS
    private static volatile UserRepository instance;

    private UserRepository() { }

    public static UserRepository getInstance() {
        Log.d(TAG, "getInstance");

        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


}
