package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.databinding.library.baseAdapters.BR;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.OrderActivity;
import com.humaclab.selliscope.databinding.ItemOrderProductBinding;
import com.humaclab.selliscope.helper.SelectedProductHelper;
import com.humaclab.selliscope.helper.ShowProductSelectionDialog;
import com.humaclab.selliscope.model.variant_product.ProductsItem;


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
    private String outletType;

    public OrderProductRecyclerAdapter(Context context, OrderActivity orderActivity, List<ProductsItem> productsItemList, List<SelectedProductHelper> selectedProductList, String outletType, OnSelectProductListener onSelectProductListener) {
        this.orderActivity = orderActivity;
        this.context = context;
        this.productsItemList = productsItemList;
        this.selectedProductList = selectedProductList;
        this.outletType = outletType;
        this.mOnSelectProductListener=onSelectProductListener;
    }

    public OrderProductRecyclerAdapter(Context context, OrderActivity orderActivity, List<ProductsItem> productsItemList, List<SelectedProductHelper> selectedProductList, String outletType) {
        this.orderActivity = orderActivity;
        this.context = context;
        this.productsItemList = productsItemList;
        this.selectedProductList = selectedProductList;
        this.outletType = outletType;
    }

    @Override
    public OrderProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder( OrderProductViewHolder holder,  int position) {
        this.holder = holder;

        ProductsItem products = productsItemList.get(position);

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
                    productsItemList.get(position), outletType,selectedProduct ->  {

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

        Glide.with(holder.imageView)
                .load(products.getImg())
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.imageView);
        holder.getBinding().setVariable(BR.product, products);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return productsItemList.size();
    }


    public class OrderProductViewHolder extends RecyclerView.ViewHolder  {
        private ItemOrderProductBinding binding;
        private CardView cv_product_background;
        ImageView imageView;

        public OrderProductViewHolder(View itemView) {
            super(itemView);
            //this.setIsRecyclable(false);
            imageView = itemView.findViewById(R.id.iv_product_image);
            cv_product_background = itemView.findViewById(R.id.cv_product_background);

            binding = DataBindingUtil.bind(itemView);
          /*  itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowProductSelectionDialog showProductSelectionDialog = new ShowProductSelectionDialog(
                            OrderProductRecyclerAdapter.OrderProductViewHolder.this,
                            context,
                            productsItemList.get(getAdapterPosition()),outletType);
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

        //on selected product show the price and quenty

    /*    @Override
        public void onSetSelectedProduct(SelectedProductHelper selectedProduct) {
            orderActivity.onSetSelectedProduct(selectedProduct);
            cv_product_background.setCardBackgroundColor(Color.GREEN);
            getBinding().llQuantity.setVisibility(View.VISIBLE);
            getBinding().tvProductQuantity.setText(selectedProduct.getProductQuantity());
            getBinding().tvProductPrice.setText(selectedProduct.getProductPrice());
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
