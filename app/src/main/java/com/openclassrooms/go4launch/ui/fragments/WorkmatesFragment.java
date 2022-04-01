package com.openclassrooms.go4launch.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.FragmentWorkmatesBinding;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.services.UserHelper;
import com.openclassrooms.go4launch.ui.adapter.ListAdapter;
import com.openclassrooms.go4launch.ui.adapter.WorkmatesAdapter;
import com.openclassrooms.go4launch.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WorkmatesFragment extends Fragment {

    // For Log.d
    public static final String TAG = WorkmatesFragment.class.getSimpleName();

    // For ViewBinding
    private FragmentWorkmatesBinding binding;

    // For DATAS
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private List<User> mUsers = new ArrayList<>();

    // For Adapter
    private WorkmatesAdapter mAdapter;

    // For RecyclerView
    @BindView(R.id.fragment_workmates_recycler_view)
    RecyclerView recyclerView;

    // For ViewModel
    private ViewModel usersViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        // Inflate the layout for this fragment
        //binding = FragmentWorkmatesBinding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);

        // Set-up adapter
        if (view instanceof RecyclerView) {
            Log.i(TAG, "onCreateView - IF");

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.mAdapter = new WorkmatesAdapter(this.mUsers);

            recyclerView.setAdapter(this.mAdapter);
        }

        this.configureRecyclerView();
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        usersViewModel = ViewModelProviders.of(requireActivity()).get(ViewModel.class);
        usersViewModel.getUsersList().observe(this, users -> {
            if (users != null) {
                Log.i(TAG, "getUsersList().observe - users is not null");
                try {
                    CollectionReference usersCollection = UserHelper.getUsersCollection();
                    usersCollection.get().addOnCompleteListener(task -> {
                        List<User> list = new ArrayList<>();
                        if (task.getResult() != null) {
                            Log.i(TAG, "getUserList - getResult is not null !");
                            for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                list.add(documentSnapshot.toObject(User.class));
                            }
                            this.mAdapter.update(list);
                        }
                    });

                } catch (Exception e) {
                    Log.d("onResponse : ", "There is an error");
                    e.printStackTrace();
                }
            }
        });
    }

    private void configureRecyclerView() {
        Log.i(TAG, "configureRecyclerView");

        this.mUsers = new ArrayList<>();
        this.mAdapter = new WorkmatesAdapter(this.mUsers);
        this.recyclerView.setAdapter(this.mAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}