package com.openclassrooms.go4launch.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.FragmentListItemBinding;
import com.openclassrooms.go4launch.model.Result;
import com.openclassrooms.go4launch.ui.DetailRestaurantActivity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Mohamed GHERBAL (pour OC) on 02/02/2022
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.RestaurantViewHolder> {

    // For Log.d
    private static final String TAG = ListAdapter.class.getSimpleName();

    // For ViewHolder
    public RestaurantViewHolder restaurantViewHolder;

    // For DATAS
    private Context context;
    private Result mDetailRestaurant;
    private List<Result> mRestaurants;

    public ListAdapter(List<Result> mRestaurants) {
        this.mRestaurants = mRestaurants;
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
        holder.updateWithNearbySearch(result);

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

        public void updateWithNearbySearch(Result result) {
            Log.e(TAG, "updateWithNearbySearch");

            FragmentListItemBinding.bind(itemView).listViewNameTextView.setText(result.getName());
            FragmentListItemBinding.bind(itemView).listViewVicinityTextView.setText(result.getVicinity());

            float[] results = new float[1];
            Location.distanceBetween(result.getGeometry().getLocation().getLat(),
                    result.getGeometry().getLocation().getLng(),
                    result.getGeometry().getLocation().getLat(),
                    result.getGeometry().getLocation().getLng(),
                    results);
            float distance = results[0];

            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            String roundedDistance = df.format(distance);

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

            FragmentListItemBinding.bind(itemView).listViewWorkmatesTextView.setText("("  + result.getNumUsers() + ")");

            FragmentListItemBinding.bind(itemView).listViewRatingStar.setRating(result.getRating() != null ? result.getRating().byteValue() * 3 / 5f : 0);

            if (result.getPhotos() != null && result.getPhotos().size() != 0) {
                this.picUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                        + result.getPhotos().get(0).getPhotoReference()
                        + "&key=AIzaSyCl61H4olbn8hk-whs8j4CYC5KEipU4dcY";

                Glide.with(itemView.getContext())
                        .load(picUrl).centerCrop()
                        .override(250, 250)
                        .into(FragmentListItemBinding.bind(itemView)
                                .listViewPhotoImageView);
            }
        }
    }
}
