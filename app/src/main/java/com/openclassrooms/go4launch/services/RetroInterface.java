package com.openclassrooms.go4launch.services;

import com.firebase.ui.auth.data.model.User;
import com.openclassrooms.go4launch.model.NearByAPIResponse;
import com.openclassrooms.go4launch.model.Result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Mohamed GHERBAL (pour OC) on 13/01/2022
 */
public interface RetroInterface {

    @GET("place/nearbysearch/json")
    Call<NearByAPIResponse> getNearBy(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("keyword") String keyword,
            @Query("key") String key
    );

    /* // USE FOR TEMPLATE //
    @GET("/api/unknown")
    //Call<MultipleResource> doGetListResources();

    @POST("/api/users")
    Call<User> createUser(@Body User user);

    @GET("/api/users?")
    Call<UserList> doGetUserList(@Query("page") String page);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);
    */

}
