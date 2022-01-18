package com.openclassrooms.go4launch.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.go4launch.databinding.FragmentListBinding;
import com.openclassrooms.go4launch.databinding.FragmentMapBinding;


public class ListFragment extends Fragment {

    private FragmentListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }
}