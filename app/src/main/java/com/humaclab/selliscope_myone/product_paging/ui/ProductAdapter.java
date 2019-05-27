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
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.productDialog.ProductDialogAdapter;
import com.humaclab.selliscope_myone.product_paging.model.ProductsItem;

/***
 * Created by mtita on 22,May,2019.
 */
public class ProductAdapter extends PagedListAdapter<ProductsItem, ProductAdapter.ProductsViewHolder> {

    ProductAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

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
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_dialog_recycler_model, parent, false);
        return new ProductsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position) {
        ProductsItem product = getItem(position);
        try {
            holder.nameTxt.setText(product.id.toString());
            holder.priceTxt.setText("Price: "+ product.price);
            holder.stockTxt.setText("Stock: "+ product.stockType);
            Glide.with(holder.productPic)
                    .load(R.drawable.app_logo)
                    .into(holder.productPic);
        } catch (Exception e) {
            Log.d("tareq_test" , ""+e.getMessage());
            e.printStackTrace();
        }
    }




    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        ImageView productPic;
        TextView nameTxt, stockTxt, priceTxt;

        public ProductsViewHolder(View itemView) {
            super(itemView);
            nameTxt= (TextView) itemView.findViewById(R.id.nameTxt);
            priceTxt =itemView.findViewById(R.id.priceText);
            stockTxt=itemView.findViewById(R.id.stockText);

            productPic=itemView.findViewById(R.id.productimageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   mOnItemClickListener.onClick(products.get(getAdapterPosition()));
                    mOnItemClickListener.onClick( getItem(getAdapterPosition()));
                }
            });
        }


    }

    public interface OnItemClickListener{
        void onClick(com.humaclab.selliscope_myone.product_paging.model.ProductsItem productsItem);
    }
}
