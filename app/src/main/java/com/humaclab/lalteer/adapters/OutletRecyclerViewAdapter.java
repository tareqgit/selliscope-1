package com.humaclab.lalteer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.activity.OutletActivity;
import com.humaclab.lalteer.activity.OutletDetailsActivity;
import com.humaclab.lalteer.activity.OutletMapActivity;
import com.humaclab.lalteer.activity.PurchaseHistoryActivity;
import com.humaclab.lalteer.model.Outlets;
import com.humaclab.lalteer.model.UserLocation;
import com.humaclab.lalteer.utils.Constants;
import com.humaclab.lalteer.utils.DatabaseHandler;
import com.humaclab.lalteer.utils.GetAddressFromLatLang;
import com.humaclab.lalteer.utils.NetworkUtility;
import com.humaclab.lalteer.utils.SendUserLocationData;
import com.humaclab.lalteer.utils.SessionManager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Leon on 6/12/2017.
 */

public class OutletRecyclerViewAdapter extends RecyclerView.Adapter<OutletRecyclerViewAdapter.OutletViewHolder> {
    private Shimmer shimmer;

    private GoogleApiClient googleApiClient;
    private SelliscopeApiEndpointInterface apiService;
    private Context context;
    private Activity activity;
    private List<Outlets.Outlet> outlets;
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;

    public OutletRecyclerViewAdapter(Context context, Activity activity, List<Outlets.Outlet> outlets) {
        this.outlets = outlets;
        this.context = context;
        this.activity = activity;
        this.sessionManager = new SessionManager(context);


        shimmer= new Shimmer.ColorHighlightBuilder()
                .setBaseAlpha(.85f)
                .setIntensity(0)
                .build();

        databaseHandler = new DatabaseHandler(context);
    }


    public void updateOutlets( List<Outlets.Outlet> outlets){
        this.outlets=outlets;
        notifyDataSetChanged();
    }


