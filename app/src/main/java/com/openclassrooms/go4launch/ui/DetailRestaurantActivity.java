package com.openclassrooms.go4launch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.ActivityDetailRestaurantBinding;
import com.openclassrooms.go4launch.databinding.ScrollingContentBinding;
import com.openclassrooms.go4launch.model.Result;

import com.openclassrooms.go4launch.viewmodel.ViewModel;


public class DetailRestaurantActivity extends AppCompatActivity {

    // For Log.d
    private static final String TAG = DetailRestaurantActivity.class.getSimpleName();

    // For Design
    private View mView;

    // For DataBinding
    private ActivityDetailRestaurantBinding binding;

    // For RecyclerView
    RecyclerView mRecyclerViewWorkmates;

    // For ViewModel
    private ViewModel detailRestaurantViewModel;

    // For ProgressDialog
    private ProgressDialog progressDialog;

    // For DATAS
    private Result result;
    private String pictureURL;
    private String nameRestaurant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        binding = ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
        mView = binding.getRoot();
        setContentView(mView);

        init();

        Intent intent = getIntent();
        String placeId = intent.getStringExtra("place_id");

        detailRestaurantViewModel = new ViewModelProvider(this).get(ViewModel.class);
        detailRestaurantViewModel.getDetail(placeId).observe(this, result -> {
            if (result != null) {
                updateDetailRestaurant(result);
            }
        });
    }

    private void updateDetailRestaurant(Result result) {
        Log.d(TAG, "updateDetailRestaurant");

        if (result.getPhotos() != null && result.getPhotos().size() != 0) {
            Log.d(TAG, "updateDetailRestaurant - on getPhotos");
            this.pictureURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + result.getPhotos().get(0).getPhotoReference()
                    + "&key=AIzaSyCl61H4olbn8hk-whs8j4CYC5KEipU4dcY"; //BuildConfig.API_KEY;
        }

        Glide.with(mView.getContext())
                .load(pictureURL)
                .into(binding.detailsActivityImage);

        binding.scrollcontent.detailsRestaurantNameOfRestaurant.setText(result.getName());
        binding.scrollcontent.detailsRestaurantInformationOfRestaurant.setText(result.getVicinity());
        binding.scrollcontent.ratingBar2.setRating(result.getRating() != null ? result.getRating().byteValue() * 3 / 5f : 0);

        configureCallButton(result);
        configureLikeButton(result);
        configureWebSiteButton(result);
    }

    // For Call Button
    private void configureCallButton(Result result) {
        Log.d(TAG, "configureCallButton");

        binding.scrollcontent.detailRestaurantCallPlaceButton.setOnClickListener(v -> {

            if (result.getInternationalPhoneNumber() != null) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + result.getInternationalPhoneNumber())));
            } else {
                Toast.makeText(DetailRestaurantActivity.this, R.string.no_phone_number, Toast.LENGTH_LONG).show();
            }
        });
    }

    // For Like Button
    private void configureLikeButton(Result result) {
        Log.d(TAG, "configureLikeButton");

        binding.scrollcontent.detailRestaurantFavoritePlaceButton.setOnClickListener(v ->
                Toast.makeText(DetailRestaurantActivity.this, R.string.like_button + result.getName(), Toast.LENGTH_LONG).show());
    }

    // For WebSite Button
    private void configureWebSiteButton(Result result) {
        Log.d(TAG, "configureWebSiteButton");

        binding.scrollcontent.detailRestaurantWebsiteButton.setOnClickListener(view -> {

            if (result.getWebsite() != null) {
                Intent websiteView = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getWebsite()));
                startActivity(websiteView);
            } else {
                Toast.makeText(DetailRestaurantActivity.this, R.string.no_website, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configureCollapsingToolbar() {
        Log.i(TAG, "configureCollapsingToolbar");

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout coll_toolbar = (CollapsingToolbarLayout) binding.toolbarLayout;
        coll_toolbar.setTitle(getTitle());
    }

//    private void configureUI() {
//
//        // ProgressDialog
//        progressDialog = new ProgressDialog(this);
//        progressDialog.show();
//
//        // RecyclerView
//        mRecyclerViewWorkmates = (RecyclerView) binding.scrollcontent.detailRestaurantWorkmatesOfPlaceRecyclerView;
//        //adapter = new AdaptorListViewWorkmates(this, false);
//        //mRecyclerViewWorkmates.setAdapter(adapter);
//        mRecyclerViewWorkmates.setLayoutManager(new LinearLayoutManager(this));
//    }

    private void init() {
        Log.d(TAG, "init");

        this.configureCollapsingToolbar();
    }
}
