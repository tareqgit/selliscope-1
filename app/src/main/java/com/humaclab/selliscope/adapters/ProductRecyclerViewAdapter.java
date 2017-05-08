package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.model.ProductResponse;

import java.util.List;

/**
 * Created by tonmoy on 5/6/17.
 */

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductsViewHolder> {
    private Context context;
    private List<ProductResponse.ProductResult> productResultList;

    public ProductRecyclerViewAdapter(Context context, List<ProductResponse.ProductResult> productResultList) {
        this.context = context;
        this.productResultList = productResultList;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position) {
        ProductResponse.ProductResult productResult = productResultList.get(position);
        holder.tv_product_name.setText(productResult.name);
        holder.tv_category.setText(productResult.category.name);
        holder.tv_brand.setText(productResult.brand.name);
        holder.tv_price.setText(String.valueOf(productResult.price));
        /*Picasso.with(context)
                .load(productResult.img)
                .error(R.drawable.ic_outlet_bnw)
                .placeholder(R.drawable.ic_outlet_bnw)
                .into(holder.iv_product);*/
        Glide.with(context).load(productResult.img)
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.ic_outlet_bnw)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_product);
    }

    @Override
    public int getItemCount() {
        return productResultList.size();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name, tv_category, tv_brand, tv_price;
        ImageView iv_product;
        Button btn_order;

        public ProductsViewHolder(View itemView) {
            super(itemView);
            iv_product = (ImageView) itemView.findViewById(R.id.iv_product);
            tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            tv_category = (TextView) itemView.findViewById(R.id.tv_category);
            tv_brand = (TextView) itemView.findViewById(R.id.tv_brand);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
        }
    }
}
