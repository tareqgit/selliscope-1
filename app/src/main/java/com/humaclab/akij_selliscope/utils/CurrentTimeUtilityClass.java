package com.humaclab.akij_selliscope.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by dipu_ on 4/22/2017.
 */

public class CurrentTimeUtilityClass {
    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
