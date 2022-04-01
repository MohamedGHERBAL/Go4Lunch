package com.openclassrooms.go4launch.model;

import java.util.ArrayList;

/**
 * Created by Mohamed GHERBAL (pour OC) on 27/01/2022
 */
public class User {

    /** UID */
    private String uid;

    /** Name */
    private String name;

    /** Email */
    private String email;

    /** URL Picture */
    private String urlPicture;

    /** Restaurant ID */
    private String restId;

    /** Restaurant Name */
    private String restName;

    /** Liked Restaurants */
    private ArrayList<String> likedRestaurants;


    public User() { }

    /**
     * Constructor
     * @param uid
     * @param name
     * @param email
     * @param urlPicture
     * @param restId
     * @param restName
     * @param likedRestaurants
     */
    public User(String uid, String name, String email, String urlPicture, String restId, String restName, ArrayList<String> likedRestaurants) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
        this.restId = restId;
        this.restName = restName;
        this.likedRestaurants = likedRestaurants;
    }

    //---

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlPicture() {
        return urlPicture;
    }
    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getRestId() { return restId; }
    public void setRestId(String restId) { this.restId = restId; }

    public String getRestName() { return restName; }
    public void setRestName(String restName) { this.restName = restName; }

    public ArrayList<String> getLikedRestaurants() { return likedRestaurants; }
    public void setLikedRestaurants(ArrayList<String> likedRestaurants) { this.likedRestaurants = likedRestaurants; }

}
