package com.openclassrooms.go4launch.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4launch.model.Restaurant;

/**
 * Created by Mohamed GHERBAL (pour OC) on 29/03/2022
 */
public class RestHelper {

    private static final String COLLECTION_NAME = "Restaurant";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getRestCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRest(String restId, String restName, int numUsers) {
        Restaurant restToCreate = new Restaurant(restId, restName, numUsers);
        return RestHelper.getRestCollection().document(restId).set(restToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getRest(String uid) {
        return RestHelper.getRestCollection().document(uid).get();
    }

    public static Query getRestname(String restName) {
        return getRestCollection().whereEqualTo("restName", restName);
    }

    public static Task<DocumentSnapshot> getRestId(String restId) {
        return RestHelper.getRestCollection().document(restId).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateRestName(String restName, String uid) {
        return  RestHelper.getRestCollection().document(uid).update("restName", restName);
    }
    public static Task<Void> updateRestId(String restId, String uid) {
        return  RestHelper.getRestCollection().document(uid).update("restId", restId);
    }
    public static Task<Void> updateNumUsers(int numUsers, String uid) {
        return RestHelper.getRestCollection().document(uid).update("numUsers", numUsers);
    }
}
