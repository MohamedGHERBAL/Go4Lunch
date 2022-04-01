package com.openclassrooms.go4launch.services;

import com.firebase.ui.auth.data.model.User;
import com.openclassrooms.go4launch.BuildConfig;
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

    /**
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("place/nearbysearch/json?sensor=true&key=" + BuildConfig.PLACE_API)
    Call<NearByAPIResponse> getNearbyPlaces(
            @Query("type") String type,
            @Query("location") String location,
            @Query("radius") int radius);

    /*
     *
     *
     */
    @GET("place/details/json?categories=basic&key=" + BuildConfig.PLACE_API)
    Call<NearByAPIResponse> getDetailResult(
            @Query("place_id") String placeId
    );

    @GET("textsearch/json")
    Call<Result> getResultFromTextSearch(@Query("query") String query,
                                         @Query("type") String type,
                                         @Query("key") String key,
                                         @Query("radius") Integer radius,
                                         @Query("maxprice") Integer maxPrice,
                                         @Query("opennow") Boolean openNow);

}
