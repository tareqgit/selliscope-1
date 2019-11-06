/*
 * Created by Tareq Islam on 11/6/19 4:01 PM
 *
 *  Last modified 11/6/19 4:01 PM
 */

package com.humaclab.lalteer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * Created by mtita on 06,November,2019.
 */
public class DateDiffUtils {


    public static void main(String[] args) {
        try {
            System.out.println( getDifferenceDays("10/10/2018","10/10/2019 16:30:10","dd/MM/yyyy"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    //1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400
    public static long getDifferenceMiliseconds(String startDate, String endDate, String dateTimePattern) throws ParseException {



      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimePattern);
            Date date1 = simpleDateFormat.parse(startDate);
            Date date2 = simpleDateFormat.parse(endDate);



        //milliseconds
        long different = date2.getTime() - date1.getTime();

        return different;





    }


    public static long getDifferenceSeconds(String startDate, String endDate, String dateTimePattern) throws ParseException {



            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimePattern);
            Date date1 = simpleDateFormat.parse(startDate);
            Date date2 = simpleDateFormat.parse(endDate);



            //milliseconds
            long different = date2.getTime() - date1.getTime();



            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;
            return elapsedSeconds;


    }

    public static long getDifferenceMinutes(String startDate, String endDate, String dateTimePattern) throws ParseException {



            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimePattern);
            Date date1 = simpleDateFormat.parse(startDate);
            Date date2 = simpleDateFormat.parse(endDate);



            //milliseconds
            long different = date2.getTime() - date1.getTime();



            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;

            return elapsedMinutes;



    }

    public static long getDifferenceHours(String startDate, String endDate, String dateTimePattern) throws ParseException {



            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimePattern);
            Date date1 = simpleDateFormat.parse(startDate);
            Date date2 = simpleDateFormat.parse(endDate);



            //milliseconds
            long different = date2.getTime() - date1.getTime();



            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;

            return elapsedHours;





    }

    public static long getDifferenceDays(String startDate, String endDate, String dateTimePattern) throws ParseException {



            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimePattern);
            Date date1 = simpleDateFormat.parse(startDate);
            Date date2 = simpleDateFormat.parse(endDate);



            //milliseconds
            long different = date2.getTime() - date1.getTime();



            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            return elapsedDays;


    }

}
