package com.openclassrooms.go4launch.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.go4launch.R;

import com.openclassrooms.go4launch.model.NearByAPIResponse;
import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.services.RetroInterface;
import com.openclassrooms.go4launch.services.RetrofitClient;
import com.openclassrooms.go4launch.ui.DetailRestaurantActivity;
import com.openclassrooms.go4launch.ui.ProfileActivity;
import com.openclassrooms.go4launch.ui.adapter.ListAdapter;
import com.openclassrooms.go4launch.viewmodel.ViewModel;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    // For Log.d
    public static final String TAG = MapFragment.class.getSimpleName();

    //FragmentMapBinding binding;

    private GoogleMap mMap;
    double latitude;
    double longitude;

    // For Viewmodel
    private ViewModel restaurantsViewModel;

    private Boolean isOpenOnly;
    private Integer maxPrice;
    private Integer radius;
    private int PROXIMITY_RADIUS = 10000;

    private Map<Marker, Result> markerMap = new HashMap<Marker, Result>();

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private FloatingActionButton fab;

    private static final int REQUEST_CODE = 101;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //binding = FragmentMapBinding.inflate(inflater, container, false);
        //return binding.getRoot();

        /*
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);
        */

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_map, null, false);

        fab = (FloatingActionButton) view.findViewById(R.id.fab_mylocation);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLocation();
        build_retrofit_and_get_response("restaurant");

        fab.setOnClickListener(view1 -> {
            Log.e(TAG, "FAB setOnClickListener is clicked !");
            fetchLocation();
        });

        return view;
    }


    private void fetchLocation() {
        Log.e(TAG, "fetchLocation");
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "fetchLocation if");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Log.e(TAG, "fetchLocation 2");

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                Log.e(TAG, "fetchLocation 2 if");
                currentLocation = location;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(getContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MapFragment.this);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        restaurantsViewModel = ViewModelProviders.of(requireActivity()).get(ViewModel.class);
    }

    private void observeViewModel(ViewModel viewModel) {
        Log.e(TAG, "observeViewModel");

        // Update the list when the data changes

        //viewModel.getRestaurantsListObservable()
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Change MAP Type

        Log.e(TAG,"onMapReady");

        //Disable Map Toolbar and zoom buttons:
        mMap.getUiSettings().setMapToolbarEnabled(false);
        //mMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Vous êtes ici !");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);

        //build_retrofit_and_get_response("restaurant");
        restaurantsViewModel.getRestaurants(latLng.latitude, latLng.longitude).observe(this, results -> {
            if (results != null) {
                Log.e(TAG, "observeViewModel - result is not null");
                //fragmentListBinding.setIsLoading(false);
                //ListAdapter.restaurantViewHolder.updateWithNearbySearch();

                try {
                    Log.e(TAG, "build_retrofit_and_get_response TRY");
                    //mMap.clear(); // Remove the marker from currentLocation on the map

                    // This loop will go through all the results and add marker on each location.
                    Log.e(TAG, "size of getResults : " + results.size());
                    for (int i = 0; i < results.size(); i++) {

                        Log.e(TAG, "build_retrofit_and_get_response TRY FOR");
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
                            //mMap.clear();
                            for (Result result : results) {
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()))
                                        .title(result.getName()));
                                //marker.setIcon(BitmapDescriptorFactory.fromResource(result.getNumUsers() > 0 ? R.drawable.marker_green : R.drawable.marker_red));
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
            Log.i(TAG, "onInfoWindowClick");

            Result result = markerMap.get(marker);
            String markerId = result.getPlaceId();
            Log.i(TAG, "getPlaceId = " + markerId);

            Intent details = new Intent(getContext(), DetailRestaurantActivity.class);
            details.putExtra("place_id", markerId);
            getActivity().startActivity(details);
        });
    }

    private void build_retrofit_and_get_response(String type) {
        Log.e(TAG, "build_retrofit_and_get_response");

        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = RetrofitClient.getClient();
        RetroInterface service = RetrofitClient.createService(RetroInterface.class);
        Call<NearByAPIResponse> call = service.getNearbyPlaces(type, latitude + "," + longitude, PROXIMITY_RADIUS);
        call.enqueue(new Callback<NearByAPIResponse>() {

            @Override
            public void onResponse(Call<NearByAPIResponse> call, Response<NearByAPIResponse> response) {
                try {
                    Log.e(TAG, "build_retrofit_and_get_response TRY");
                    //mMap.clear(); // Remove the marker from currentLocation on the map

                    // This loop will go through all the results and add marker on each location.
                    Log.e(TAG, "size of getResults : " + response.body().getResults().size());
                    for (int i = 0; i < response.body().getResults().size(); i++) {

                        Log.e(TAG, "build_retrofit_and_get_response TRY FOR");
                        Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                        Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();

                        String placeName = response.body().getResults().get(i).getName();
                        String vicinity = response.body().getResults().get(i).getVicinity();
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(lat, lng);

                        // Position of Marker on Map
                        markerOptions.position(latLng);

                        // Adding Title to the Marker
                        markerOptions.title(placeName + " : " + vicinity);

                        // Adding Marker to the Camera.
                        Marker m = mMap.addMarker(markerOptions);

                        // Adding colour to the marker
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<NearByAPIResponse> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //fetchLocation();
            }
        }
    }
}