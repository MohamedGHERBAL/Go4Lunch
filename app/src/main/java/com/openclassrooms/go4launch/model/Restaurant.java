package com.openclassrooms.go4launch.model;

/**
 * Created by Mohamed GHERBAL (pour OC) on 29/03/2022
 */
public class Restaurant {

    /** UID */
    //private String uid;

    /** Restaurant ID */
    private String restId;

    /** Restaurant Name */
    private String restName;

    /** Number of Users */
    private int numUsers;


    public Restaurant() { }

    public Restaurant(String restId, String restName, int numUsers) {
        this.restId = restId;
        this.restName = restName;
        this.numUsers = numUsers;
    }


    // --- GETTER ---
/*    public String getUid() {
        return uid;
    }*/

    public String getRestId() {
        return restId;
    }

    public String getRestName() {
        return restName;
    }

    public int getNumUsers() {
        return numUsers;
    }


    // --- SETTER ---
/*    public void setUid(String uid) {
        this.uid = uid;
    }*/

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

}
