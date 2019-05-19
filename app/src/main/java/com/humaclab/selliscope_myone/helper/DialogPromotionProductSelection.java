package com.humaclab.selliscope_myone.helper;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.humaclab.selliscope_myone.R;

/**
 * Created by leon on 9/4/18.
 */

public class DialogPromotionProductSelection {
    private Context context;
    private AlertDialog builder;
    private RecyclerView rv_promotion_products;
    private ImageView civ_cancel;

    public DialogPromotionProductSelection(Context context) {
        this.context = context;
        this.builder = new AlertDialog.Builder(context, R.style.Theme_Design_Light).create();
    }

    public void showDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_promotion_product_selection, null);
        builder.setView(dialogView);

        rv_promotion_products = dialogView.findViewById(R.id.rv_promotion_products);
        rv_promotion_products.setLayoutManager(new LinearLayoutManager(context));

        civ_cancel = dialogView.findViewById(R.id.civ_cancel);
        civ_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });


        builder.show();
    }
}
