/*
 * Created by Tareq Islam on 6/9/19 4:07 PM
 *
 *  Last modified 6/9/19 4:07 PM
 */

package com.humaclab.lalteer.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.humaclab.lalteer.R;

/***
 * Created by mtita on 09,June,2019.
 */
public class MyDialog extends DialogFragment {


    public static MyDialog newInstance()
    {

        MyDialog myDialog=new MyDialog();
        Bundle args=new Bundle();

        myDialog.setArguments(args);
        return myDialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View v = inflater.inflate(R.layout.cus_dialog_header, container, false);
        getDialog().getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;

        //for having custom rounded background we need to change the default background into transparent
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        initFields();
        
        return v;

    }

    private void initFields() {
    }
}
