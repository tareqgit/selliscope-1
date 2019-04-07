package com.humaclab.lalteer.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.lalteer.BR;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.activity.ActivityCart;
import com.humaclab.lalteer.databinding.ItemSelectedProductBinding;
import com.humaclab.lalteer.helper.SelectedProductHelper;

import java.util.List;
import com.humaclab.lalteer.helper.SelectedProductHelper;
import static com.humaclab.lalteer.activity.OrderActivity.selectedProductList;


public class SelectedProductRecyclerAdapter extends RecyclerView.Adapter<SelectedProductRecyclerAdapter.SelectedProductViewHolder> {
    private ActivityCart activityCart;
    private Context context;
  //  private List<SelectedProductHelper> selectedProductList;

    public SelectedProductRecyclerAdapter(Context context, ActivityCart activityCart, List<SelectedProductHelper> selectedProductList) {
        this.context = context;
        this.activityCart = activityCart;
      //  this.selectedProductList = selectedProductList;
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
                activityCart.onRemoveSelectedProduct(selectedProduct);
            } });
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
