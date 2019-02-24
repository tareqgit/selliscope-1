package com.humaclab.akij_selliscope.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.akij_selliscope.BR;
import com.humaclab.akij_selliscope.R;
import com.humaclab.akij_selliscope.activity.ActivityCart;
import com.humaclab.akij_selliscope.databinding.ItemSelectedProductBinding;
import com.humaclab.akij_selliscope.helper.SelectedProductHelper;

import java.util.List;

public class SelectedProductRecyclerAdapter extends RecyclerView.Adapter<SelectedProductRecyclerAdapter.SelectedProductViewHolder> {
    private ActivityCart activityCart;
    private Context context;
    private List<SelectedProductHelper> selectedProductList;

    public SelectedProductRecyclerAdapter(Context context, ActivityCart activityCart, List<SelectedProductHelper> selectedProductList) {
        this.context = context;
        this.activityCart = activityCart;
        this.selectedProductList = selectedProductList;
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
        holder.getBinding().btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedProductList.remove(position);
                SelectedProductRecyclerAdapter.this.notifyItemRemoved(position);
                SelectedProductRecyclerAdapter.this.notifyItemRangeChanged(position, selectedProductList.size());
                activityCart.onRemoveSelectedProduct(selectedProduct);
                activityCart.finish();
                context.startActivity(activityCart.getIntent());            }
        });
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
}