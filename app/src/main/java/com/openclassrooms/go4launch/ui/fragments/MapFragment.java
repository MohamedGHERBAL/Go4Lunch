package com.openclassrooms.go4launch.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.go4launch.BuildConfig;
import com.openclassrooms.go4launch.R;

import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.repositories.ApplicationData;
import com.openclassrooms.go4launch.services.UserHelper;
import com.openclassrooms.go4launch.ui.DetailRestaurantActivity;
import com.openclassrooms.go4launch.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    // For Log.d
    public static final String TAG = MapFragment.class.getSimpleName();

    //FragmentMapBinding binding;

    private GoogleMap mMap;
    private static double latitude;
    private static double longitude;

    // For Viewmodel
    private ViewModel restaurantsViewModel;

    private int PROXIMITY_RADIUS = 10000;

    private Map<Marker, Result> markerMap = new HashMap<Marker, Result>();

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager = null;

    private FloatingActionButton fab;

    private static final int REQUEST_CODE = 101;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //binding = FragmentMapBinding.inflate(inflater, container, false);
        //return binding.getRoot();

        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.fragment_map, null, false);

        getUsers();
        this.configurePlaceAuto();

        fab = (FloatingActionButton) view.findViewById(R.id.fab_mylocation);

        locationManager = (LocationManager)
                Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            fetchLocation();
        } else {
            String locationProviders = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (locationProviders == null || locationProviders.equals("")) {
                alert();
            }
        }

        fab.setOnClickListener(view1 -> {
            Log.i(TAG, "FAB setOnClickListener is clicked !");
            fetchLocation();
        });

        return view;
    }


    public void alert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        builder.setTitle("Attention")
                .setMessage("To use Go4Lunch you need to enable GPS first to use the app properly, do you want to activate it ?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // If you choose 'No' the app do nothing.
                })
                .show();
    }


    private void fetchLocation() {
        Log.i(TAG, "fetchLocation");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "fetchLocation if");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            return;
        }

        Log.i(TAG, "fetchLocation 2");

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                Log.i(TAG, "fetchLocation 2 if");
                currentLocation = location;
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Toast.makeText(getContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MapFragment.this);

                this.getCurrentLocation();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");

        restaurantsViewModel = ViewModelProviders.of(requireActivity()).get(ViewModel.class);
    }

    public void getCurrentLocation() {
        LocationManager locationManager = (LocationManager)
                Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        currentLoc(locationManager);
    }

    public void currentLoc(LocationManager locationManager) {
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_LOCATION = 1;
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
        android.location.Location location = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager
                .getBestProvider(criteria, false)));
        if (location !=  null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    private void getUsers() {
        try {
            CollectionReference usersCollection = UserHelper.getUsersCollection();
            usersCollection.get().addOnCompleteListener(task -> {
                List<User> list = new ArrayList<>();
                if (task.getResult() != null) {
                    Log.i(TAG, "getUserList - getResult is not null !");
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        list.add(documentSnapshot.toObject(User.class));
                    }
                    ApplicationData.getInstance().setmUsers((ArrayList<User>) list);
                }
            });

        } catch (Exception e) {
            Log.d("onResponse : ", "There is an error");
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG,"onMapReady");
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Change MAP Design

        // Disable Map Toolbar and zoom buttons:
        mMap.getUiSettings().setMapToolbarEnabled(false);

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Vous êtes ici !");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        googleMap.addMarker(markerOptions);

        restaurantsViewModel.getRestaurants(latLng.latitude, latLng.longitude).observe(this, results -> {
            if (results != null) {
                Log.i(TAG, "getRestaurants - result is not null");

                try {
                    Log.i(TAG, "getRestaurants TRY");
                    mMap.clear(); // Remove the marker from currentLocation on the map

                    // This loop will go through all the results and add marker on each location.
                    Log.i(TAG, "size of getResults : " + results.size());
                    for (int i = 0; i < results.size(); i++) {

                        Log.i(TAG, "getRestaurants TRY FOR");
                        Double lat = results.get(i).getGeometry().getLocation().getLat();
                        Double lng = results.get(i).getGeometry().getLocation().getLng();

                        String placeName = results.get(i).getName();
                        String vicinity = results.get(i).getVicinity();
                        MarkerOptions markerRestaurantsOptions = new MarkerOptions();
                        LatLng latLngRestaurants = new LatLng(lat, lng);

                        // Position of Marker on Map
                        markerRestaurantsOptions.position(latLngRestaurants);

                        // Adding Title to the Marker
                        markerRestaurantsOptions.title(placeName + " : " + vicinity);

                        // Adding Marker to the Camera.
                        Marker m = mMap.addMarker(markerRestaurantsOptions);

                        // Adding colour to the marker
                        markerRestaurantsOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        if (results != null) {
                            mMap.clear();
                            for (Result result : results) {
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()))
                                        .title(result.getName()));

                                int numUsers = 0;
                                for (User user : ApplicationData.getInstance().getmUsers()) {
                                    if (user.getRestId().equals(result.getPlaceId())) {
                                        numUsers++;
                                    }
                                }
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(numUsers > 0 ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED));

                                markerMap.put(marker, result);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }
        });

        /*
         OnClick Listener for Markers info windows on the map.
         Clicking will open a new activity with details of the marker.
        */
        mMap.setOnInfoWindowClickListener(marker -> {
            Log.i(TAG, "setOnInfoWindowClickListener");

            Result result = markerMap.get(marker);
            String markerId = result.getPlaceId();
            Log.i(TAG, "getPlaceId = " + markerId);

            Intent details = new Intent(getContext(), DetailRestaurantActivity.class);
            details.putExtra("place_id", markerId);
            getActivity().startActivity(details);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //fetchLocation();
            }
        }
    }

    // Init Place Autocomplete
    protected void configurePlaceAuto() {
        Places.initialize(getContext(), BuildConfig.PLACE_API);
    }

}