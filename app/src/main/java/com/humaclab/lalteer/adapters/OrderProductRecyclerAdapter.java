package com.humaclab.lalteer.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;
import com.humaclab.lalteer.BR;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.activity.OrderActivity;
import com.humaclab.lalteer.databinding.ItemOrderProductBinding;
import com.humaclab.lalteer.helper.SelectedProductHelper;
import com.humaclab.lalteer.helper.ShowProductSelectionDialog;

import com.humaclab.lalteer.model.variant_product.ProductsItem;
import com.humaclab.lalteer.utils.Constants;

import java.util.List;

/**
 * Created by leon on 14/3/18.
 */

public class OrderProductRecyclerAdapter extends RecyclerView.Adapter<OrderProductRecyclerAdapter.OrderProductViewHolder> {
    private OrderActivity orderActivity;
    private Context context;
    private List<ProductsItem> productsItemList;
    private OrderProductViewHolder holder;
    private List<SelectedProductHelper> selectedProductList;
    private OnSelectProductListener mOnSelectProductListener;

    private OrderProductRecyclerAdapter(Context context, OrderActivity orderActivity, List<ProductsItem> productsItemList, List<SelectedProductHelper> selectedProductList) {
        this.orderActivity = orderActivity;
        this.context = context;
        this.productsItemList = productsItemList;
        this.selectedProductList = selectedProductList;
    }


    public OrderProductRecyclerAdapter(Context context, OrderActivity orderActivity, List<ProductsItem> productsItemList, List<SelectedProductHelper> selectedProductList, OnSelectProductListener onSelectProductListener) {
        this.orderActivity = orderActivity;
        this.context = context;
        this.productsItemList = productsItemList;
        this.selectedProductList = selectedProductList;
        this.mOnSelectProductListener = onSelectProductListener;
    }