    @Override
    public OutletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.outlet_item, parent, false);
        return new OutletViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final OutletViewHolder holder, final int position) {

        Outlets.Outlet outlet = outlets.get(position);

        ShimmerDrawable shimmerDrawable=new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);



        if(!Objects.equals(outlet.outletImgUrl, "")) {

            final String url = Constants.BASE_URL.substring(0,Constants.BASE_URL.length() - 4) + outlet.outletImgUrl;

            Glide.with(context)
                    //as base usl has api in url but image don't have that
                    .load(url)
                    .placeholder(shimmerDrawable)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.iv_outlet_image);
        }else{
            holder.iv_outlet_image.setImageResource(R.drawable.selliscope_splash);
        }
        holder.tv_outletCode.setText(outlet.outlet_code == null ? "Pending" : outlet.outlet_code);
        holder.tvOutletName.setText(outlet.outletName);
        holder.tvOutletType.setText(outlet.outletType);
        holder.tvOutletAddress.setText(outlet.outletAddress);
        holder.tvOutletContactNumber.setText(outlet.phone);
        holder.tvOutletOwnerName.setText(outlet.ownerName);
        if (outlet.outlet_routeplan.equals("1")) {
            holder.lo_routeplan_background2.setImageResource(R.drawable.moss_gradient2);

        }else{
            holder.lo_routeplan_background2.setImageResource(R.color.white);
        }
        holder.checkInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                holder.pbCheckIn.setVisibility(View.VISIBLE);
                googleApiClient = new GoogleApiClient.Builder(context)
                        .addApi(LocationServices.API)
                        .build();
                googleApiClient.connect();
                googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Location outletLocation = new Location("outlet_location");
                        outletLocation.setLatitude(outlet.outletLatitude);
                        outletLocation.setLongitude(outlet.outletLongitude);
                        getLocation(outlet.outletId, outletLocation, holder.pbCheckIn);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        holder.pbCheckIn.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Didn't able to get location " +
                                "data. Please, try again later!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: add map direction layout here.
                Log.d("tareq_test" , ""+outlet.outletLatitude);

                Intent intent = new Intent(context, OutletMapActivity.class);
                intent.putExtra("outletName", outlet.outletName);
                intent.putExtra("outletID", outlet.outletId);
                intent.putExtra("outletLat", outlet.outletLatitude);
                intent.putExtra("outletLong", outlet.outletLongitude);
                context.startActivity(intent);
            }
        });
        holder.historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PurchaseHistoryActivity.class);
                intent.putExtra("outletDetails", outlet);
                context.startActivity(intent);
            }
        });
        setAnimation(holder.cardView, position);
    }

    private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void getLocation(final int outletId, final Location outletLocation, final ProgressBar progressbar) {
        SendUserLocationData sendUserLocationData = new SendUserLocationData(context);
        sendUserLocationData.getInstantLocation(activity, new SendUserLocationData.OnGetLocation() {
            @Override
            public void getLocation(Double latitude, Double longitude) {

                Location location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                if (location.distanceTo(outletLocation) <= sessionManager.getDiameter()) {
                    if (NetworkUtility.isNetworkAvailable(context)) {
                        sendUserLocation(location, outletId, progressbar);
                    } else {
                        progressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Enable Wifi or Mobile data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "You are not within 70m radius of the outlet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserLocation(Location location, final int outletId, final ProgressBar progressBar) {
        SessionManager sessionManager = new SessionManager(context);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        List<UserLocation.Visit> userLocationVisits = new ArrayList<>();
        userLocationVisits.add(new UserLocation.Visit(location.getLatitude(), location.getLongitude(), GetAddressFromLatLang.getAddressFromLatLan(context, location.getLatitude(), location.getLongitude()), outletId));
        Call<ResponseBody> call = apiService.sendUserLocation(new UserLocation(userLocationVisits));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Timber.d("Code:" + response.code());
                Gson gson = new Gson();
                if (response.code() == 200) {
                    try {
                        UserLocation.Successful userLocationSuccess = null;
                        if (response.body() != null) {
                            userLocationSuccess = gson.fromJson(response.body().string(), UserLocation.Successful.class);
                        }

                        progressBar.setVisibility(View.INVISIBLE);

                        if (userLocationSuccess != null) {
                            if (userLocationSuccess.msg.equals("")) { // To check if the user already checked in the outlet
                                Toast.makeText(context, "You are checked in.", Toast.LENGTH_SHORT).show();
                                databaseHandler.afterCheckinUpdateOutletRoutePlan(outletId);

                                ((OutletActivity) context).getRoute();//For reloading the outlet recycler view
                                ((OutletActivity) context).getOutlets();//For reloading the outlet recycler view
                            } else {
                                Toast.makeText(context, "You already checked in.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Timber.d("Error:" + e.toString());
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {
                    progressBar.setVisibility(View.INVISIBLE);

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context,
                            "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("Response", t.toString());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {

        return outlets == null ? 0 : outlets.size();
    }

    public class OutletViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iv_outlet_image;
        TextView tvOutletName, tvOutletAddress, tvOutletOwnerName, tvOutletContactNumber,tv_outletCode, tvOutletType;
        Button checkInButton, mapButton, historyButton;
        ProgressBar pbCheckIn;
        TextView tv_checkroute;
        ImageView lo_routeplan_background2;

        public OutletViewHolder(View itemView) {
            super(itemView);
            tv_outletCode = itemView.findViewById(R.id.tv_outletCode);
            iv_outlet_image = itemView.findViewById(R.id.iv_outlet_image);
            cardView = itemView.findViewById(R.id.cv_outlet_item);
            tvOutletName = itemView.findViewById(R.id.tv_outlet_name);
            tvOutletType = itemView.findViewById(R.id.tv_outlet_type);
            tvOutletAddress = itemView.findViewById(R.id.tv_outlet_address);
            tvOutletContactNumber = itemView.findViewById(R.id.tv_outlet_contact_number);
            tvOutletOwnerName = itemView.findViewById(R.id.tv_owner_name);
            checkInButton = itemView.findViewById(R.id.btn_check_in);
            mapButton = itemView.findViewById(R.id.btn_map);
            historyButton = itemView.findViewById(R.id.btn_history);
            pbCheckIn = itemView.findViewById(R.id.pb_check_in);
            lo_routeplan_background2 = itemView.findViewById(R.id.routeplan_background2);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, OutletDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("outletDetails", outlets.get(getLayoutPosition()));
                context.startActivity(intent);
            });
        }
    }
}
