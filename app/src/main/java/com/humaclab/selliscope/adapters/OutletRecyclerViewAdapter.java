package com.humaclab.selliscope.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.parser.IntegerParser;
import com.andrognito.flashbar.Flashbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.selliscope.LocationMonitoringService;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.activity.OutletActivity;
import com.humaclab.selliscope.activity.OutletDetailsActivity;
import com.humaclab.selliscope.activity.OutletMapActivity;
import com.humaclab.selliscope.activity.PurchaseHistoryActivity;
import com.humaclab.selliscope.activity.SelfieCheck_inActivity;
import com.humaclab.selliscope.databinding.OutletItemBinding;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.UserLocation;
import com.humaclab.selliscope.performance.daily_activities.model.OutletWithCheckInTime;
import com.humaclab.selliscope.service.NotificationHandler;
import com.humaclab.selliscope.utility_db.model.RegularPerformanceEntity;
import com.humaclab.selliscope.utility_db.db.UtilityDatabase;
import com.humaclab.selliscope.utils.BatteryUtils;
import com.humaclab.selliscope.utils.Constants;
import com.humaclab.selliscope.utils.CurrentTimeUtilityClass;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.GetAddressFromLatLang;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leon on 6/12/2017.
 */

public class OutletRecyclerViewAdapter extends RecyclerView.Adapter<OutletRecyclerViewAdapter.OutletViewHolder> {
    private Shimmer shimmer;

    private GoogleApiClient googleApiClient;
    private SelliscopeApiEndpointInterface apiService;
    private Context context;
    private Activity activity;
    public List<Outlets.Outlet> outlets;
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;

    public OutletRecyclerViewAdapter(Context context, Activity activity,   List<Outlets.Outlet> outlets) {
        this.outlets = outlets;
        this.context = context;
        this.activity = activity;
        this.sessionManager = new SessionManager(context);


        shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseAlpha(.85f)
                .setIntensity(0)
                .build();

    }


    public void updateOutlate(  List<Outlets.Outlet> outlets){
        this.outlets = outlets;
        notifyDataSetChanged();
    }

