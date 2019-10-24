package com.humaclab.selliscope.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.dbmodel.UserVisit;
import com.humaclab.selliscope.model.UserLocation;
import com.humaclab.selliscope.utility_db.db.UtilityDatabase;
import com.humaclab.selliscope.utils.BatteryUtils;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.GetAddressFromLatLang;
import com.humaclab.selliscope.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Response;

/***
 * Created by mtita on 29,September,2019.
 */
public class LocationSyncerWorker extends Worker {

    UtilityDatabase mUtilityDatabase;
    SessionManager sessionManager;
    private DatabaseHandler dbHandler ;
    Context mContext;

    public LocationSyncerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext=context;
        sessionManager = new SessionManager(context);
        dbHandler= new DatabaseHandler(context);
        mUtilityDatabase = (UtilityDatabase) UtilityDatabase.getInstance(context);

    }


    @NonNull
    @Override
    public Result doWork() {



        SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                sessionManager.getUserPassword(), false)
                .create(SelliscopeApiEndpointInterface.class);
        List<UserLocation.Visit> userLocationVisits = getVisits();

        Response<ResponseBody> response = null;
        try {
            response = apiService.sendUserLocation(new UserLocation(userLocationVisits)).execute();
        } catch (IOException e) {
            Log.d("tareq_test", "LocationSyncerWorker #54: doWork:  " + e.getMessage());
            e.printStackTrace();
        }

        if(response.isSuccessful()){
            Log.d("tareq_test", "LocationSyncerWorker #60: doWork:   success");
            dbHandler.deleteUserVisit();
            mUtilityDatabase.returnCheckInDao().deleteAllCheck_In();
            return Result.success();
            }else{
                if(response.code() >=500 && response.code()<=599){
                    Log.d("tareq_test", "LocationSyncerWorker #64: doWork:   retry");
                    return Result.retry();
                }
            Log.d("tareq_test", "LocationSyncerWorker #67: doWork:  failed");
            return  Result.failure();
            }

    }

    private List<UserLocation.Visit> getVisits() {
        List<UserLocation.Visit> userLocationVisits = new ArrayList<>();
        //getting data from sqlite database
        for (UserVisit userVisit : dbHandler.getUSerVisits()) {
            userLocationVisits.add(new UserLocation.Visit(userVisit.getLatitude(), userVisit.getLongitude(), GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), userVisit.getLatitude(), userVisit.getLongitude()), userVisit.getTimeStamp(),userVisit.getBattery_status()));
        }

        if( mUtilityDatabase.returnCheckInDao().getCheckInList().size()>0){
            for (UserLocation.Visit userVisit :  mUtilityDatabase.returnCheckInDao().getCheckInList()) {
                userLocationVisits.add(new UserLocation.Visit(userVisit.latitude, userVisit.longitude, GetAddressFromLatLang.getAddressFromLatLan(getApplicationContext(), userVisit.latitude, userVisit.longitude), userVisit.timeStamp, userVisit.outletId,userVisit.img, userVisit.comment, BatteryUtils.getBatteryLevelPercentage(mContext)));
            }
        }
        return userLocationVisits;
    }
}
