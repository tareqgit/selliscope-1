package com.humaclab.selliscope.sales_return;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.ActivitySalesReturn2019ProductCardBinding;
import com.humaclab.selliscope.model.variant_product.ProductsItem;
import com.humaclab.selliscope.sales_return.model.SalesReturn2019SelectedProduct;

import java.util.ArrayList;
import java.util.List;

import static com.humaclab.selliscope.sales_return.SalesReturn_2019_Activity.sSalesReturn2019SelectedProducts;

/***
 * Created by mtita on 09,July,2019.
 */
public class SalesReturn_2019_Adapter extends RecyclerView.Adapter<SalesReturn_2019_Adapter.SalesReturnViewHolder> {

    Context mContext;
    private List<ProductsItem> productsItemList;
    private OnSelectProductListener mOnSelectProductListener;


    public SalesReturn_2019_Adapter(Context context, List<ProductsItem> productsItemList, OnSelectProductListener onSelectProductListener) {
        mContext = context;
        this.productsItemList = productsItemList;
        mOnSelectProductListener = onSelectProductListener;
    }

    public void setProductsItemList(List<ProductsItem> productsItemList) {
        this.productsItemList = productsItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SalesReturnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SalesReturnViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_sales_return_2019_product_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SalesReturnViewHolder holder, int position) {
        ProductsItem product = productsItemList.get(position);

        /*First we have to make sure everything is resetted beacause the view changes will be reused */
        holder.getBinding().ivRemoveProduct.setVisibility(View.GONE);
        holder.getBinding().llQuantity.setVisibility(View.GONE);
        holder.getBinding().linearLayoutRate.setVisibility(View.GONE);
        holder.getBinding().cvProductBackground.setCardBackgroundColor(Color.WHITE);

        //Then use the logic to mark the selected Item
        if (!sSalesReturn2019SelectedProducts.isEmpty()) {
            for (final SalesReturn2019SelectedProduct selectedProductHelper : sSalesReturn2019SelectedProducts) {
                //as we don't have any unique key we need to check two condition for make this thing unique
                if (selectedProductHelper.getProductId()==product.getId() &&
                        (selectedProductHelper.getProductVariantRow()== Integer.parseInt(product.getVariantRow()))) {
                    // Log.d("tareq_test", "colored" + position);
                    holder.getBinding().cvProductBackground.setCardBackgroundColor(Color.GREEN);
                    holder.getBinding().llQuantity.setVisibility(View.VISIBLE);
                    holder.getBinding().linearLayoutRate.setVisibility(View.VISIBLE);
                    holder.getBinding().tvProductPrice.setText(String.valueOf(selectedProductHelper.getProductQty()));
                    holder.getBinding().tvProductQuantity.setText(String.valueOf(selectedProductHelper.getProductQty()));
                    holder.getBinding().ivRemoveProduct.setVisibility(View.VISIBLE);


                }
            }
        }

        /*On product select */
        holder.getBinding().cvProductBackground.setOnClickListener(v -> {
            FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
            Fragment prev = ((AppCompatActivity) mContext).getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            //we want to show a dialog
            SalesReturn_2019_ProductSelectionDialog salesReturn2019ProductSelectionDialog = new SalesReturn_2019_ProductSelectionDialog(
                    mContext
                    , new SalesReturn2019SelectedProduct.Builder()
                    .withProductId(product.getId())
                    .withProductName(product.getName())
                    .withProductVariantRow(Integer.parseInt(product.getVariantRow()))

                    , new SalesReturn_2019_ProductSelectionDialog.OnDialogSelectListener() {
                @Override
                public void onFinalSelect(SalesReturn2019SelectedProduct selectedProduct) {

                    mOnSelectProductListener.onSetSelectedProduct(selectedProduct);
                    /*Then update apearance of that card*/
                    holder.getBinding().cvProductBackground.setCardBackgroundColor(Color.GREEN);
                    holder.getBinding().llQuantity.setVisibility(View.VISIBLE);
                    holder.getBinding().linearLayoutRate.setVisibility(View.VISIBLE);
                    holder.getBinding().tvProductPrice.setText(String.valueOf(selectedProduct.getProductQty()));
                     holder.getBinding().tvProductQuantity.setText(String.valueOf(selectedProduct.getProductQty()));
                    holder.getBinding().ivRemoveProduct.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinalCancel() {

                }
            });







            /*if the product has been selected before now we want to modify*/
            if (!sSalesReturn2019SelectedProducts.isEmpty()) {
                for (SalesReturn2019SelectedProduct selectedProductHelper : sSalesReturn2019SelectedProducts) {
                    if (selectedProductHelper.getProductId()==productsItemList.get(position).getId()
                            && selectedProductHelper.getProductVariantRow()== Integer.parseInt(productsItemList.get(position).getVariantRow())) {

                        //By calling this method we update dialog value like procuct selected quantity
         //               showProductSelectionDialog.setSelectedProduct(selectedProductHelper);

                    }
                }
            }


            salesReturn2019ProductSelectionDialog.show(ft, "dialog");
        });

        /*On remove button click */
        holder.getBinding().ivRemoveProduct.setOnClickListener(v -> {
            if (!sSalesReturn2019SelectedProducts.isEmpty()) {
                List<SalesReturn2019SelectedProduct> selectedProductList = new ArrayList<>(sSalesReturn2019SelectedProducts);

                for (SalesReturn2019SelectedProduct selectedProductHelper : selectedProductList) {
                    if (selectedProductHelper.getProductId() == productsItemList.get(position).getId()
                            && selectedProductHelper.getProductVariantRow() == Integer.parseInt(productsItemList.get(position).getVariantRow())) {

                        mOnSelectProductListener.onRemoveSelectedProduct(selectedProductHelper);


                        holder.getBinding().ivRemoveProduct.setVisibility(View.GONE);
                        holder.getBinding().llQuantity.setVisibility(View.GONE);
                        holder.getBinding().linearLayoutRate.setVisibility(View.GONE);
                        holder.getBinding().cvProductBackground.setCardBackgroundColor(Color.WHITE);

                    }

                }
            }
        });

        Glide.with(holder.getBinding().ivProductImage)
                .load(product.getImg())
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.getBinding().ivProductImage);
        holder.getBinding().setProduct(product);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return productsItemList.size();
    }

    public class SalesReturnViewHolder extends RecyclerView.ViewHolder {

        private ActivitySalesReturn2019ProductCardBinding mCardBinding;

        public SalesReturnViewHolder(@NonNull View itemView) {
            super(itemView);

            mCardBinding = DataBindingUtil.bind(itemView);
        }

        public ActivitySalesReturn2019ProductCardBinding getBinding() {
            return mCardBinding;
        }
    }


    public interface OnSelectProductListener {
        /**
         * Update UI when product select for order
         *
         * @param selectedProduct SelectedProductHelper selected product details
         */
        void onSetSelectedProduct(SalesReturn2019SelectedProduct selectedProduct);

        /**
         * Update UI when product is removed from order
         *
         * @param selectedProduct SelectedProductHelper removed product details
         */
         void  onRemoveSelectedProduct(SalesReturn2019SelectedProduct selectedProduct);
    }
}
