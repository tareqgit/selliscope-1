package com.humaclab.selliscope.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;


import com.humaclab.selliscope.activity.HomeActivity;
import com.humaclab.selliscope.service.SendLocationDataService;

/**
 * Created by leon on 9/26/17.
 */

public class CheckAppUpdated {

    /*public static void checkAppUpdate(final Context context) {
        final SessionManager sessionManager = new SessionManager(context);
        final ProgressDialog pd = new ProgressDialog(context);

        AppUpdater appUpdater = new AppUpdater(context)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("Check out the latest version available on play store")
                .setTitleOnUpdateNotAvailable("Latest Update")
                .setContentOnUpdateNotAvailable("You have the latest update.")
                .setButtonUpdate("Update now?")
                .setButtonUpdateClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                        pd.setMessage("Logging out...");
                        pd.show();
                        sessionManager.logoutUser(true);
                        HomeActivity.schedulerForMinute.shutdownNow();
                        HomeActivity.schedulerForHour.shutdownNow();
                        context.stopService(new Intent(context, SendLocationDataService.class));
                        context.deleteDatabase(Constants.databaseName);
                        context.unregisterReceiver(HomeActivity.receiver);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    pd.dismiss();
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        }, 5000);
                    }
                })
                .setButtonDismiss(null)
                .setButtonDoNotShowAgain(null)
                .setCancelable(false); // Dialog could not be dismissable
        appUpdater.start();
    }*/
}
