package com.humaclab.lalteer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.activity.OutletActivity;
import com.humaclab.lalteer.activity.OutletDetailsActivity;
import com.humaclab.lalteer.activity.PurchaseHistoryActivity;
import com.humaclab.lalteer.activity.RouteActivity;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Leon on 6/12/2017.
 */

public class OutletRecyclerViewAdapter extends RecyclerView.Adapter<OutletRecyclerViewAdapter.OutletViewHolder> {

    private GoogleApiClient googleApiClient;
    private SelliscopeApiEndpointInterface apiService;
    private Context context;
    private Activity activity;
    private Outlets.OutletsResult outletItems;
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;

    public OutletRecyclerViewAdapter(Context context, Activity activity, Outlets.OutletsResult outletItems) {
        this.outletItems = outletItems;
        this.context = context;
        this.activity = activity;
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public OutletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        databaseHandler = new DatabaseHandler(context);
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.outlet_item, parent, false);
        return new OutletViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final OutletViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final Outlets.Outlet outlet = outletItems.outlets.get(position);


        Glide.with(context)
                .load(Constants.baseUrl+outlet.outletImgUrl)
                .placeholder(R.drawable.ic_outlet)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.iv_outlet);

        holder.tv_outletCode.setText(outlet.outlet_code == null ? "Pending" : outlet.outlet_code);
        holder.tvOutletName.setText(outlet.outletName);
        holder.tvOutletAddress.setText(outlet.outletAddress);
        holder.tvOutletContactNumber.setText(outlet.phone);
        holder.tvOutletOwnerName.setText(outlet.ownerName);
        if (outlet.outlet_routeplan.equals("1")) {
            holder.lo_routeplan_background2.setBackgroundColor(Color.parseColor("#ff7043"));

        }
        holder.checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.pbCheckIn.setVisibility(View.VISIBLE);
                googleApiClient = new GoogleApiClient.Builder(context)
                        .addApi(Awareness.API)
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
                Intent intent = new Intent(context, RouteActivity.class);
                intent.putExtra("outletName", outlet.outletName);
                intent.putExtra("outletID", outlet.outletId);
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
    }

    private void getLocation(final int outletId, final Location outletLocation, final ProgressBar progressbar) {
        SendUserLocationData sendUserLocationData = new SendUserLocationData(context);
        sendUserLocationData.getInstantLocation(activity, new SendUserLocationData.OnGetLocation() {
            @Override
            public void getLocation(Double latitude, Double longitude) {
                Timber.d("User location: Latitude: " + latitude + "," + "Longitude: " + longitude);
                Timber.d("Outlet location: Latitude: " + outletLocation.getLatitude() + "," + "Longitude: " + outletLocation.getLongitude());

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
                        UserLocation.Successful userLocationSuccess = gson.fromJson(response.body().string(), UserLocation.Successful.class);
                        Timber.d("Result:" + userLocationSuccess.result);
                        progressBar.setVisibility(View.INVISIBLE);

                        if (userLocationSuccess.msg.equals("")) { // To check if the user already checked in the outlet
                            Toast.makeText(context, "You are checked in.", Toast.LENGTH_SHORT).show();
                            databaseHandler.afterCheckinUpdateOutletRoutePlan(outletId);

                            ((OutletActivity) context).getRoute();//For reloading the outlet recycler view
                            ((OutletActivity) context).getOutlets();//For reloading the outlet recycler view
                        } else {
                            Toast.makeText(context, "You already checked in.", Toast.LENGTH_SHORT).show();
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
        return outletItems.outlets.size();
    }

    public class OutletViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iv_outlet;
        TextView tvOutletName, tvOutletAddress, tvOutletOwnerName, tvOutletContactNumber,tv_outletCode;
        Button checkInButton, mapButton, historyButton;
        ProgressBar pbCheckIn;
        TextView tv_checkroute;
        LinearLayout lo_routeplan_background2;

        public OutletViewHolder(View itemView) {
            super(itemView);
            tv_outletCode = itemView.findViewById(R.id.tv_outletCode);
            iv_outlet = itemView.findViewById(R.id.iv_outlet);
            cardView = itemView.findViewById(R.id.cv_outlet_item);
            tvOutletName = itemView.findViewById(R.id.tv_outlet_name);
            tvOutletAddress = itemView.findViewById(R.id.tv_outlet_address);
            tvOutletContactNumber = itemView.findViewById(R.id.tv_outlet_contact_number);
            tvOutletOwnerName = itemView.findViewById(R.id.tv_owner_name);
            checkInButton = itemView.findViewById(R.id.btn_check_in);
            mapButton = itemView.findViewById(R.id.btn_map);
            historyButton = itemView.findViewById(R.id.btn_history);
            pbCheckIn = itemView.findViewById(R.id.pb_check_in);
            lo_routeplan_background2 = itemView.findViewById(R.id.routeplan_background2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OutletDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("outletDetails", outletItems.outlets.get(getLayoutPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
