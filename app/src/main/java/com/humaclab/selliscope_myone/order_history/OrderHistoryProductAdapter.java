/*
 * Created by Tareq Islam on 5/28/19 3:26 PM
 *
 *  Last modified 5/28/19 3:26 PM
 */

package com.humaclab.selliscope_myone.order_history;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.databinding.ActivityOrderHistoryProductModelBinding;
import com.humaclab.selliscope_myone.order_history.api.response_model.ProductsItem;

import java.util.List;

/***
 * Created by mtita on 28,May,2019.
 */
public class OrderHistoryProductAdapter extends RecyclerView.Adapter<OrderHistoryProductAdapter.TViewHolder>{

    private Context mContext;
    private List<ProductsItem> mProductList;


    public OrderHistoryProductAdapter(Context context, List<ProductsItem> productList) {
        mContext = context;
        mProductList = productList;
    }



    @NonNull
    @Override
    public TViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.activity_order_history_product_model,viewGroup,false);
        return new TViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TViewHolder tViewHolder, int i) {
        ProductsItem productsItem = mProductList.get(i);
        tViewHolder.getBinding().setData(productsItem);
    }

    @Override
    public int getItemCount() {
        if(mProductList!=null)
            return mProductList.size();
        else
            return 0;
    }

    public class TViewHolder extends RecyclerView.ViewHolder{
        private ActivityOrderHistoryProductModelBinding mBinding;

        public TViewHolder(View itemView) {
            super(itemView);
            mBinding= DataBindingUtil.bind(itemView);
        }

        public ActivityOrderHistoryProductModelBinding getBinding() {
            return mBinding;
        }
    }
}
