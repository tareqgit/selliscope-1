/*
 * Created by Tareq Islam on 3/27/19 3:53 PM
 *
 *  Last modified 3/27/19 3:53 PM
 */

package com.easyopstech.easyops.performance.orders;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.ActivityPerformanceOrdersProductsModelBinding;
import com.easyopstech.easyops.model.performance.orders_model.Product;

import java.util.List;

/***
 * Created by mtita on 27,March,2019.
 */
public class PerformanceOrdersProductsActivityAdapter extends RecyclerView.Adapter<PerformanceOrdersProductsActivityAdapter.TViewHolder>{

    private Context mContext;
    private List<Product> mProductList;

    public PerformanceOrdersProductsActivityAdapter(Context context, List<Product> productList) {
        mContext = context;
        mProductList = productList;
    }

    @NonNull
    @Override
    public TViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(mContext).inflate(R.layout.activity_performance_orders_products_model,parent,false);
        return new TViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TViewHolder holder, int position) {
        Product product=mProductList.get(position);
        holder.getBinding().setProduct( product);
    }

    @Override
    public int getItemCount() {

        if(mProductList!=null)
        return mProductList.size();
        else
            return 0;
    }

    public class TViewHolder extends RecyclerView.ViewHolder{
        private ActivityPerformanceOrdersProductsModelBinding mBinding;
        public TViewHolder(View itemView) {
            super(itemView);
            mBinding= DataBindingUtil.bind(itemView);
        }

        public ActivityPerformanceOrdersProductsModelBinding getBinding() {
            return mBinding;
        }
    }
}
