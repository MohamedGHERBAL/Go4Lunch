package com.openclassrooms.go4launch.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.go4launch.BuildConfig;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.FragmentListBinding;
import com.openclassrooms.go4launch.databinding.FragmentListItemBinding;
import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.repositories.ApplicationData;
import com.openclassrooms.go4launch.services.UserHelper;
import com.openclassrooms.go4launch.ui.adapter.ListAdapter;
import com.openclassrooms.go4launch.viewmodel.ViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListFragment extends Fragment {

    // For Log.d
    public static final String TAG = ListFragment.class.getSimpleName();

    // For Design RecyclerView
    @BindView(R.id.recycle_view_list)
    RecyclerView recyclerView;

    // For DataBinding
    private FragmentListBinding fragmentListBinding;
    private FragmentListItemBinding fragmentListItemBinding;

    // For Viewmodel
    private ViewModel restaurantsViewModel;

    // For Adapter
    private ListAdapter listAdapter;

    // For DATAS
    private List<Result> mResults = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();

    double latitude;
    double longitude;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private int mColumnCount = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        configurePlaceAuto();
        getUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        // Inflate the layout for this fragment
        //this.fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        latitude = MapFragment.getLatitude();
        longitude = MapFragment.getLongitude();
        Log.i(TAG, "MapFragment getLatitude() + getLongitude(): " +latitude +longitude);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Log.i(TAG, "onCreateView - IF");

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.listAdapter = new ListAdapter(this.mResults, latitude, longitude);

            recyclerView.setAdapter(this.listAdapter);
        }

        this.configureRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        restaurantsViewModel = ViewModelProviders.of(requireActivity()).get(ViewModel.class);
        restaurantsViewModel.getRestaurants().observe(this, results -> {
            if (results != null) {
                Log.i(TAG, "getRestaurants().observe - result is not null");

                try {
                    // This loop will go through all the results and add marker on each location.
                    Log.i(TAG, "size onActivityCreated getResults : " + results.size());
                    for (int i = 0; i < results.size(); i++) {
                        Log.i(TAG, "onActivityCreated TRY FOR");

                        Double lat = results.get(i).getGeometry().getLocation().getLat();
                        Double lng = results.get(i).getGeometry().getLocation().getLng();

                        String placeName = results.get(i).getName();
                        String vicinity = results.get(i).getVicinity();
                        MarkerOptions markerRestaurantsOptions = new MarkerOptions();
                        LatLng latLngRestaurants = new LatLng(lat, lng);
                    }

                } catch (Exception e) {
                    Log.d("onResponse : ", "There is an error");
                    e.printStackTrace();
                }
            }
            this.listAdapter.update(results);
        });

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

    private void configureRecyclerView() {
        Log.i(TAG, "configureRecyclerView");

        this.mResults = new ArrayList<>();
        this.listAdapter = new ListAdapter(this.mResults, latitude, longitude);
        this.recyclerView.setAdapter(this.listAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Init Place Autocomplete
    protected void configurePlaceAuto() {
        Places.initialize(getActivity(), BuildConfig.PLACE_API);
    }

}
