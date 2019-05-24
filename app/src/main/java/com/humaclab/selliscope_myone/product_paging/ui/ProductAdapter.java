/*
 * Created by Tareq Islam on 5/23/19 2:19 PM
 *
 *  Last modified 5/23/19 1:36 PM
 */

/*
 * Created by Tareq Islam on 5/22/19 2:58 PM
 *
 *  Last modified 5/22/19 2:58 PM
 */

package com.humaclab.selliscope_myone.product_paging.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.activity.OrderActivity;
import com.humaclab.selliscope_myone.activity.OutletDetailsActivity;
import com.humaclab.selliscope_myone.model.UserLocation;
import com.humaclab.selliscope_myone.outlet_paging.model.OutletItem;
import com.humaclab.selliscope_myone.product_paging.model.ProductsItem;
import com.humaclab.selliscope_myone.utils.GetAddressFromLatLang;
import com.humaclab.selliscope_myone.utils.NetworkUtility;
import com.humaclab.selliscope_myone.utils.SendUserLocationData;
import com.humaclab.selliscope_myone.utils.SessionManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/***
 * Created by mtita on 22,May,2019.
 */
public class ProductAdapter extends PagedListAdapter<ProductsItem, ProductAdapter.ProductsViewHolder> {

    /**
     * DiffUtil to compare the Repo data (old and new)
     * for issuing notify commands suitably to update the list
     */
    private static DiffUtil.ItemCallback<ProductsItem> REPO_COMPARATOR
            = new DiffUtil.ItemCallback<ProductsItem>() {
        @Override
        public boolean areItemsTheSame(ProductsItem oldItem, ProductsItem newItem) {
            return oldItem.name.equals(newItem.name);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(ProductsItem oldItem, ProductsItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    private Context mContext;
    private Activity mActivity;

    public ProductAdapter(Context context, Activity activity) {

        super(REPO_COMPARATOR);
        mContext=context;
        mActivity = activity;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position) {
        ProductsItem productResult = getItem(position);

        holder.tv_product_name.setText(productResult.name);
        holder.tv_category.setText(productResult.category.toString());
        holder.tv_brand.setText(productResult.brand.toString());
        holder.tv_price.setText(String.valueOf(productResult.price));

        /*if (productResult.isHasVariant()) {
            holder.ll_price.setVisibility(View.GONE);
            holder.btn_order.setVisibility(View.GONE);
        }*/

        Glide.with(mContext).load(productResult.img)
                .thumbnail(0.5f)
                .into(holder.iv_product);

       /* holder.iv_product_promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.product_promotion_dialog);
                dialog.setTitle("Product Promotion");
                TextView tv_product_description = dialog.findViewById(R.id.tv_product_promotion);
                TextView tv_product_usp = dialog.findViewById(R.id.tv_product_usp);
                TextView tv_product_tips = dialog.findViewById(R.id.tv_product_tips);
                try {
                    tv_product_description.setText(productResult.getPromotion().getDiscount());
                } catch (Exception e) {
                    e.printStackTrace();
                    tv_product_description.setText("This product has no Promotion offer yet.");
                }
                try {
                    tv_product_usp.setText(productResult.getPitch().getUsp());
                } catch (Exception e) {
                    e.printStackTrace();
                    tv_product_usp.setText("This product has no USP yet.");
                }
                try {
                    tv_product_tips.setText(productResult.getPitch().getTips());
                } catch (Exception e) {
                    e.printStackTrace();
                    tv_product_tips.setText("This product has no Tips yet.");
                }
                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });*/

        try {
            holder.btn_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OrderActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("productName", productResult.name);
                    intent.putExtra("productID", productResult.id);
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_price;
        TextView tv_product_name, tv_category, tv_brand, tv_price;
        ImageView iv_product, iv_product_promo;
        Button btn_order;

        public ProductsViewHolder(View itemView) {
            super(itemView);
            ll_price = itemView.findViewById(R.id.ll_price);
            iv_product = itemView.findViewById(R.id.iv_product);
            iv_product_promo = itemView.findViewById(R.id.iv_product_promo);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_brand = itemView.findViewById(R.id.tv_brand);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_order = itemView.findViewById(R.id.btn_order);
        }
    }

}
