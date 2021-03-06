package com.easyopstech.easyops.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.activity.OrderActivity;
import com.easyopstech.easyops.model.variant_product.ProductsItem;

import java.util.List;

/**
 * Created by leon on 5/6/17.
 */

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductsViewHolder> {
    private Context context;
    private List<ProductsItem> productsItemList;

    public ProductRecyclerViewAdapter(Context context, List<ProductsItem> productsItemList) {
        this.context = context;
        this.productsItemList = productsItemList;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position) {
        final ProductsItem productResult = productsItemList.get(position);
        holder.tv_product_name.setText(productResult.getName());
        holder.tv_category.setText(productResult.getCategory().getName());
        holder.tv_brand.setText(productResult.getBrand().getName());
        holder.tv_price.setText(String.valueOf(productResult.getPrice()));

        if (productResult.isHasVariant()) {
            holder.ll_price.setVisibility(View.GONE);
            holder.btn_order.setVisibility(View.GONE);
        }
        /*Picasso.with(context)
                .load(productResult.img)
                .error(R.drawable.ic_outlet_bnw)
                .placeholder(R.drawable.ic_outlet_bnw)
                .into(holder.iv_product);*/
        Glide.with(context).load(productResult.getImg())
                .thumbnail(0.5f)
                .into(holder.iv_product);

        holder.iv_product_promo.setOnClickListener(new View.OnClickListener() {
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
        });

        try {
            holder.btn_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("productName", productResult.getName());
                    intent.putExtra("productID", productResult.getId());
                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productsItemList.size();
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
