package com.openclassrooms.go4launch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4launch.databinding.ActivityMainBinding;
import com.openclassrooms.go4launch.databinding.NavDrawerHeaderBinding;
import com.openclassrooms.go4launch.manager.UserManager;
import com.openclassrooms.go4launch.ui.fragments.ListFragment;
import com.openclassrooms.go4launch.ui.fragments.MapFragment;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.ui.fragments.WorkmatesFragment;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // For Log.d
    private static final String TAG = MainActivity.class.getSimpleName();

    // For Data
    private UserManager userManager = UserManager.getInstance();

    // For ViewBinding
    private ActivityMainBinding mainBinding;

    // For Design
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    //private BottomNavigationView btm_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);

        init();

        // Show First Fragment
        getFragment(new MapFragment());
    }

    // ------------------------------------------------------
    // INIT
    // ------------------------------------------------------

    // Initialisation
    private void init() {
        this.configureToolBar();
        configureDrawerLayout();
        configureNavigationView();
        this.configureBtnNavView();

        updateUIWithUserData();
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
        // Handle Navigation Item Click
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_drawer_yourlunch:
                break;
            case R.id.nav_drawer_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            case R.id.nav_drawer_logout:
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // ------------------------------------------------------
    // CONFIGURATION
    // ------------------------------------------------------

    // Configure Toolbar
    private void configureToolBar() {
        this.toolbar = (Toolbar) mainBinding.mainToolbar;
        setSupportActionBar(toolbar);
    }

    // Configure DrawerLayout
    private void configureDrawerLayout() {
        this.drawerLayout = (DrawerLayout) mainBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure NavigationView
    public void configureNavigationView() {
        this.navigationView = (NavigationView) mainBinding.activityMainNavView;
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Configure Bottom Navigation View
    private void configureBtnNavView() {
        mainBinding.activityMainBottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.map_view) {
                setTitle(R.string.hello_map_fragment);
                getFragment(new MapFragment());
            } else if (item.getItemId() == R.id.list_view) {
                setTitle(R.string.hello_list_fragment);
                getFragment(new ListFragment());
            } else if (item.getItemId() == R.id.workmates) {
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
        if(userManager.isCurrentUserLogged()){
            FirebaseUser user = userManager.getCurrentUser();

            if(user.getPhotoUrl() != null) {
                loadUserDataInDrawerHeader(user.getPhotoUrl());
            }
        }
    }

    private void loadUserDataInDrawerHeader(Uri profilePictureUrl) {
        Log.d(TAG, "loadUserDataInDrawerHeader");

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        View navHeaderView = navigationView.getHeaderView(0);

        ImageView userImgProfile = (ImageView)navHeaderView.findViewById(R.id.drawer_header_user_picture);
        TextView userName = (TextView)navHeaderView.findViewById(R.id.drawer_header_user_name);
        TextView userEmail = (TextView)navHeaderView.findViewById(R.id.drawer_header_user_email);

        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userImgProfile);

        // Get email & username from User
        FirebaseUser user = null;
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        // Update views with data
        userName.setText(username);
        userEmail.setText(email);

    }

    //------------------------------------------------------
    // FRAGMENTS
    //------------------------------------------------------

    // Show Fragment
    private void getFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment);
        fragmentTransaction.commit();
    }
}