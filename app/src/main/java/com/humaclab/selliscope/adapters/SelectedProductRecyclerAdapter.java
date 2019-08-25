package com.humaclab.selliscope.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.ActivityCart;
import com.humaclab.selliscope.databinding.ItemSelectedProductBinding;
import com.humaclab.selliscope.helper.SelectedProductHelper;
import com.humaclab.selliscope.sales_return.model.post.SalesReturn2019SelectedProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.humaclab.selliscope.activity.OrderActivity.selectedProductList;
import static com.humaclab.selliscope.sales_return.SalesReturn_2019_Activity.sSalesReturn2019SelectedProducts;

public class SelectedProductRecyclerAdapter extends RecyclerView.Adapter<SelectedProductRecyclerAdapter.SelectedProductViewHolder> {
    private ActivityCart activityCart;
    private Context context;

   private List<Object> mSelectedProductList= new ArrayList<>();



    OnRemoveFromCartListener mOnRemoveFromCartListener;

    public void updateCart(){
        mSelectedProductList.clear();
        mSelectedProductList.addAll(selectedProductList);
        mSelectedProductList.addAll(sSalesReturn2019SelectedProducts);
        notifyDataSetChanged();
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
        Log.d("tareq_test" , "size"+ mSelectedProductList.size());

       //for default product

        //region for Product Reset
        holder.getBinding().btnRemove.setVisibility(View.VISIBLE);

        holder.getBinding().textViewPromotionLabel.setVisibility(View.VISIBLE);
        holder.getBinding().textViewPromotionLabel.setText("PromotionalDiscount");
        holder.getBinding().textViewPromotionEqual.setVisibility(View.VISIBLE);
        holder.getBinding().tvPromotiondiscount.setVisibility(View.VISIBLE);

        holder.getBinding().textViewTotalLabel.setVisibility(View.VISIBLE);
        holder.getBinding().textViewTotalEqual.setVisibility(View.VISIBLE);
        holder.getBinding().tvFinaltotal.setVisibility(View.VISIBLE);

        holder.getBinding().textViewFree.setVisibility(View.GONE);
        holder.getBinding().textViewReturn.setVisibility(View.GONE);
        //endregion



        if((isItOrder(mSelectedProductList.get(position)))){
            SelectedProductHelper selectedProduct = (SelectedProductHelper) mSelectedProductList.get(position);
            if( selectedProduct.isFree()) {
                holder.getBinding().btnRemove.setVisibility(View.GONE);

                holder.getBinding().textViewPromotionLabel.setVisibility(View.GONE);
                holder.getBinding().textViewPromotionEqual.setVisibility(View.GONE);
                holder.getBinding().tvPromotiondiscount.setVisibility(View.GONE);

                holder.getBinding().textViewTotalLabel.setVisibility(View.GONE);
                holder.getBinding().textViewTotalEqual.setVisibility(View.GONE);
                holder.getBinding().tvFinaltotal.setVisibility(View.GONE);

                holder.getBinding().textViewFree.setVisibility(View.VISIBLE);

            }

            if(selectedProduct.getFreeProductdetails()!=null && !selectedProduct.getFreeProductdetails().equals("") ){
                holder.getBinding().textViewPromotionLabel.setText(selectedProduct.getFreeProductdetails().toString());
            //    holder.getBinding().textViewPromotionEqual.setVisibility(View.GONE);
            //    holder.getBinding().tvPromotiondiscount.setVisibility(View.GONE);
            }
         //   holder.getBinding().setVariable(BR.selectedProduct, selectedProduct);
            holder.getBinding().tvProductName.setText(selectedProduct.getProductName());
            holder.getBinding().tvPrice.setText(selectedProduct.getProductPrice());
            holder.getBinding().etQty.setText(selectedProduct.getProductQuantity());
            holder.getBinding().tvAmount.setText(selectedProduct.getTotalPrice());
            holder.getBinding().tvPromotiondiscount.setText(selectedProduct.getTpDiscount());
            holder.getBinding().tvFinaltotal.setText(String.format(Locale.ENGLISH,"%,.2f",Double.parseDouble(selectedProduct.getTotalPrice().replace(",","")) - Double.parseDouble(selectedProduct.getTpDiscount().replace(",",""))));

            holder.getBinding().btnRemove.setOnClickListener(v -> {
                if (selectedProductList.size() != 0) {

                    try {
                        if( selectedProductList.get(position+1).isFree()) selectedProductList.remove(position+1);
                    } catch (Exception e) {
                       Log.e("tareq_test" , "can't remove free item from cart: "+ e.getMessage());
                    }
                    selectedProductList.remove(position);

                    SelectedProductRecyclerAdapter.this.notifyItemRemoved(position);
                    SelectedProductRecyclerAdapter.this.notifyItemRangeChanged(position, selectedProductList.size());
                    mOnRemoveFromCartListener.onRemoveSelectedProduct(selectedProduct);
                    updateCart();
                }
            });
        }


        if( isItReturn(mSelectedProductList.get(position))){
            SalesReturn2019SelectedProduct selectedProduct =(SalesReturn2019SelectedProduct) mSelectedProductList.get(position);
            holder.getBinding().tvProductName.setText(selectedProduct.getProductName());
            holder.getBinding().tvPrice.setText(String.valueOf(selectedProduct.getProductRate()));
            holder.getBinding().etQty.setText(String.valueOf(selectedProduct.getProductQty()));
            holder.getBinding().tvAmount.setText(String.valueOf(selectedProduct.getProductTotal()));
            holder.getBinding().tvFinaltotal.setText(String.valueOf(selectedProduct.getProductTotal()));

            //for hiding promotional discount
            holder.getBinding().textViewPromotionLabel.setVisibility(View.GONE);
            holder.getBinding().textViewPromotionEqual.setVisibility(View.GONE);
            holder.getBinding().tvPromotiondiscount.setVisibility(View.GONE);

            //for default products

            //for showing return badge
            holder.getBinding().textViewReturn.setVisibility(View.VISIBLE);

            holder.getBinding().btnRemove.setOnClickListener(v -> {
                if (sSalesReturn2019SelectedProducts.size() != 0) {
                    sSalesReturn2019SelectedProducts.remove(position-selectedProductList.size());
                    SelectedProductRecyclerAdapter.this.notifyItemRemoved(position-selectedProductList.size());
                    SelectedProductRecyclerAdapter.this.notifyItemRangeChanged(position-selectedProductList.size(), sSalesReturn2019SelectedProducts.size());
                    mOnRemoveFromCartListener.onRemoveSelectedProduct(selectedProduct);
                    updateCart();
                }
            });
        }
    }

    private boolean isItOrder(Object object){
        try {
          SelectedProductHelper selectedProductHelper=  (SelectedProductHelper) object;

        }catch (Exception ex){
            return false;
        }
        return true;
    }

    private boolean isItReturn(Object object){
        try {
            SalesReturn2019SelectedProduct selectedProductHelper=  (SalesReturn2019SelectedProduct) object;

        }catch (Exception ex){
            return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return mSelectedProductList.size();
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
        void onRemoveSelectedProduct(Object selectedProduct);
    }
}
