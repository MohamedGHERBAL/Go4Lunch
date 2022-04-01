package com.openclassrooms.go4launch.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4launch.model.User;

import java.util.ArrayList;

/**
 * Created by Mohamed GHERBAL (pour OC) on 23/02/2022
 */
public class UserHelper {

    private static final String COLLECTION_NAME = "Workmates";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String name, String email, String urlPicture, String restName, String restId, ArrayList likedRestaurants) {
        User userToCreate = new User(uid, name, email, urlPicture, restName, restId, likedRestaurants);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Query getUsersWithRestname(String restName) {
        return getUsersCollection().whereEqualTo("restName", restName);
    }

    public static Task<DocumentSnapshot> getUserRestId(String restId) {
        return UserHelper.getUsersCollection().document(restId).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String name, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("name", name);
    }
    public static Task<Void> updateRestName(String restName, String uid) {
        return  UserHelper.getUsersCollection().document(uid).update("restName", restName);
    }
    public static Task<Void> updateRestId(String restId, String uid) {
        return  UserHelper.getUsersCollection().document(uid).update("restId", restId);
    }
    public static Task<Void> updateLikedRestaurants(String likedRestaurants, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("likedRestaurants", FieldValue.arrayUnion(likedRestaurants));
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
