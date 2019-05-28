/*
 * Created by Tareq Islam on 3/19/19 4:52 PM
 *
 *  Last modified 3/19/19 4:52 PM
 */

package com.humaclab.selliscope_myone.productDialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.model.variantProduct.ProductsItem;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by mtita on 19,March,2019.
 */
public class ProductDialogAdapter extends RecyclerView.Adapter<ProductDialogAdapter.MyHolder> {

    OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    Context c;
    List<ProductsItem> products = new ArrayList<>();

    public ProductDialogAdapter(Context c, List<ProductsItem> products) {
        this.c = c;
        this.products = products;
    }

    public ProductDialogAdapter( Context c, List<ProductsItem> products, OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        this.c = c;
        this.products = products;
    }

    public void updateData(List<ProductsItem> products){
        this.products=products;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_dialog_recycler_model,parent,false);
        MyHolder holder=new MyHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.nameTxt.setText(products.get(position).getName());
        holder.priceTxt.setText("Price: "+ products.get(position).getPrice());
        holder.stockTxt.setText("Stock: "+ products.get(position).getStock());
        Glide.with(holder.productPic)
                .load(R.drawable.app_logo)
                .into(holder.productPic);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder  {
        ImageView productPic;
        TextView nameTxt, stockTxt, priceTxt;

        public MyHolder(View itemView) {
            super(itemView);
            nameTxt= (TextView) itemView.findViewById(R.id.nameTxt);
            priceTxt =itemView.findViewById(R.id.priceText);
            stockTxt=itemView.findViewById(R.id.stockType);

            productPic=itemView.findViewById(R.id.productimageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(products.get(getAdapterPosition()));
                }
            });
        }


    }

    public interface OnItemClickListener{
        void onClick(ProductsItem productsItem);
    }
}
