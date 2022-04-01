package com.openclassrooms.go4launch.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4launch.BuildConfig;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.FragmentListItemBinding;
import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.repositories.ApplicationData;
import com.openclassrooms.go4launch.services.CurrentUser;
import com.openclassrooms.go4launch.services.RestHelper;
import com.openclassrooms.go4launch.services.UserHelper;
import com.openclassrooms.go4launch.ui.DetailRestaurantActivity;
import com.openclassrooms.go4launch.viewmodel.ViewModel;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


/**
 * Created by Mohamed GHERBAL (pour OC) on 02/02/2022
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.RestaurantViewHolder> implements Filterable {

    // For Log.d
    private static final String TAG = ListAdapter.class.getSimpleName();

    // For ViewHolder
    public RestaurantViewHolder restaurantViewHolder;

    // For DATAS
    private Context context;
    private List<Result> mRestaurants;
    private List<Result> mRestaurantsFiltered;
    private List<Result> mRestaurantsOriginal;
    private List<User> mUsers;

    private double curLat;
    private double curLng;

    public ListAdapter(List<Result> mRestaurants, double curLat, double curLng) {
        Log.d(TAG, "ListAdapter - Constructor");

        this.mRestaurants = mRestaurants;
        this.curLat = curLat;
        this.curLng = curLng;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder");

        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_item, parent, false);

        return new RestaurantViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.RestaurantViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder");

        Result result = mRestaurants.get(position);
        holder.updateWithNearbySearch(result, curLat, curLng);

        String placeId = result.getPlaceId();
        Log.i(TAG, "placeId = " + placeId);

        holder.itemView.setOnClickListener(v -> {
            Intent details = new Intent(context, DetailRestaurantActivity.class);
            details.putExtra("place_id", placeId);
            context.startActivity(details);
        });
    }

    public void update(List<Result> result) {
        Log.e(TAG, "update");

        this.mRestaurants = result;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mRestaurants.size();
    }

    @Override
    public Filter getFilter() {
        Log.e(TAG, "getFilter");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.e(TAG, "performFiltering");
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    mRestaurantsFiltered = mRestaurantsOriginal; // If no text is entered into the search box, the filtered array = the origional place array
                } else {
                    ArrayList<Result> queryfilteredList = new ArrayList<>(); // create a new Arraylist to hold the filtered results
                    for (Result row : mRestaurantsOriginal) { // loop through the origional array adding the places that match the filter criteria to the mRestaurantsFiltered
                        // Filter criteria matching by looking at the place name and place address
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getVicinity().toLowerCase().contains(charString.toLowerCase())) {
                            queryfilteredList.add(row);
                        }
                    }
                    mRestaurantsFiltered = queryfilteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mRestaurantsFiltered;

                return filterResults;
            }

            // Set the place array to equal the results of the filter and then notifyDataSetChanged()
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.e(TAG, "publishResults");
                //noinspection unchecked
                mRestaurants = (ArrayList<Result>) results.values;
                notifyDataSetChanged();
            }
        };
    }


//**************************************************************************************************


    /* ** VIEWHOLDER ** */

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        private Context context;
        private String picUrl;

        public RestaurantViewHolder(View itemView, Context context) {
            super(itemView);
            Log.e(TAG, "RestaurantViewHolder");

            mView = itemView;
            this.context = context;
        }

        public void updateWithNearbySearch(Result result, double curLat, double curLng) {
            Log.e(TAG, "updateWithNearbySearch");


            FragmentListItemBinding.bind(itemView).listViewNameTextView.setText(result.getName());
            FragmentListItemBinding.bind(itemView).listViewVicinityTextView.setText(result.getVicinity());

            float[] results = new float[1];
            Location.distanceBetween(curLat,
                    curLng,
                    result.getGeometry().getLocation().getLat(),
                    result.getGeometry().getLocation().getLng(),
                    results);
            float distance = results[0];
            Log.i(TAG, "currentLocation = "+curLat +curLng);
            Log.i(TAG, "result.getGeometry().getLocation() = "+result.getGeometry().getLocation().getLat()+result.getGeometry().getLocation().getLng());

            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            String rDistance = df.format(distance);

            FragmentListItemBinding.bind(itemView).listViewDistanceTextView.setText(rDistance + "m");

            if (result.getOpeningHours() != null) {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

            String openingHour = "";
            if (result.getOpeningHours().getWeekdayText() != null) {
                openingHour += (String) result.getOpeningHours().getWeekdayText().get(currentDay);
            }
            String isOpen;
            if (result.getOpeningHours().getOpenNow()) {
                isOpen = FragmentListItemBinding.bind(itemView).listViewOpeningHoursTextView.getResources().getString(R.string.place_open);
            } else {
                isOpen = FragmentListItemBinding.bind(itemView).listViewOpeningHoursTextView.getResources().getString(R.string.place_close);
            }
            openingHour += " " + isOpen;

            FragmentListItemBinding.bind(itemView).listViewOpeningHoursTextView.setText(openingHour);
            }

            FragmentListItemBinding.bind(itemView).listViewWorkmatesTextView.setText("0");

            int numUsers = 0;

            for (User user : ApplicationData.getInstance().getmUsers()) {
                if (user.getRestId().equals(result.getPlaceId())) {
                    numUsers++;
                }
            }

            FragmentListItemBinding.bind(itemView).listViewWorkmatesTextView.setText("("  + numUsers + ")");

            FragmentListItemBinding.bind(itemView).listViewRatingStar.setRating(result.getRating() != null ? result.getRating().byteValue() * 3 / 5f : 0);

            if (result.getPhotos() != null && result.getPhotos().size() != 0) {
                this.picUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                        + result.getPhotos().get(0).getPhotoReference()
                        + "&key="+ BuildConfig.PLACE_API;

                Glide.with(itemView.getContext())
                        .load(picUrl).centerCrop()
                        .override(250, 250)
                        .into(FragmentListItemBinding.bind(itemView)
                                .listViewPhotoImageView);
            }
        }
    }
//**************************************************************************************************
}
