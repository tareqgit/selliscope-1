package com.humaclab.selliscope.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.receiver.ActionReceiver;
import com.humaclab.selliscope.utils.Constants;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/***
 * Created by mtita on 10,October,2019.
 */
public class NotificationHandler extends Worker {

        //constructor
    public NotificationHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }


    public static void scheduleReminder(Context context, long duration, Data data, String tag) {

        OneTimeWorkRequest notificationWork= new OneTimeWorkRequest.Builder(NotificationHandler.class)
        //periodicWorkRequest
   //     PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(NotificationHandler.class,1,TimeUnit.DAYS)
                .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .setInputData(data).build();
        WorkManager instance = WorkManager.getInstance(context);
      //  instance.enqueueUniquePeriodicWork("notification", ExistingPeriodicWorkPolicy.REPLACE,notificationWork);
        instance.enqueue(notificationWork);
    }


    public static void cancelReminder(Context context,String tag) {
        WorkManager instance = WorkManager.getInstance(context);
        instance.cancelAllWorkByTag(tag);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString(Constants.EXTRA_TITLE);
        String text = getInputData().getString(Constants.EXTRA_TEXT);
        int id = (int) getInputData().getLong(Constants.EXTRA_ID, 0);

        sendNotification(title, text, id);
        return Result.success();
    }

    private void sendNotification(String title, String text, int id) {
        //This is the intent of PendingIntent
        Intent intent = new Intent(getApplicationContext(), ActionReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.EXTRA_ID, id);

        //This is optional if you have more than one buttons and want to differentiate between two
        intent.putExtra("notificationId",id);
        intent.putExtra("action","actionUpload");

        //PendingIntent pendingIntent  = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntent =PendingIntent.getBroadcast(getApplicationContext(),1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_HIGH);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")

                .setContentTitle(title)
                .setContentText(text)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.danger))
                //.setContentIntent(pendingIntent)
                 .setSmallIcon(R.drawable.ic_selliscope_icon)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_file_upload_black_24dp, "Upload Data", pendingIntent);


        Objects.requireNonNull(notificationManager).notify(id, notification.build());
    }

}
