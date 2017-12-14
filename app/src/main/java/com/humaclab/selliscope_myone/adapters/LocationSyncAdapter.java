package com.humaclab.selliscope_myone.adapters;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import timber.log.Timber;

/**
 * Created by leon on 8/30/17.
 */

public class LocationSyncAdapter extends AbstractThreadedSyncAdapter {
    private Context context;

    public LocationSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;

        sendLocationToServer();
        Timber.d("Sync adapter is running");
    }

    private void sendLocationToServer() {
        Timber.d("User Location Tracking Service on sync adapter");
        /*SendUserLocationData sendUserLocationData = new SendUserLocationData(context);
        sendUserLocationData.getLocation();*/
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    }
}
