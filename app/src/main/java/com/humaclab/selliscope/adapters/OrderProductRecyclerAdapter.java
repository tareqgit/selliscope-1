package com.humaclab.selliscope.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.activity.OrderActivity;
import com.humaclab.selliscope.databinding.ItemOrderProductBinding;
import com.humaclab.selliscope.helper.SelectedProductHelper;
import com.humaclab.selliscope.helper.ShowProductSelectionDialog;
import com.humaclab.selliscope.model.variant_product.ProductsItem;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by leon on 14/3/18.
 */

public class OrderProductRecyclerAdapter extends RecyclerView.Adapter<OrderProductRecyclerAdapter.OrderProductViewHolder> {
    private Context context;
    private List<ProductsItem> productsItemList;
    private List<SelectedProductHelper> selectedProductList;
    private OnSelectProductListener mOnSelectProductListener;
    private String outletType;

    public OrderProductRecyclerAdapter(Context context, List<ProductsItem> productsItemList, List<SelectedProductHelper> selectedProductList, String outletType, OnSelectProductListener onSelectProductListener) {
        this.context = context;
        this.productsItemList = productsItemList;
        this.selectedProductList = selectedProductList;
        this.outletType = outletType;
        this.mOnSelectProductListener=onSelectProductListener;
    }




    @Override
    public OrderProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder( OrderProductViewHolder holder,  int position) {

        ProductsItem products = productsItemList.get(position);

        /*First we have to make sure everything is resetted because the view changes will be reused */
        holder.getBinding().ivRemoveProduct.setVisibility(View.GONE);
        holder.getBinding().llQuantity.setVisibility(View.GONE);
        holder.cv_product_background.setCardBackgroundColor(Color.WHITE);

        //Then use the logic to mark the selected Item
        if (!selectedProductList.isEmpty()) {
            for (final SelectedProductHelper selectedProductHelper : selectedProductList) {
                //as we don't have any unique key we need to check two condition for make this thing unique
                if (selectedProductHelper.getProductID().equals(String.valueOf(products.getId())) &&
                        (selectedProductHelper.getProductRow().equals(products.getVariantRow())) && !selectedProductHelper.isFree()) {
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
                    productsItemList.get(position), outletType,selectedProducts ->  {

                mOnSelectProductListener.onSetSelectedProduct(selectedProducts);

                //    Log.d("tareq_test", "dialog fianl colored" + position);

                /*Then update apearance of that card*/
                holder.cv_product_background.setCardBackgroundColor(Color.GREEN);
                holder.getBinding().llQuantity.setVisibility(View.VISIBLE);
                holder.getBinding().tvProductQuantity.setText(selectedProducts.get(0).getProductQuantity());//as 0 no product is order product
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
                List<SelectedProductHelper> selectedProductHelperList = new ArrayList<>(selectedProductList);
                for (final SelectedProductHelper selectedProductHelper : selectedProductHelperList) {
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
        holder.getBinding().tvProductPrice.setText(String.format(Locale.ENGLISH,"%,.2f ", Double.parseDouble((products.getPrice()==null?"0":products.getPrice()).replace(",",""))));
        holder.getBinding().setProduct(products);
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

        //on selected product show the price and quantity

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
         * @param selectedProducts SelectedProductHelper selected product details
         */
        void onSetSelectedProduct(List<SelectedProductHelper> selectedProducts);

        /**
         * Update UI when product is removed from order
         *
         * @param selectedProduct SelectedProductHelper removed product details
         */
        void onRemoveSelectedProduct(SelectedProductHelper selectedProduct);
    }
}
