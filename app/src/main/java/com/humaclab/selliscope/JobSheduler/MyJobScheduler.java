package com.humaclab.selliscope.JobSheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.model.UserLocation;
import com.humaclab.selliscope.utils.CurrentTimeUtilityClass;
import com.humaclab.selliscope.utils.GetAddressFromLatLang;
import com.humaclab.selliscope.utils.NetworkUtility;
import com.humaclab.selliscope.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class MyJobScheduler extends JobService {
    private MyExecuteTask myExecuteTask;
    private Context context = this;
    SessionManager sessionManager;
    @Override
    public void onCreate() {
        super.onCreate();


    }


    @Override
    public boolean onStartJob(final JobParameters params) {



        sessionManager =  new SessionManager(context);
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy);

        SmartLocation.with(context)
                .location()
                .oneFix()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        double latitude = Double.parseDouble(String.format("%.05f", location.getLatitude()));
                        double longitude = Double.parseDouble(String.format("%.05f", location.getLongitude()));

                        Timber.d("Latitude: " + latitude + " Longitude: " + longitude);
                        if (NetworkUtility.isNetworkAvailable(context)) {
                            sendUserLocation(
                                    latitude,
                                    longitude,
                                    CurrentTimeUtilityClass.getCurrentTimeStamp(),
                                    false,
                                    -1);
                                       /* List<UserVisit> userVisits = dbHandler.getUSerVisits();
                                        if (!userVisits.isEmpty())
                                            for (UserVisit userVisit : userVisits) {
                                                sendUserLocation(
                                                        userVisit.getLatitude(),
                                                        userVisit.getLongitude(),
                                                        userVisit.getTimeStamp(),
                                                        true,
                                                        userVisit.getVisitId()
                                                );
                                            }*/
                        }
                                    /*else {
                                        dbHandler.addUserVisits(new UserVisit(latitude, longitude, CurrentTimeUtilityClass.getCurrentTimeStamp()));
                                        Timber.d("User Location Saved in Database");
                                    }*/

                    }
                });


        Toast.makeText(context," Done",Toast.LENGTH_LONG);
        Log.d("ok","oo");
        jobFinished(params,false);


        /*myExecuteTask = new MyExecuteTask(context)
        {
            @Override
            protected void onPostExecute(String s) {
            }
        };
        myExecuteTask.execute();*/
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        myExecuteTask.cancel(true);
        return false;
    }



    private void sendUserLocation(double latitude, double longitude, String timeStamp, final boolean fromDB, final int visitId) {
        SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        List<UserLocation.Visit> userLocationVisits = new ArrayList<>();
        userLocationVisits.add(new UserLocation.Visit(latitude, longitude, GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), latitude, longitude), timeStamp));
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
                        /*if (fromDB)
                            dbHandler.deleteUserVisit(visitId);*/
                    } catch (IOException e) {
                        Timber.d("Error:" + e.toString());
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {

                } else {
                    Toast.makeText(getApplicationContext(), response.code() + " Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Response", t.toString());
            }
        });
    }

}
