package com.openclassrooms.go4launch.model;

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

    //
    public User() {
        // For Firestore
    }

    /**
     * Constructor
     * @param uid
     * @param name
     * @param email
     * @param urlPicture
     */
    public User(String uid, String name, String email, String urlPicture) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
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

}
