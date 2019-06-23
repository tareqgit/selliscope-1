package com.humaclab.selliscope.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.ActivityCart;
import com.humaclab.selliscope.databinding.ItemSelectedProductBinding;
import com.humaclab.selliscope.helper.SelectedProductHelper;

import java.util.List;

import static com.humaclab.selliscope.activity.OrderActivity.selectedProductList;

public class SelectedProductRecyclerAdapter extends RecyclerView.Adapter<SelectedProductRecyclerAdapter.SelectedProductViewHolder> {
    private ActivityCart activityCart;
    private Context context;

  //  private List<SelectedProductHelper> selectedProductList;

    OnRemoveFromCartListener mOnRemoveFromCartListener;

    public SelectedProductRecyclerAdapter(Context context, ActivityCart activityCart, List<SelectedProductHelper> selectedProductList) {
        this.context = context;
        this.activityCart = activityCart;
      //  this.selectedProductList = selectedProductList;

    }
    public SelectedProductRecyclerAdapter(Context context, ActivityCart activityCart, OnRemoveFromCartListener onRemoveFromCartListener) {
        this.context = context;
        this.activityCart = activityCart;

        this.mOnRemoveFromCartListener = onRemoveFromCartListener;

    }

    @Override
    public SelectedProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_product, parent, false);
        return new SelectedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectedProductViewHolder holder, final int position) {
        final SelectedProductHelper selectedProduct = selectedProductList.get(position);
        holder.getBinding().setVariable(BR.selectedProduct, selectedProduct);
        holder.getBinding().btnRemove.setOnClickListener(v -> {
            if(selectedProductList.size()!=0) {
                selectedProductList.remove(position);
                SelectedProductRecyclerAdapter.this.notifyItemRemoved(position);
                SelectedProductRecyclerAdapter.this.notifyItemRangeChanged(position, selectedProductList.size());
                mOnRemoveFromCartListener.onRemoveSelectedProduct(selectedProduct);
            }});
    }

    @Override
    public int getItemCount() {
        return selectedProductList.size();
    }

    public class SelectedProductViewHolder extends RecyclerView.ViewHolder {
        private ItemSelectedProductBinding binding;

        public SelectedProductViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ItemSelectedProductBinding getBinding() {
            return binding;
        }
    }


    public interface OnRemoveFromCartListener{
        /**
         * Update UI when product is removed from order
         *
         * @param selectedProduct SelectedProductHelper removed product details
         */
        void onRemoveSelectedProduct(SelectedProductHelper selectedProduct);
    }
}
