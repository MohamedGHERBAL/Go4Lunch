package com.openclassrooms.go4launch.manager;

import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4launch.repositories.UserRepository;

/**
 * Created by Mohamed GHERBAL (pour OC) on 29/12/2021
 */
public class UserManager {

    private static volatile UserManager instance;
    private UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public Boolean isCurrentUserLogged() {
        return (userRepository.getCurrentUser() != null);
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

}
