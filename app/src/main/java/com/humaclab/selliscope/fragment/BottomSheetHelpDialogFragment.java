/*
 * Created by Tareq Islam on 7/28/19 3:30 PM
 *
 *  Last modified 7/28/19 3:30 PM
 */

package com.humaclab.selliscope.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.humaclab.selliscope.R;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;

import java.util.Objects;

/***
 * Created by mtita on 28,July,2019.
 */
public class BottomSheetHelpDialogFragment extends BottomSheetDialogFragment {

    public BottomSheetHelpDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_help_bottom_sheet_dialog,
                container, false);

        PushDownAnim.setPushDownAnimTo(inflate.findViewById(R.id.textView_call)).setOnSingleClickListener(v->{
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:8801707073175"));

            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);
        });

       PushDownAnim.setPushDownAnimTo(inflate.findViewById(R.id.textView_text)).setOnSingleClickListener(v->{
           String contact = "+88 01707073175"; // use country code with your phone number
           String url = "https://api.whatsapp.com/send?phone=" + contact;
           try {
               PackageManager pm = Objects.requireNonNull(getContext()).getPackageManager();
               pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
               Intent i = new Intent(Intent.ACTION_VIEW);
               i.setData(Uri.parse(url));
               startActivity(i);
           } catch (PackageManager.NameNotFoundException e) {
               Toast.makeText(getContext(), "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
               e.printStackTrace();
           }
        });
        return inflate;


    }
}
