package com.openclassrooms.go4launch.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.services.UserHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Mohamed GHERBAL (pour OC) on 29/12/2021
 */
public final class UserRepository {

    // For Log.d
    private static final String TAG = UserRepository.class.getSimpleName();

    // For DATAS
    private static volatile UserRepository instance;
    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<List<User>> filterUsers = new MutableLiveData<>();
    private Result mResult;
    private int howManyPeople = 0;

    // For Firestore
    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";
    private static final String IS_MENTOR_FIELD = "isMentor";

    private static final String COLLECTION_WORKMATES = "Workmates";
    public static final String SELECTED_RESTAURANT_ID_FIELD = "restId";


    // Get the Collection Reference
    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_WORKMATES);
    }

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


    // Get list of Users for Workmates
    public MutableLiveData<List<User>> getUserList() {
        Log.i(TAG, "getUserList");

        CollectionReference usersCollection = UserHelper.getUsersCollection();
        usersCollection.get().addOnCompleteListener(task -> {
            List<User> list = new ArrayList<>();
            if (task.getResult() != null) {
                Log.i(TAG, "getUserList - getResult is not null !");
                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    list.add(documentSnapshot.toObject(User.class));
                }
                users.setValue(list);
            }
        });
        return users;
    }

    public MutableLiveData<List<User>> getFilteredUserList(String restName) {
        final Query docRef = UserHelper.getUsersWithRestname(restName);
        docRef.addSnapshotListener((value, error) -> {
            List<User> list = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(value.getDocuments())) {
                list.add(documentSnapshot.toObject(User.class));
            }
            filterUsers.setValue(list);
        });

        return filterUsers;
    }

    public String howManyPeopleChoseThisRestaurant(ArrayList<User> usersList) {
        for (User user : usersList) {
            if (user.getRestId().equals(mResult.getPlaceId())) {
                howManyPeople++;
            }
        }
        return "(" + howManyPeople + ")";
    }


//    // Create User in Firestore
//    public void createUser() {
//        FirebaseUser user = getCurrentUser();
//        if(user != null){
//            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
//            String username = user.getDisplayName();
//            String uid = user.getUid();
//
//            User userToCreate = new User(uid, username, urlPicture);
//
//            Task<DocumentSnapshot> userData = getUserData();
//            // If the user already exist in Firestore, we get his data (isMentor)
//            userData.addOnSuccessListener(documentSnapshot -> {
//                if (documentSnapshot.contains(IS_MENTOR_FIELD)){
//                    userToCreate.setIsMentor((Boolean) documentSnapshot.get(IS_MENTOR_FIELD));
//                }
//                this.getUsersCollection().document(uid).set(userToCreate);
//            });
//        }
//    }
//
    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
//
//    public Task<Void> signOut(Context context){
//        return AuthUI.getInstance().signOut(context);
//    }
//
//    public Task<Void> deleteUser(Context context){
//        return AuthUI.getInstance().delete(context);
//    }
//
//    // Get User Data from Firestore
//    public Task<DocumentSnapshot> getUserData(){
//        String uid = this.getCurrentUserUID();
//        if(uid != null){
//            return this.getUsersCollection().document(uid).get();
//        }else{
//            return null;
//        }
//    }
//
//    // Update User Username
//    public Task<Void> updateUsername(String username) {
//        String uid = this.getCurrentUserUID();
//        if(uid != null){
//            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
//        }else{
//            return null;
//        }
//    }
//
//    // Update User isMentor
//    public void updateIsMentor(Boolean isMentor) {
//        String uid = this.getCurrentUserUID();
//        if(uid != null){
//            this.getUsersCollection().document(uid).update(IS_MENTOR_FIELD, isMentor);
//        }
//    }

}
