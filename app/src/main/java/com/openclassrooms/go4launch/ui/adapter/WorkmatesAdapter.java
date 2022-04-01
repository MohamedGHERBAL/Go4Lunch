package com.openclassrooms.go4launch.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.go4launch.R;
import com.openclassrooms.go4launch.databinding.ItemWorkmatesBinding;
import com.openclassrooms.go4launch.model.User;
import com.openclassrooms.go4launch.ui.DetailRestaurantActivity;

import java.util.List;

/**
 * Created by Mohamed GHERBAL (pour OC) on 22/02/2022
 */
public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmatesViewHolder> {

    // For Log.d
    private static final String TAG = WorkmatesAdapter.class.getSimpleName();

    // For ViewHolder
    public WorkmatesViewHolder workmatesViewHolder;

    // For DATAS
    private Context context;
    private List<User> mUsers;

    public WorkmatesAdapter(List<User> items) {
        this.mUsers = items;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder");

        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_workmates, parent, false);

        return new WorkmatesViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.WorkmatesViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder");

        User user = mUsers.get(position);
        holder.updateWithUser(user);

        String restId = user.getRestId();
        Log.i(TAG, "restId = " + restId);

        holder.itemView.setOnClickListener(view -> {
            Intent details = new Intent(context, DetailRestaurantActivity.class);
            details.putExtra("place_id", restId);
            context.startActivity(details);
        });
    }

    public void update(List<User> users) {
        Log.e(TAG, "update");

        this.mUsers = users;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.mUsers.size();
    }

//**************************************************************************************************


    /* ** VIEWHOLDER ** */

    public static class WorkmatesViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        private Context context;

        // For PictureURL
        private String picUrl;

        public WorkmatesViewHolder(View itemView, Context context) {
            super(itemView);
            Log.e(TAG, "WorkmatesViewHolder");

            mView = itemView;
            this.context = context;
        }

        public void updateWithUser(User user) {
            Log.e(TAG, "updateWithUser");

            String uid = user.getUid();
            String name = user.getName();
            String restName = user.getRestName();

            if (name != null) {
                if (restName != null && !restName.equals(""))
                    ItemWorkmatesBinding.bind(itemView).itemWorkmatesText.setText(name + " " + itemView.getContext().getResources().getString(R.string.is_eating_at) + " " + restName);
                else {
                    ItemWorkmatesBinding.bind(itemView).itemWorkmatesText.setText(name + " " + itemView.getContext().getResources().getString(R.string.hasnt_decided));
                    ItemWorkmatesBinding.bind(itemView).itemWorkmatesText.setTextColor(itemView.getContext().getResources().getColor(R.color.workmates_grey));

                }
            }
            if (user.getUrlPicture() != null){
                this.picUrl = user.getUrlPicture();
                Glide.with(itemView.getContext()).load(this.picUrl). circleCrop().override(250, 250).into(ItemWorkmatesBinding.bind(itemView).itemWorkmateIcon);
            }
        }

    }

}
