package com.openclassrooms.go4launch.repositories;

import com.openclassrooms.go4launch.model.User;

import java.util.ArrayList;

/**
 * Created by Mohamed GHERBAL (pour OC) on 31/03/2022
 */
public class ApplicationData {

    private static ApplicationData instance = null;
    private ArrayList<User> mUsers = new ArrayList<>();

     public static ApplicationData getInstance() {
         if (instance == null) {
             instance = new ApplicationData();
         }
         return instance;
     }

    public ArrayList<User> getmUsers() {
        return mUsers;
    }

    public void setmUsers(ArrayList<User> mUsers) {
        this.mUsers = mUsers;
    }
}
