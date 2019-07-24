package com.humaclab.selliscope.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dipu_ on 4/22/2017.
 */

public class CurrentTimeUtilityClass {
    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH).format(Calendar.getInstance().getTime());
    }
    public static String getCurrentTimeStampDate() {
        return new SimpleDateFormat("yyyy-M-d",Locale.ENGLISH).format(Calendar.getInstance().getTime());
    }

    public static long getDiffBetween(String preTime1){

        long diff=-1;
        try {
            Date preTime = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy").parse(preTime1);
            Date nowTime = Calendar.getInstance().getTime();
            diff=   (nowTime.getTime() - preTime.getTime())  / (60 * 1000) % 60 ;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return diff;

    }
}
