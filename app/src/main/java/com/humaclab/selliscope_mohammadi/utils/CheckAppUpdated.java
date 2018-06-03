package com.humaclab.selliscope_mohammadi.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

/**
 * Created by leon on 9/26/17.
 */

public class CheckAppUpdated {

    public static void checkAppUpdate(final Context context) {
        AppUpdater appUpdater = new AppUpdater(context)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .showEvery(5)
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("Check out the latest version available on play store")
                .setTitleOnUpdateNotAvailable("Latest Update")
                .setButtonUpdate("Update now?")
                .setButtonUpdateClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .setButtonDismiss(null)
                .setButtonDoNotShowAgain(null)
                .setCancelable(false); // Dialog could not be dismissable
        appUpdater.start();
    }
}