    @Override
    public OrderProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderProductViewHolder holder, int position) {
        this.holder = holder;
        ProductsItem products = productsItemList.get(position);
       // Log.d("tareq_test", "prduct: " + new Gson().toJson(products));
        /*First we have to make sure everything is resetted beacause the view changes will be reused */
        holder.getBinding().ivRemoveProduct.setVisibility(View.GONE);
        holder.getBinding().llQuantity.setVisibility(View.GONE);
        holder.cv_product_background.setCardBackgroundColor(Color.WHITE);

        //Then use the logic to mark the selected Item
        if (!selectedProductList.isEmpty()) {
            for (final SelectedProductHelper selectedProductHelper : selectedProductList) {
                //as we don't have any unique key we need to check two condition for make this thing unique
                if (selectedProductHelper.getProductID().equals(String.valueOf(products.getId())) &&
                        (selectedProductHelper.getProductRow().equals(products.getVariantRow()))) {
                    // Log.d("tareq_test", "colored" + position);
                    holder.cv_product_background.setCardBackgroundColor(Color.GREEN);
                    holder.getBinding().llQuantity.setVisibility(View.VISIBLE);
                    holder.getBinding().tvProductQuantity.setText(selectedProductHelper.getProductQuantity());
                    holder.getBinding().ivRemoveProduct.setVisibility(View.VISIBLE);


                }

            }

        }

        /*On product select */
        holder.getBinding().cvProductBackground.setOnClickListener(v -> {

            //we want to show a dialog
            ShowProductSelectionDialog showProductSelectionDialog = new ShowProductSelectionDialog(context,
                    productsItemList.get(position), selectedProduct -> {

                mOnSelectProductListener.onSetSelectedProduct(selectedProduct);

                //    Log.d("tareq_test", "dialog fianl colored" + position);

                /*Then update apearance of that card*/
                holder.cv_product_background.setCardBackgroundColor(Color.GREEN);
                holder.getBinding().llQuantity.setVisibility(View.VISIBLE);
                holder.getBinding().tvProductQuantity.setText(selectedProduct.getProductQuantity());
                holder.getBinding().ivRemoveProduct.setVisibility(View.VISIBLE);
            });

            /*if the product has been selected before now we want to modify*/
            if (!selectedProductList.isEmpty()) {
                for (SelectedProductHelper selectedProductHelper : selectedProductList) {
                    if (selectedProductHelper.getProductID().equals(String.valueOf(productsItemList.get(position).getId()))
                            && selectedProductHelper.getProductRow().equals(productsItemList.get(position).getVariantRow())) {

                        //By calling this method we update dialog value like procuct selected quantity
                        showProductSelectionDialog.setSelectedProduct(selectedProductHelper);

                    }
                }
            }

            showProductSelectionDialog.showDialog();
        });
        /*On remove button click */
        holder.getBinding().ivRemoveProduct.setOnClickListener(v -> {
            if (!selectedProductList.isEmpty()) {
                for (final SelectedProductHelper selectedProductHelper : selectedProductList) {
                    if (selectedProductHelper.getProductID().equals(String.valueOf(products.getId()))
                            && selectedProductHelper.getProductRow().equals(products.getVariantRow())) {

                        mOnSelectProductListener.onRemoveSelectedProduct(selectedProductHelper);

                        holder.getBinding().ivRemoveProduct.setVisibility(View.GONE);
                        holder.getBinding().llQuantity.setVisibility(View.GONE);
                        holder.cv_product_background.setCardBackgroundColor(Color.WHITE);

                    }

                }
            }
        });

        if (products.getImg() != null && !products.getImg().equals("")) {
            Glide.with(context)
                    .load(Constants.BASE_URL.substring(0,Constants.BASE_URL.length()-4) + products.getImg())
                    .centerCrop()
                    .thumbnail(0.1f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.getBinding().ivProductImage);
        }

        holder.getBinding().setVariable(BR.product, products);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if(productsItemList==null) return 0;
        else return productsItemList.size();

    }


    public class OrderProductViewHolder extends RecyclerView.ViewHolder {
        private ItemOrderProductBinding binding;
        private CardView cv_product_background;

        public OrderProductViewHolder(View itemView) {
            super(itemView);
            //  this.setIsRecyclable(false);

            cv_product_background = itemView.findViewById(R.id.cv_product_background);

            binding = DataBindingUtil.bind(itemView);
          /*  itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowProductSelectionDialog showProductSelectionDialog = new ShowProductSelectionDialog(
                            OrderProductRecyclerAdapter.OrderProductViewHolder.this,
                            context,
                            productsItemList.get(getAdapterPosition()));
                    if (!selectedProductList.isEmpty()) {
                        for (SelectedProductHelper selectedProductHelper : selectedProductList) {
                            if (selectedProductHelper.getProductID().equals(String.valueOf(productsItemList.get(getAdapterPosition()).getId())))
                                if (selectedProductHelper.getProductRow().equals(productsItemList.get(getAdapterPosition()).getVariantRow())) {
                                    showProductSelectionDialog.setSelectedProduct(selectedProductHelper);
                                    break;
                                }
                        }
                    }
                    showProductSelectionDialog.showDialog();
                }
            });*/
        }

        public ItemOrderProductBinding getBinding() {
            return binding;
        }

     /*   @Override
        public void onSetSelectedProduct(SelectedProductHelper selectedProduct) {
            orderActivity.onSetSelectedProduct(selectedProduct);
            cv_product_background.setCardBackgroundColor(Color.GREEN);
            getBinding().llQuantity.setVisibility(View.VISIBLE);
            getBinding().tvProductQuantity.setText(selectedProduct.getProductQuantity());
            getBinding().ivRemoveProduct.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRemoveSelectedProduct(SelectedProductHelper selectedProduct) {

        }*/
    }


    public interface OnSelectProductListener {
        /**
         * Update UI when product select for order
         *
         * @param selectedProduct SelectedProductHelper selected product details
         */
        void onSetSelectedProduct(SelectedProductHelper selectedProduct);

        /**
         * Update UI when product is removed from order
         *
         * @param selectedProduct SelectedProductHelper removed product details
         */
        void onRemoveSelectedProduct(SelectedProductHelper selectedProduct);
    }
}
