package com.humaclab.selliscope_nepal.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.selliscope_nepal.OrderActivity;
import com.humaclab.selliscope_nepal.OutletDetailsActivity;
import com.humaclab.selliscope_nepal.R;
import com.humaclab.selliscope_nepal.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_nepal.SelliscopeApplication;
import com.humaclab.selliscope_nepal.Utils.GetAddressFromLatLang;
import com.humaclab.selliscope_nepal.Utils.NetworkUtility;
import com.humaclab.selliscope_nepal.Utils.SessionManager;
import com.humaclab.selliscope_nepal.model.Outlets;
import com.humaclab.selliscope_nepal.model.UserLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.humaclab.selliscope_nepal.RouteActivity.checkPermission;

/**
 * Created by Nahid on 3/12/2017.
 */

public class OutletRecyclerViewAdapter extends RecyclerView.Adapter<OutletRecyclerViewAdapter.OutletViewHolder> {

    private GoogleApiClient googleApiClient;
    private SelliscopeApiEndpointInterface apiService;
    private Context context;
    private Outlets.Successful.OutletsResult outletItems;

    public OutletRecyclerViewAdapter(Context context, Outlets.Successful.OutletsResult outletItems) {
        this.outletItems = outletItems;
        this.context = context;
    }

    @Override
    public OutletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.outlet_item, parent, false);
        return new OutletViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final OutletViewHolder holder, final int position) {
        final Outlets.Successful.Outlet outlet = outletItems.outlets.get(position);
        Glide.with(context).load(outlet.outletImgUrl)
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.ic_outlet_bnw)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_outlet);
        holder.tvOutletName.setText(outlet.outletName);
        holder.tvOutletAddress.setText(outlet.outletAddress);
        holder.tvOutletContactNumber.setText(outlet.phone);
        holder.tvOutletOwnerName.setText(outlet.ownerName);
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
        holder.orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderActivity.class);
                intent.putExtra("outletName", outlet.outletName);
                intent.putExtra("outletID", outlet.outletId);
                context.startActivity(intent);
            }
        });
    }

    private void getLocation(final int outletId, final Location outletLocation,
                             final ProgressBar progressbar) {
        if (checkPermission(context)) {
            Awareness.SnapshotApi.getLocation(googleApiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (locationResult.getStatus().isSuccess()) {
                                Location location = locationResult.getLocation();
                                //sendLocationDataToSever();
                                double latitude = Double.parseDouble(String.format("%.05f", location.getLatitude()));
                                double longitude = Double.parseDouble(String.format("%.05f", location.getLongitude()));
                                Timber.d("User location: Latitude: " + latitude + "," + "Longitude: " + longitude);
                                Timber.d("Outlet location: Latitude: " + outletLocation.getLatitude() + "," + "Longitude: " + outletLocation.getLongitude());
                                location.setLatitude(latitude);
                                location.setLongitude(longitude);
                                if (location.distanceTo(outletLocation) <= 150.0) {
                                    if (NetworkUtility.isNetworkAvailable(context)) {
                                        sendUserLocation(location, outletId, progressbar);
                                    } else {
                                        progressbar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(context, "Enable Wifi or Mobile data.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    progressbar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(context, "You are not " +
                                                    "within 30m radius of the outlet.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                progressbar.setVisibility(View.INVISIBLE);
                                Timber.d("Didn't get Location Data");
                                Toast.makeText(context, "Enable GPS.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            progressbar.setVisibility(View.INVISIBLE);
            Timber.d("Location Permission is not enabled.");
        }
    }

    private void sendUserLocation(Location location, int outletId, final ProgressBar progressBar) {
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
                if (response.code() == 201) {
                    try {
                        UserLocation.Successful userLocationSuccess
                                = gson.fromJson(response.body().string()
                                , UserLocation.Successful.class);
                        Timber.d("Result:" + userLocationSuccess.result);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "You are checked In.", Toast.LENGTH_SHORT).show();
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
        TextView tvOutletName, tvOutletAddress, tvOutletOwnerName, tvOutletContactNumber;
        Button checkInButton, orderButton;
        ProgressBar pbCheckIn;


        public OutletViewHolder(View itemView) {
            super(itemView);
            iv_outlet = itemView.findViewById(R.id.iv_outlet);
            cardView = itemView.findViewById(R.id.cv_outlet_item);
            tvOutletName = itemView.findViewById(R.id.tv_outlet_name);
            tvOutletAddress = itemView.findViewById(R.id.tv_outlet_address);
            tvOutletContactNumber = itemView.findViewById(R.id.tv_outlet_contact_number);
            tvOutletOwnerName = itemView.findViewById(R.id.tv_owner_name);
            checkInButton = itemView.findViewById(R.id.btn_check_in);
            orderButton = itemView.findViewById(R.id.btn_order);
            pbCheckIn = itemView.findViewById(R.id.pb_check_in);

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
