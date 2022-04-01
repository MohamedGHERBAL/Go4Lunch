package com.openclassrooms.go4launch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.go4launch.BuildConfig;
import com.openclassrooms.go4launch.databinding.ActivityMainBinding;
import com.openclassrooms.go4launch.manager.UserManager;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.services.CurrentUser;
import com.openclassrooms.go4launch.services.UserHelper;
import com.openclassrooms.go4launch.ui.fragments.ListFragment;
import com.openclassrooms.go4launch.ui.fragments.MapFragment;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.ui.fragments.WorkmatesFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // For Log.d
    private static final String TAG = MainActivity.class.getSimpleName();

    // For Data
    private static final int RC_SIGN_IN = 123;
    private static final int SIGN_OUT_TASK = 10;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    // For ViewBinding
    private ActivityMainBinding mainBinding;

    // For Design
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    // Google Places API
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    // For Firebase UserManager
    private final UserManager userManager = UserManager.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        this.startInit();
    }

    // ------------------------------------------------------
    // INIT
    // ------------------------------------------------------

    // Initialisation
    private void startInit() {
        Log.i(TAG, "startInit");

        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User currentUser = documentSnapshot.toObject(User.class);
            CurrentUser.set_instance(currentUser);
        });

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);

        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBtnNavView();
        this.configurePlaceAuto();

        updateUIWithUserData();

        // Show First Fragment
        getFragment(new MapFragment());
    }

    @Override
    public void onBackPressed() {
        // Handle back click to close Drawer Menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected");

        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_drawer_yourlunch:
                String placeId = CurrentUser.getInstance().getRestId();
                Context context = getApplicationContext();

                if (!placeId.equals("")) {
                    Intent details = new Intent(context, DetailRestaurantActivity.class);
                    details.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    details.putExtra("place_id", placeId);
                    context.startActivity(details);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.No_restaurant_selected_yet, Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.nav_drawer_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.nav_drawer_logout:
                FirebaseAuth.getInstance().signOut();
                //signOutUserFromFirebase();
                startActivity(new Intent(this, LoginActivity.class));
                //finish();
                return true;

            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ------------------------------------------------------
    // CONFIGURATION
    // ------------------------------------------------------

    // Init Place Autocomplete
    protected void configurePlaceAuto() {
        Places.initialize(getApplicationContext(), BuildConfig.PLACE_API);
    }

    // Configure Toolbar
    private void configureToolBar() {
        Log.d(TAG, "configureToolBar");

        this.toolbar = mainBinding.mainToolbar;
        setSupportActionBar(toolbar);
    }

    // Configure DrawerLayout
    private void configureDrawerLayout() {
        Log.d(TAG, "configureDrawerLayout");

        this.drawerLayout = mainBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure NavigationView
    public void configureNavigationView() {
        Log.d(TAG, "configureNavigationView");

        this.navigationView = mainBinding.activityMainNavView;
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Configure Bottom Navigation View
    private void configureBtnNavView() {
        Log.d(TAG, "configureBtnNavView");

        mainBinding.activityMainBottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.map_view) {
                item.setChecked(true);
                setTitle(R.string.hello_map_fragment);
                getFragment(new MapFragment());

            } else if (item.getItemId() == R.id.list_view) {
                item.setChecked(true);
                setTitle(R.string.hello_list_fragment);
                getFragment(new ListFragment());

            } else if (item.getItemId() == R.id.workmates) {
                item.setChecked(true);
                setTitle(R.string.hello_workmates_fragment);
                getFragment(new WorkmatesFragment());
            }
            return false;
        });
    }

    //------------------------------------------------------
    // UI
    //------------------------------------------------------

    public void updateUIWithUserData() {
        Log.d(TAG, "updateUIWithUserData");

        if (userManager.isCurrentUserLogged()) {
            loadUserDataInDrawerHeader();
        }
    }

    private void loadUserDataInDrawerHeader() {
        Log.d(TAG, "loadUserDataInDrawerHeader");

        FirebaseUser user = userManager.getCurrentUser();

        NavigationView navView = mainBinding.activityMainNavView;
        ImageView img = navView.getHeaderView(0).findViewById(R.id.drawer_header_user_picture);
        TextView userName = navView.getHeaderView(0).findViewById(R.id.drawer_header_user_name);
        TextView userEmail = navView.getHeaderView(0).findViewById(R.id.drawer_header_user_email);


        if (user.getPhotoUrl() != null) {

            // Get picture URL from Firebase
            if (getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(img);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_baseline_person_24)
                        .apply(RequestOptions.circleCropTransform())
                        .into(img);

            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : getCurrentUser().getDisplayName();

            //Update views with data
            userName.setText(username);
            userEmail.setText(email);
        }
    }

    //------------------------------------------------------
    // FRAGMENTS
    //------------------------------------------------------

    // Show Fragment
    private void getFragment(Fragment fragment) {
        Log.d(TAG, "getFragment");

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    //------------------------------------------------------
    // OTHERS
    //------------------------------------------------------

    private void createUserInFirestore() {
        Log.d(TAG, "createUserInFirestore");

        UserHelper.getUsersCollection().document(getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot doc = task.getResult();
                if(!doc.exists()){
                    String urlPicture = (getCurrentUser().getPhotoUrl() != null) ? getCurrentUser().getPhotoUrl().toString() : null;
                    String name = getCurrentUser().getDisplayName();
                    String uid = getCurrentUser().getUid();
                    String email = getCurrentUser().getEmail();
                    String restName = "";
                    String restId = "";
                    ArrayList<String> likedRestaurant = new ArrayList<>();

                    UserHelper.createUser(uid, name, email, urlPicture, restName, restId, likedRestaurant);
                }
            }
        });

    }

    @Nullable
    protected static FirebaseUser getCurrentUser() {
        Log.d(TAG, "getCurrentUser");
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // LOG OUT
    private void signOutUserFromFirebase() {
        Log.d(TAG, "signOutUserFromFirebase");

        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        Log.d(TAG, "updateUIAfterRESTRequestsCompleted");

        return aVoid -> {
            switch (origin) {
                // h - Hiding Progress bar after request completed

                case SIGN_OUT_TASK:
                    return;

                default:
                    break;
            }
        };
    }

}