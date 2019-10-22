package com.humaclab.selliscope.utils;

import android.content.Context;
import android.os.BatteryManager;


import static android.content.Context.BATTERY_SERVICE;


/***
 * Created by mtita on 22,October,2019.
 */
public class BatteryUtils {

    public static int getBatteryLevel(Context context){
        //region Battery Percentage
        BatteryManager bm = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        return batLevel;
        //endregion
    }
}
