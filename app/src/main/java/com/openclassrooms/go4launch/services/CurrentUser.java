package com.openclassrooms.go4launch.services;

import com.openclassrooms.go4launch.model.User;


/**
 * Created by Mohamed GHERBAL (pour OC) on 02/03/2022
 */
public class CurrentUser extends User {

    private static User _instance = null;

    public static void set_instance(User currentUser) {
        _instance = currentUser;
    }

    public static User getInstance() {
        return _instance;
    }

}
