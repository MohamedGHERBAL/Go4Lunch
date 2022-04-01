package com.openclassrooms.go4launch.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.openclassrooms.go4launch.BuildConfig;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.ActivityDetailRestaurantBinding;
import com.openclassrooms.go4launch.model.Result;

import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.services.CurrentUser;
import com.openclassrooms.go4launch.services.RestHelper;
import com.openclassrooms.go4launch.services.UserHelper;
import com.openclassrooms.go4launch.ui.adapter.ListAdapter;
import com.openclassrooms.go4launch.ui.adapter.WorkmatesAdapter;
import com.openclassrooms.go4launch.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;


public class DetailRestaurantActivity extends AppCompatActivity {

    // For Log.d
    private static final String TAG = DetailRestaurantActivity.class.getSimpleName();

    // For DataBinding
    private ActivityDetailRestaurantBinding binding;

    // For Design
    private View mView;

    // For Adapter
    WorkmatesAdapter mAdapter;

    // For RecyclerView
    RecyclerView mRecyclerViewWorkmates;

    // For ViewModel
    private ViewModel detailRestaurantViewModel;

    // For DATAS
    private String pictureURL;
    private String restName;
    private List<User> mWorkmates = new ArrayList<>();

    private int numUsers = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // Binding View
        binding = ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
        mView = binding.getRoot();
        setContentView(mView);

        configToolbar();

        // RecyclerView Layout ID
        mRecyclerViewWorkmates = binding.detailRestaurantWorkmatesOfPlaceRecyclerView;

        // Get placeId from previous activity
        Intent intent = getIntent();
        String placeId = intent.getStringExtra("place_id");

        getDetailANDWorkmates(placeId);
        setupAdapter();
    }

    // ------------------------------------------------------
    // CONFIGURATION
    // ------------------------------------------------------

    // Setup Adapter
    private void setupAdapter() {
        if (mRecyclerViewWorkmates != null) {
            Context context = mView.getContext();
            RecyclerView recyclerView = mRecyclerViewWorkmates;
            int mColumnCount = 1;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.mAdapter = new WorkmatesAdapter(this.mWorkmates);
            recyclerView.setAdapter(this.mAdapter);
        }

        assert mRecyclerViewWorkmates != null;
        mRecyclerViewWorkmates.setHasFixedSize(true);
        mRecyclerViewWorkmates.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewWorkmates.setAdapter(mAdapter);
    }

    // Get Detail Info and Workmates List
    private void getDetailANDWorkmates(String placeId) {
        detailRestaurantViewModel = new ViewModelProvider(this).get(ViewModel.class);
        detailRestaurantViewModel.getDetail(placeId).observe(this, result -> {
            if (result != null) {
                updateDetailRestaurant(result);

                // G5
                detailRestaurantViewModel = new ViewModelProvider(this).get(ViewModel.class);
                detailRestaurantViewModel.getFilteredListOfUsers(restName).observe(this, users -> {
                    if (users != null) {
                        updateUIWithWorkmates(users);
                    }
                });
            }
        });
    }

    // ------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------

    // update Restaurant's Detail and config Buttons
    private void updateDetailRestaurant(Result result) {
        Log.d(TAG, "updateDetailRestaurant");

        restName = result.getName();

        if (result.getPhotos() != null && result.getPhotos().size() != 0) {
            Log.d(TAG, "updateDetailRestaurant - on getPhotos");
            this.pictureURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                    + result.getPhotos().get(0).getPhotoReference()
                    + "&key=" + BuildConfig.PLACE_API;
        }

        Glide.with(mView.getContext())
                .load(pictureURL)
                .into(binding.detailsActivityImage);

        binding.detailsRestaurantNameOfRestaurant.setText(restName);
        binding.detailsRestaurantInformationOfRestaurant.setText(result.getVicinity());
        binding.ratingBar2.setRating(result.getRating() != null ? result.getRating().byteValue() * 3 / 5f : 0);

        configureCallButton(result);
        configureLikeButton(result);
        configureWebSiteButton(result);

        configFloatingActionButton(result);
    }

    // update UI workmates list
    private void updateUIWithWorkmates(List<User> workmates) {
        Log.d(TAG, "updateUIWithWorkmates");

        this.mWorkmates.clear();
        this.mWorkmates.addAll(workmates);
        this.mAdapter.notifyDataSetChanged();
    }

    // ------------------------------------------------------
    // UI
    // ------------------------------------------------------

    // For Toolbar and Arrow BackButton
    private void configToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24dp);

        // Add back arrow to Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    // For FloatingActionButton
    private void configFloatingActionButton(Result result) {
        Log.d(TAG, "configFloatingActionButton");

        FloatingActionButton floatingActionButton = binding.fab;

        User currentUser = CurrentUser.getInstance();
        if (result.getPlaceId().equals(currentUser.getRestId())) {
            floatingActionButton.setImageResource(R.drawable.ic_baseline_green_pin_24dp);
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);
        }

        floatingActionButton.setOnClickListener(v -> {
            Log.d(TAG, "configFloatingActionButton -> floatingActionButton");

            if (!result.getPlaceId().equals(currentUser.getRestId())) {
                Log.d(TAG, "floatingActionButton -> IF");
                floatingActionButton.setImageResource(R.drawable.ic_baseline_green_pin_24dp);

                detailRestaurantViewModel.updateCurrentUser(result.getName(), result.getPlaceId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                //RestHelper.createRest(result.getPlaceId(), result.getName(), result.setNumUsers(result.getNumUsers()) + 1);
                detailRestaurantViewModel.increaseResultsNumUsers(result.getPlaceId());

            } else {
                Log.d(TAG, "configFloatingActionButton -> floatingActionButton -> ELSE");
                floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);

                detailRestaurantViewModel.updateCurrentUser("", "", FirebaseAuth.getInstance().getCurrentUser().getUid());
                //RestHelper.createRest(result.getPlaceId(), result.getName(), result.getNumUsers());
                detailRestaurantViewModel.decreaseResultsNumUsers(result.getPlaceId());
            }
        });
    }

    // For Call Button
    private void configureCallButton(Result result) {
        Log.d(TAG, "configureCallButton");

        binding.detailRestaurantCallPlaceButton.setOnClickListener(v -> {

            if (result.getInternationalPhoneNumber() != null) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + result.getInternationalPhoneNumber())));
            } else {
                Snackbar.make(binding.detailRestaurantLayout, R.string.no_phone_number, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // For Like Button
    private void configureLikeButton(Result result) {
        Log.d(TAG, "configureLikeButton");

        binding.detailRestaurantFavoritePlaceButton.setOnClickListener(v -> {

            UserHelper.updateLikedRestaurants(result.getPlaceId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
            Snackbar.make(binding.detailRestaurantLayout, R.string.like_button + result.getName(), Snackbar.LENGTH_SHORT).show();
        });
    }

    // For WebSite Button
    private void configureWebSiteButton(Result result) {
        Log.d(TAG, "configureWebSiteButton");

        binding.detailRestaurantWebsiteButton.setOnClickListener(view -> {

            if (result.getWebsite() != null) {
                Intent websiteView = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getWebsite()));
                startActivity(websiteView);
            } else {
                Snackbar.make(binding.detailRestaurantLayout, R.string.no_website, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // ------------------------------------------------------
    // OTHERS
    // ------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        // When BACK BUTTON is pressed, the activity on the stack is restarted
        // Do what you want on the refresh procedure here
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // TODO: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