    @Override
    public OutletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        databaseHandler = new DatabaseHandler(context);
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.outlet_item, parent, false);
        return new OutletViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final OutletViewHolder holder, final int position) {
        // holder.setIsRecyclable(false);
        final Outlets.Outlet outlet = outlets.get(position);

        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);


        if (!outlet.outletImgUrl.equals("")) {

            Glide.with(context).
                    load(outlet.outletImgUrl)
                    .thumbnail(0.1f)
                    .placeholder(shimmerDrawable)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.getBinding().outletImage);

        } else {
            holder.getBinding().outletImage.setImageResource(R.drawable.selliscope_splash);
        }
        holder.getBinding().tvOutletName.setText(outlet.outletName);
        holder.getBinding().tvClientId.setText(outlet.getClientID().equals("") ? " Pending" : outlet.getClientID());
        holder.getBinding().tvOutletAddress.setText(outlet.outletAddress);
        holder.getBinding().tvOutletContactNumber.setText(outlet.phone);
        holder.getBinding().tvOwnerName.setText(outlet.ownerName);
        if (outlet.getOutlet_routeplan().equals("1")) {
            holder.getBinding().routeplanBackground2.setImageResource(R.drawable.moss_gradient2);

        } else {
            holder.getBinding().routeplanBackground2.setImageResource(R.color.white);
        }
        holder.getBinding().btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.getBinding().pbCheckIn.setVisibility(View.VISIBLE);

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
                        getLocation(outlet, outletLocation, holder.getBinding().pbCheckIn);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        holder.getBinding().pbCheckIn.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Didn't able to get location " +
                                "data. Please, try again later!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.getBinding().btnMap.setOnClickListener(v -> {


            //TODO: add map direction layout here.
            Intent intent = new Intent(context, OutletMapActivity.class);
            intent.putExtra("outletName", outlet.outletName);
            intent.putExtra("outletID", outlet.outletId);
            intent.putExtra("outletLat", outlet.outletLatitude);
            intent.putExtra("outletLong", outlet.outletLongitude);

            context.startActivity(intent);

        });


        holder.getBinding().btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PurchaseHistoryActivity.class);
                intent.putExtra("outletDetails", outlet);
                context.startActivity(intent);
            }
        });
    }

    private void getLocation(final Outlets.Outlet outlet, final Location outletLocation, final ProgressBar progressbar) {
    //    SendUserLocationData sendUserLocationData = new SendUserLocationData(context);
     //   sendUserLocationData.getInstantLocation(activity, (latitude, longitude) -> {
       if( LocationMonitoringService.sLocation!=null) {
           Log.d("tareq_test", "OutletRecyclerViewAdapter #200: getLocation:  " + LocationMonitoringService.sLocation.getLatitude() + " , " + LocationMonitoringService.sLocation.getLongitude());
           Location location = new Location("");
           location.setLatitude(LocationMonitoringService.sLocation.getLatitude());
           location.setLongitude(LocationMonitoringService.sLocation.getLongitude());

           /*If client is not BIRGH then act as normal*/
           if (Integer.parseInt(new SessionManager(context).getClientID()) != 1) { //change 1 into birgh ID for birgh 5



           if (location.distanceTo(outletLocation) <= sessionManager.getDiameter())
           {


               if (NetworkUtility.isNetworkAvailable(context)) {
                   sendUserLocation(location, outlet, progressbar);
               } else {
                   //if internet not available
                   progressbar.setVisibility(View.INVISIBLE);
                   new Flashbar.Builder(activity)
                           .gravity(Flashbar.Gravity.TOP)
                           .title("Sorry!")
                           .message("Internet Connection not available.")
                           .castShadow()
                           .enableSwipeToDismiss()
                           .barDismissListener(new Flashbar.OnBarDismissListener() {
                               @Override
                               public void onDismissing(@NotNull Flashbar flashbar, boolean b) {

                               }

                               @Override
                               public void onDismissProgress(@NotNull Flashbar flashbar, float v) {

                               }

                               @Override
                               public void onDismissed(@NotNull Flashbar flashbar, Flashbar.@NotNull DismissEvent dismissEvent) {
                                   progressbar.setVisibility(View.INVISIBLE);
                               }
                           })
                           .primaryActionText("Selfie")
                           .primaryActionTapListener(new Flashbar.OnActionTapListener() {
                               @Override
                               public void onActionTapped(Flashbar flashbar) {
                                   flashbar.dismiss();
                                   Intent intent = new Intent(context, SelfieCheck_inActivity.class);
                                   intent.putExtra("outletId", outlet.outletId);
                                   intent.putExtra("outlet", outlet);
                                   context.startActivity(intent);
                               }
                           })
                           .backgroundDrawable(R.drawable.moss_gradient2)
                           .build().show();
               }
           } else
               {
               //if not within redius
               progressbar.setVisibility(View.INVISIBLE);
               new Flashbar.Builder(activity)
                       .gravity(Flashbar.Gravity.TOP)
                       .title("Sorry!")
                       .message("You are not within 70 meter.")
                       .castShadow()
                       .enableSwipeToDismiss()
                       .barDismissListener(new Flashbar.OnBarDismissListener() {
                           @Override
                           public void onDismissing(@NotNull Flashbar flashbar, boolean b) {

                           }

                           @Override
                           public void onDismissProgress(@NotNull Flashbar flashbar, float v) {

                           }

                           @Override
                           public void onDismissed(@NotNull Flashbar flashbar, Flashbar.@NotNull DismissEvent dismissEvent) {
                               progressbar.setVisibility(View.INVISIBLE);
                           }
                       })
                       .primaryActionText("Selfie")
                       .primaryActionTapListener(new Flashbar.OnActionTapListener() {
                           @Override
                           public void onActionTapped(Flashbar flashbar) {
                               flashbar.dismiss();
                               Intent intent = new Intent(context, SelfieCheck_inActivity.class);
                               intent.putExtra("outletId", outlet.outletId);
                               intent.putExtra("outlet", outlet);
                               context.startActivity(intent);
                           }
                       })
                       .backgroundDrawable(R.drawable.moss_gradient2)
                       .build().show();
           }
       }else{

               //if client is BIRGH ^^
               //first check-in would be selfie check-IN,
               if(new SessionManager(context).getLastCheckInTime() != null)       Log.d("tareq_test", "OutletRecyclerViewAdapter #310: getLocation:  "+ CurrentTimeUtilityClass.getDiffBetween( new SessionManager(context).getLastCheckInTime()));

               if(new SessionManager(context).getLastCheckInDate() == null
                       || CurrentTimeUtilityClass.getDiffBetweenTwoDate( new SessionManager(context).getLastCheckInDate(), CurrentTimeUtilityClass.getCurrentTimeStampDate()) >0
                       ||  CurrentTimeUtilityClass.getDiffBetween( new SessionManager(context).getLastCheckInTime())>= Constants.SCHEDULER_INTERVAL_BIR){ //change time distance to 120 for 2 hour



           if(new SessionManager(context).getLastCheckInDate() != null)      Log.d("tareq_test", "OutletRecyclerViewAdapter #264: getLocation:  "+ CurrentTimeUtilityClass.getDiffBetweenTwoDate( new SessionManager(context).getLastCheckInDate(), CurrentTimeUtilityClass.getCurrentTimeStampDate()));

           if(new SessionManager(context).getLastCheckInTime() != null)       Log.d("tareq_test", "OutletRecyclerViewAdapter #310: getLocation:  "+ CurrentTimeUtilityClass.getDiffBetween( new SessionManager(context).getLastCheckInTime()));

                   progressbar.setVisibility(View.INVISIBLE);
                   new Flashbar.Builder(activity)
                           .gravity(Flashbar.Gravity.TOP)
                           .title("Please!")
                           .message("Give a Selfie check-in")
                           .castShadow()
                           .enableSwipeToDismiss()
                           .barDismissListener(new Flashbar.OnBarDismissListener() {
                               @Override
                               public void onDismissing(@NotNull Flashbar flashbar, boolean b) {

                               }

                               @Override
                               public void onDismissProgress(@NotNull Flashbar flashbar, float v) {

                               }

                               @Override
                               public void onDismissed(@NotNull Flashbar flashbar, Flashbar.@NotNull DismissEvent dismissEvent) {
                                   progressbar.setVisibility(View.INVISIBLE);
                               }
                           })
                           .positiveActionTextColor(R.color.success)
                           .positiveActionText("Selfie")
                           .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                               @Override
                               public void onActionTapped(Flashbar flashbar) {
                                   flashbar.dismiss();
                                   Intent intent = new Intent(context, SelfieCheck_inActivity.class);
                                   intent.putExtra("outletId", outlet.outletId);
                                   intent.putExtra("outlet", outlet);
                                   context.startActivity(intent);
                               }
                           })
                           .backgroundDrawable(R.drawable.moss_gradient2)
                           .build().show();
               }else{ //from second check-in we will ask for check-in or selfie check-in

                   /*new Flashbar.Builder(activity)
                           .gravity(Flashbar.Gravity.TOP)
                           .title("Please!")
                           .message("Chose a option from below. if your last check-in interval was more than 2 hour, Please, submit Selfie check-in ")
                           .castShadow()
                           .enableSwipeToDismiss()
                           .barDismissListener(new Flashbar.OnBarDismissListener() {
                               @Override
                               public void onDismissing(@NotNull Flashbar flashbar, boolean b) {

                               }

                               @Override
                               public void onDismissProgress(@NotNull Flashbar flashbar, float v) {

                               }

                               @Override
                               public void onDismissed(@NotNull Flashbar flashbar, Flashbar.@NotNull DismissEvent dismissEvent) {
                                   progressbar.setVisibility(View.INVISIBLE);
                               }
                           })
                           .positiveActionText("Selfie")
                           .positiveActionTextColorRes(R.color.success)
                           .negativeActionTextColorRes(R.color.colorPrimaryDark)
                           .negativeActionText("Normal")
                           .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                               @Override
                               public void onActionTapped(Flashbar flashbar) {
                                   flashbar.dismiss();

                                   Intent intent = new Intent(context, SelfieCheck_inActivity.class);
                                   intent.putExtra("outletId", outlet.outletId);
                                   intent.putExtra("outlet", outlet);
                                   context.startActivity(intent);
                               }
                           })
                           .negativeActionTapListener(new Flashbar.OnActionTapListener() {
                               @Override
                               public void onActionTapped(@NotNull Flashbar flashbar) {
                                   flashbar.dismiss();
                                   if (location.distanceTo(outletLocation) <= sessionManager.getDiameter())
                                   {


                                       if (NetworkUtility.isNetworkAvailable(context)) {
                                           sendUserLocation(location, outlet, progressbar);
                                       } else {
                                           //if internet not available
                                           progressbar.setVisibility(View.INVISIBLE);
                                           new Flashbar.Builder(activity)
                                                   .gravity(Flashbar.Gravity.TOP)
                                                   .title("Sorry!")
                                                   .message("Internet Connection not available.")
                                                   .castShadow()
                                                   .enableSwipeToDismiss()
                                                   .barDismissListener(new Flashbar.OnBarDismissListener() {
                                                       @Override
                                                       public void onDismissing(@NotNull Flashbar flashbar, boolean b) {

                                                       }

                                                       @Override
                                                       public void onDismissProgress(@NotNull Flashbar flashbar, float v) {

                                                       }

                                                       @Override
                                                       public void onDismissed(@NotNull Flashbar flashbar, Flashbar.@NotNull DismissEvent dismissEvent) {
                                                           progressbar.setVisibility(View.INVISIBLE);
                                                       }
                                                   })
                                                   .positiveActionText("Selfie")
                                                   .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                                                       @Override
                                                       public void onActionTapped(Flashbar flashbar) {
                                                           flashbar.dismiss();
                                                           Intent intent = new Intent(context, SelfieCheck_inActivity.class);
                                                           intent.putExtra("outletId", outlet.outletId);
                                                           intent.putExtra("outlet", outlet);
                                                           context.startActivity(intent);
                                                       }
                                                   })
                                                   .backgroundDrawable(R.drawable.moss_gradient2)
                                                   .build().show();
                                       }
                                   } else
                                   {
                                       //if not within redius
                                       progressbar.setVisibility(View.INVISIBLE);
                                       new Flashbar.Builder(activity)
                                               .gravity(Flashbar.Gravity.TOP)
                                               .title("Sorry!")
                                               .message("You are not within 70 meter.")
                                               .castShadow()
                                               .enableSwipeToDismiss()
                                               .barDismissListener(new Flashbar.OnBarDismissListener() {
                                                   @Override
                                                   public void onDismissing(@NotNull Flashbar flashbar, boolean b) {

                                                   }

                                                   @Override
                                                   public void onDismissProgress(@NotNull Flashbar flashbar, float v) {

                                                   }

                                                   @Override
                                                   public void onDismissed(@NotNull Flashbar flashbar, Flashbar.@NotNull DismissEvent dismissEvent) {
                                                       progressbar.setVisibility(View.INVISIBLE);
                                                   }
                                               })
                                               .positiveActionText("Selfie")
                                               .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                                                   @Override
                                                   public void onActionTapped(Flashbar flashbar) {
                                                       flashbar.dismiss();
                                                       Intent intent = new Intent(context, SelfieCheck_inActivity.class);
                                                       intent.putExtra("outletId", outlet.outletId);
                                                       intent.putExtra("outlet", outlet);
                                                       context.startActivity(intent);
                                                   }
                                               })
                                               .backgroundDrawable(R.drawable.moss_gradient2)
                                               .build().show();
                                   }
                               }
                           })
                           .backgroundDrawable(R.drawable.moss_gradient2)
                           .build().show();*/

                   if (location.distanceTo(outletLocation) <= sessionManager.getDiameter())
                   {


                       if (NetworkUtility.isNetworkAvailable(context)) {
                           sendUserLocation(location, outlet, progressbar);
                       } else {
                           //if internet not available
                           progressbar.setVisibility(View.INVISIBLE);
                           new Flashbar.Builder(activity)
                                   .gravity(Flashbar.Gravity.TOP)
                                   .title("Sorry!")
                                   .message("Internet Connection not available.")
                                   .castShadow()
                                   .enableSwipeToDismiss()
                                   .barDismissListener(new Flashbar.OnBarDismissListener() {
                                       @Override
                                       public void onDismissing(@NotNull Flashbar flashbar, boolean b) {

                                       }

                                       @Override
                                       public void onDismissProgress(@NotNull Flashbar flashbar, float v) {

                                       }

                                       @Override
                                       public void onDismissed(@NotNull Flashbar flashbar, Flashbar.@NotNull DismissEvent dismissEvent) {
                                           progressbar.setVisibility(View.INVISIBLE);
                                       }
                                   })
                                   .positiveActionText("Selfie")
                                   .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                                       @Override
                                       public void onActionTapped(Flashbar flashbar) {
                                           flashbar.dismiss();
                                           Intent intent = new Intent(context, SelfieCheck_inActivity.class);
                                           intent.putExtra("outletId", outlet.outletId);
                                           intent.putExtra("outlet", outlet);
                                           context.startActivity(intent);
                                       }
                                   })
                                   .backgroundDrawable(R.drawable.moss_gradient2)
                                   .build().show();
                       }
                   } else
                   {
                       //if not within redius
                       progressbar.setVisibility(View.INVISIBLE);
                       new Flashbar.Builder(activity)
                               .gravity(Flashbar.Gravity.TOP)
                               .title("Sorry!")
                               .message("You are not within 70 meter.")
                               .castShadow()
                               .enableSwipeToDismiss()
                               .barDismissListener(new Flashbar.OnBarDismissListener() {
                                   @Override
                                   public void onDismissing(@NotNull Flashbar flashbar, boolean b) {

                                   }

                                   @Override
                                   public void onDismissProgress(@NotNull Flashbar flashbar, float v) {

                                   }

                                   @Override
                                   public void onDismissed(@NotNull Flashbar flashbar, Flashbar.@NotNull DismissEvent dismissEvent) {
                                       progressbar.setVisibility(View.INVISIBLE);
                                   }
                               })
                               .positiveActionText("Selfie")
                               .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                                   @Override
                                   public void onActionTapped(Flashbar flashbar) {
                                       flashbar.dismiss();
                                       Intent intent = new Intent(context, SelfieCheck_inActivity.class);
                                       intent.putExtra("outletId", outlet.outletId);
                                       intent.putExtra("outlet", outlet);
                                       context.startActivity(intent);
                                   }
                               })
                               .backgroundDrawable(R.drawable.moss_gradient2)
                               .build().show();
                   }
               }
           }
       }else{
           progressbar.setVisibility(View.INVISIBLE);
           Toast.makeText(context, "Can't get the location", Toast.LENGTH_SHORT).show();
       }
       // });
    }

    private void sendUserLocation(Location location, final Outlets.Outlet outlet, final ProgressBar progressBar) {
        SessionManager sessionManager = new SessionManager(context);
        apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        List<UserLocation.Visit> userLocationVisits = new ArrayList<>();
        userLocationVisits.add(new UserLocation.Visit(location.getLatitude(), location.getLongitude(), GetAddressFromLatLang.getAddressFromLatLan(context, location.getLatitude(), location.getLongitude()), outlet.outletId, BatteryUtils.getBatteryLevelPercentage(context)));
        Log.d("tareq_test", "OutletRecyclerViewAdapter #244: sendUserLocation:  "+ new Gson().toJson(new UserLocation(userLocationVisits)));
        Call<ResponseBody> call = apiService.sendUserLocation(new UserLocation(userLocationVisits));
        call.enqueue(new Callback<ResponseBody>() {
            private final Intent intent = new Intent(context, SelfieCheck_inActivity.class).putExtra("outletId",outlet.outletId).putExtra("outlet", outlet);



            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.code() == 201) {
                    try {
                        UserLocation.Successful userLocationSuccess = null;
                        if (response.body() != null) {
                            userLocationSuccess = gson.fromJson(response.body().string(), UserLocation.Successful.class);
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        if (userLocationSuccess != null) {
                            if (userLocationSuccess.msg.equals("")) { // To check if the user already checked in the outlet
                                new SessionManager(context).updateLastCheckInDate(CurrentTimeUtilityClass.getCurrentTimeStampDate());
                                new SessionManager(context).updateLastCheckInTime(CurrentTimeUtilityClass.getCurrentTimeStamp2());


                                new  Flashbar.Builder(activity)
                                        .gravity(Flashbar.Gravity.TOP)
                                        .message("You have successfully checked in.")
                                        .backgroundDrawable(R.drawable.moss_gradient)
                                        .duration(3000)
                                        .build().show();
                                databaseHandler.afterCheckinUpdateOutletRoutePlan(outlet.outletId);

                                //region Update checkedin for Activities
                                UtilityDatabase utilityDatabase = (UtilityDatabase) UtilityDatabase.getInstance(context);
                                Date d = Calendar.getInstance().getTime();
                                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                String date = formatDate.format(d);
                                SimpleDateFormat formathour = new SimpleDateFormat("HH-mm", Locale.ENGLISH);
                                String hour = formathour.format(d);

                                OutletWithCheckInTime outletWithCheckInTime = new OutletWithCheckInTime(outlet, hour);

                                new Thread(() -> {
                                    List<RegularPerformanceEntity> regularPerformanceEntities = utilityDatabase.returnUtilityDao().getRegularPerformance(date);
                                    if (regularPerformanceEntities.size() == 0) {
                                        utilityDatabase.returnUtilityDao().insertRegularPerformance(new RegularPerformanceEntity.Builder().withDate(date).withDistance(0).withOutlets_checked_in(new Gson().toJson(outletWithCheckInTime) + "~;~").build());
                                    } else {

                                        String outlets = regularPerformanceEntities.get(0).outlets_checked_in + new Gson().toJson(outletWithCheckInTime) + "~;~";


                                        utilityDatabase.returnUtilityDao().updateRegularPerformanceOutlets(outlets, date);
                                    }
                                }).start();

                                //endregion


                                ((OutletActivity) context).getRoute();//For reloading the outlet recycler view
                                ((OutletActivity) context).getOutlets();//For reloading the outlet recycler view
                            } else {
                                new  Flashbar.Builder(activity)
                                        .gravity(Flashbar.Gravity.TOP)
                                        .message("You have already checked in.")
                                        .backgroundDrawable(R.drawable.moss_gradient)
                                        .duration(3000)
                                        .build().show();
                            }
                        }
                    } catch (IOException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Outlet Check-in Successful with empty result", Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 400) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "Outlet Check-in response code: " + response.code(), Toast.LENGTH_SHORT).show();

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    new  Flashbar.Builder(activity)
                            .gravity(Flashbar.Gravity.TOP)
                            .title("Sorry!")
                            .message("Server Error, Please try Selfie check-in.")
                           .castShadow()
                            .primaryActionText("Selfie")
                            .primaryActionTapListener(new Flashbar.OnActionTapListener() {
                                @Override
                                public void onActionTapped(Flashbar flashbar) {
                                    flashbar.dismiss();
                                    context.startActivity(intent);
                                }
                            })
                            .backgroundDrawable(R.drawable.moss_gradient2)
                            .build().show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                new  Flashbar.Builder(activity)
                        .gravity(Flashbar.Gravity.TOP)
                        .title("Sorry!")
                        .message("Network Error, Please try Selfie check-in.")
                        .castShadow()
                        .primaryActionText("Selfie")
                        .primaryActionTapListener(new Flashbar.OnActionTapListener() {
                            @Override
                            public void onActionTapped(Flashbar flashbar) {

                                flashbar.dismiss();
                                context.startActivity(intent);
                            }
                        })
                        .backgroundDrawable(R.drawable.moss_gradient2)
                        .build().show();
                Log.d("Response", t.toString());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(outlets!=null)
        return outlets.size();
        else
            return 0;
    }

    public class OutletViewHolder extends RecyclerView.ViewHolder {

        private OutletItemBinding mOutletItemBinding;


        public OutletViewHolder(View itemView) {
            super(itemView);
            mOutletItemBinding = DataBindingUtil.bind(itemView);


            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, OutletDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("outletID",outlet))
                intent.putExtra("outletDetails", outlets.get(getLayoutPosition()));
                context.startActivity(intent);
            });
        }

        public OutletItemBinding getBinding() {
            return mOutletItemBinding;
        }
    }


}
