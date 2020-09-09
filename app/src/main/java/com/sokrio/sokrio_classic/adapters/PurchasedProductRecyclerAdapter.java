package com.sokrio.sokrio_classic.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sokrio.sokrio_classic.R;
import com.sokrio.sokrio_classic.databinding.ItemPurchaseHistoryProductListBinding;
import com.sokrio.sokrio_classic.model.purchase_history.OrderDetailsItem;
import com.sokrio.sokrio_classic.sales_return.model.get.DataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leon on 11/3/18.
 */

public class PurchasedProductRecyclerAdapter extends RecyclerView.Adapter<PurchasedProductRecyclerAdapter.PurchasedProductViewHolder> {
    private Context context;
    private List<OrderDetailsItem> orderDetails;
    private List<DataItem> salesDataItems;
    private List<Object> productsList =new ArrayList<>();

    public PurchasedProductRecyclerAdapter(Context context, List<OrderDetailsItem> orderDetails, List<DataItem> salesReturnDataItems) {
        this.context = context;
        this.orderDetails = orderDetails;
        this.salesDataItems = salesReturnDataItems;

        productsList.addAll(orderDetails);
        productsList.addAll(salesReturnDataItems);

        Log.d("tareq_test" , "sales data Item" + new Gson().toJson(productsList));
    }

    @Override
    public PurchasedProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_history_product_list, parent, false);
        return new PurchasedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PurchasedProductViewHolder holder, int position) {
        if(isItOrder(productsList.get(position))) {

            holder.getBinding().textViewReturn.setVisibility(View.GONE);
            holder.getBinding().tvSkuHolder.setVisibility(View.GONE);
            holder.getBinding().tvSkuNo.setVisibility(View.GONE);
           holder.getBinding().textViewDate.setVisibility(View.GONE);

            holder.getBinding().tvProductDiscount.setText("Discount: ");

            OrderDetailsItem orderDetailsItem = (OrderDetailsItem) productsList.get(position);
            try {
                if(orderDetailsItem.getIs_free()==1)      holder.getBinding().textViewFree.setVisibility(View.VISIBLE);
                holder.getBinding().tvProductName.setText(orderDetailsItem.getProductName());
                holder.getBinding().tvProductDiscount.setText(orderDetailsItem.getProductDiscount());

                holder.getBinding().tvProductPrice.setText(orderDetailsItem.getProductPrice());
                holder.getBinding().tvProductQuantity.setText(String.valueOf(orderDetailsItem.getProductQty()));
                holder.getBinding().tvProductRate.setText(String.valueOf(orderDetailsItem.getProductRate()));
            } catch (Exception e) {
                Log.e("tareq_test", "purchase history adapter order Item binding error: " + e.getMessage());
            }
        }

        if(isItReturn(productsList.get(position))){
            Log.d("tareq_test" , "matched asdjasdpajsdpasjdpa");
            holder.getBinding().textViewReturn.setVisibility(View.VISIBLE);
            holder.getBinding().tvSkuHolder.setVisibility(View.VISIBLE);
            holder.getBinding().tvSkuNo.setVisibility(View.VISIBLE);
            holder.getBinding().textViewDate.setVisibility(View.VISIBLE);
            holder.getBinding().textViewFree.setVisibility(View.GONE);
            holder.getBinding().tvProductDiscount.setText("Return Cause: ");

            DataItem salesReturnDataItem = (DataItem) productsList.get(position);

            try {
                holder.getBinding().tvProductName.setText(salesReturnDataItem.getProduct());

                holder.getBinding().tvProductPrice.setText(String.valueOf(salesReturnDataItem.getQty()* salesReturnDataItem.getRate()));
                holder.getBinding().tvProductQuantity.setText(String.valueOf(salesReturnDataItem.getQty()));
                holder.getBinding().tvProductRate.setText(String.valueOf(salesReturnDataItem.getRate()));
                holder.getBinding().tvSkuNo.setText(String.valueOf(salesReturnDataItem.getSku()));
                holder.getBinding().tvProductDiscount.setText(salesReturnDataItem.getCause());
                holder.getBinding().textViewDate.setText(salesReturnDataItem.getDate());
            } catch (Exception e) {
                Log.e("tareq_test", "purchase history adapter Sales Return Item binding error: " + e.getMessage());
            }
        }
    }



    private boolean isItOrder(Object object){
        try {
            OrderDetailsItem selectedProductHelper=  (OrderDetailsItem) object;

        }catch (Exception ex){
            return false;
        }
        return true;
    }

    private boolean isItReturn(Object object){
        try {
            DataItem selectedProductHelper=  (DataItem) object;

        }catch (Exception ex){
            return false;
        }
        return true;
    }


    @Override
    public int getItemCount() {
        //return orderDetails.size();
        return (productsList == null) ? 0 : productsList.size();
    }

    public class PurchasedProductViewHolder extends RecyclerView.ViewHolder {
        private ItemPurchaseHistoryProductListBinding binding;

        public PurchasedProductViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ItemPurchaseHistoryProductListBinding getBinding() {
            return binding;
        }
    }
}
