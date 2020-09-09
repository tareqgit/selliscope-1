package com.sokrio.sokrio_classic.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.ImageView;

import com.sokrio.sokrio_classic.R;

/***
 * Created by mtita on 11,November,2019.
 */
public class LoadingDialog {
    Activity mActivity;
    Dialog mDialog;

    public LoadingDialog(Activity activity) {
        mActivity = activity;
    }

    public void showDialog(){
        mDialog= new Dialog(mActivity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);

        mDialog.setContentView(R.layout.loading_dialog);

        ImageView loadingImage= mDialog.findViewById(R.id.custom_loading_image);

       mDialog.show();

    }


    public void hideDialog(){
        mDialog.dismiss();
    }
}
