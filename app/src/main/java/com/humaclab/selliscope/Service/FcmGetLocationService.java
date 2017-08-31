package com.humaclab.selliscope.Service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.humaclab.selliscope.Utils.SendUserLocationData;

/**
 * Created by leon on 8/28/17.
 */

public class FcmGetLocationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SendUserLocationData sendUserLocationData = new SendUserLocationData(getApplicationContext());
        if (sendUserLocationData.getLocation()) {
            sendUserLocationData.getInstantLocation();
        }

        System.out.println("Firebase return: " + new Gson().toJson(remoteMessage));
    }
}
