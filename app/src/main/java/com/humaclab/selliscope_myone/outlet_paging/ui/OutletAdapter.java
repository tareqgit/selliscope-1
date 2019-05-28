/*
 * Created by Tareq Islam on 5/22/19 2:58 PM
 *
 *  Last modified 5/22/19 2:58 PM
 */

package com.humaclab.selliscope_myone.outlet_paging.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.activity.OrderActivity;
import com.humaclab.selliscope_myone.activity.OutletDetailsActivity;
import com.humaclab.selliscope_myone.model.UserLocation;
import com.humaclab.selliscope_myone.order_history.OrderHistoryActivity;
import com.humaclab.selliscope_myone.outlet_paging.model.OutletItem;
import com.humaclab.selliscope_myone.utils.GetAddressFromLatLang;
import com.humaclab.selliscope_myone.utils.NetworkUtility;
import com.humaclab.selliscope_myone.utils.SendUserLocationData;
import com.humaclab.selliscope_myone.utils.SessionManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/***
 * Created by mtita on 22,May,2019.
 */
public class OutletAdapter extends PagedListAdapter<OutletItem, OutletAdapter.OutletViewHolder> {

    /**
     * DiffUtil to compare the Repo data (old and new) for issuing notify commands suitably to
     * update the list
     */
    private static DiffUtil.ItemCallback<OutletItem> REPO_COMPARATOR
            = new DiffUtil.ItemCallback<OutletItem>() {
        @Override
        public boolean areItemsTheSame(OutletItem oldItem, OutletItem newItem) {
            return oldItem.name.equals(newItem.name);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(OutletItem oldItem, OutletItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    private Context mContext;
    private Activity mActivity;

    public OutletAdapter(Context context, Activity activity) {

        super(REPO_COMPARATOR);
        mContext = context;
        mActivity = activity;
    }

    @Override
    public OutletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.outlet_item, parent, false);
        return new OutletViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(OutletViewHolder holder, int position) {
        OutletItem outlet = getItem(position);
        if (outlet != null) {
            holder.tvOutletName.setText(outlet.name);
            holder.tvOutletAddress.setText(outlet.address);
            holder.tvOutletContactNumber.setText(outlet.phone);
            holder.tvOutletOwnerName.setText(outlet.owner);
            holder.checkInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.pbCheckIn.setVisibility(View.VISIBLE);
                    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(v.getContext())
                            .addApi(Awareness.API)
                            .addApi(LocationServices.API)
                            .build();
                    googleApiClient.connect();
                    googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if (outlet.latitude != null && outlet.longitude != null) {
                                Location outletLocation = new Location("outlet_location");
                                outletLocation.setLatitude(Double.parseDouble(outlet.latitude));
                                outletLocation.setLongitude(Double.parseDouble(outlet.longitude));
                                getLocation(outlet.id, outletLocation, holder.pbCheckIn);
                            } else {
                                holder.pbCheckIn.setVisibility(View.INVISIBLE);
                                Toast.makeText(v.getContext(), "You have to create an order before Check-in", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            holder.pbCheckIn.setVisibility(View.INVISIBLE);
                            Toast.makeText(v.getContext(), "Didn't able to get location " +
                                    "data. Please, try again later!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            holder.orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), OrderHistoryActivity.class);
                    intent.putExtra("outletName", outlet.name);
                    intent.putExtra("outletID", outlet.id);
                    v.getContext().startActivity(intent);
                }
            });

        }
    }

    private void getLocation(final String outletId, final Location outletLocation,
                             final ProgressBar progressbar) {
        SendUserLocationData sendUserLocationData = new SendUserLocationData(mContext);
        sendUserLocationData.getInstantLocation(mActivity, new SendUserLocationData.OnGetLocation() {
            @Override
            public void getLocation(Double latitude, Double longitude) {
                Timber.d("User location: Latitude: " + latitude + "," + "Longitude: " + longitude);
                Timber.d("Outlet location: Latitude: " + outletLocation.getLatitude() + "," + "Longitude: " + outletLocation.getLongitude());

                Location location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                if (location.distanceTo(outletLocation) <= new SessionManager(mContext).getDiameter()) {
                    if (NetworkUtility.isNetworkAvailable(mContext)) {
                        sendUserLocation(location, outletId, progressbar);
                    } else {
                        progressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(mContext, "Enable Wifi or Mobile data.",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext, "You are not within 70m radius of the outlet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserLocation(Location location, String outletId, final ProgressBar progressBar) {
        SessionManager sessionManager = new SessionManager(mContext);
        SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        List<UserLocation.Visit> userLocationVisits = new ArrayList<>();
        userLocationVisits.add(new UserLocation.Visit(location.getLatitude(), location.getLongitude(), GetAddressFromLatLang.getAddressFromLatLan(mContext, location.getLatitude(), location.getLongitude()), outletId));
        Call<ResponseBody> call = apiService.sendUserLocation(new UserLocation(userLocationVisits));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Timber.d("Code:" + response.code());
                Gson gson = new Gson();
                if (response.code() == 201) {
                    try {
                        UserLocation.Successful userLocationSuccess = gson.fromJson(response.body().string(), UserLocation.Successful.class);
                        Timber.d("Result:" + userLocationSuccess.result);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(mContext, "You are checked In.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Timber.d("Error:" + e.toString());
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {
                    progressBar.setVisibility(View.INVISIBLE);

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext,
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
                    Intent intent = new Intent(v.getContext(), OutletDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // intent.putExtra("outletDetails", outletItems.outlets.get(getLayoutPosition()));
                    intent.putExtra("outletDetails", (Serializable) getItem(getAdapterPosition()));

                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
