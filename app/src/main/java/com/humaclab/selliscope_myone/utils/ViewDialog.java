/*
 * Created by Tareq Islam on 5/25/19 1:37 PM
 *
 *  Last modified 5/25/19 1:37 PM
 */

package com.humaclab.selliscope_myone.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.humaclab.selliscope_myone.R;

/***
 * Created by mtita on 25,May,2019.
 */
public class ViewDialog {
    Activity activity;
    Dialog dialog;
    public ViewDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {

        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loading_view_dialog);

        ImageView gifImageView = dialog.findViewById(R.id.custom_loading_imageView);

       // GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);
        Glide.with(activity)
                .load(R.drawable.loading)

                .placeholder(R.drawable.loading)
                .centerCrop()

                .into(gifImageView);

        dialog.show();
    }

    public void hideDialog(){
        dialog.dismiss();
    }
}
