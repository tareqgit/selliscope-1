/*
 * Created by Tareq Islam on 6/27/19 5:27 PM
 *
 *  Last modified 6/27/19 5:27 PM
 */

package com.humaclab.selliscope_mohammadi.order;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.cart.model.CartObject;
import com.humaclab.selliscope_mohammadi.databinding.ActivityOrderNewDiameterCardBinding;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.humaclab.selliscope_mohammadi.order.OrderNewActivity.s_CartObjects;

/***
 * Created by mtita on 27,June,2019.
 */
public class DiameterAdapter extends RecyclerView.Adapter<DiameterAdapter.DiameterViewHolder> {
    Context mContext;

    List<String> diameters = new ArrayList<>();
    private OnSelectProductListener mOnSelectProductListener;
    public DiameterAdapter(Context context, List<String> diameters) {
        mContext = context;
        this.diameters = diameters;
    }

    public DiameterAdapter(Context context, List<String> diameters, OnSelectProductListener onSelectProductListener) {
        mContext = context;
        this.diameters = diameters;
        mOnSelectProductListener = onSelectProductListener;
    }

    @NonNull
    @Override
    public DiameterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiameterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_order_new_diameter_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DiameterViewHolder holder, final int position) {
        String dia = diameters.get(position);

        /*First we have to make sure everything is resetted beacause the view changes will be reused */
        holder.getBinding().remove.setVisibility(View.GONE);
            holder.getBinding().diaCard.setCardBackgroundColor(Color.WHITE);


        //Then use the logic to mark the selected Item
        if (!s_CartObjects.isEmpty()) {
            for (final CartObject selectedProductHelper : s_CartObjects) {
                //as we don't have any unique key we need to check two condition for make this thing unique
                if (selectedProductHelper.getDia().equals(dia) ) {
                    // Log.d("tareq_test", "colored" + position);
                    holder.getBinding().diaCard.setCardBackgroundColor(Color.GREEN);
                    holder.getBinding().remove.setVisibility(View.VISIBLE);


                }
            }
        }

        PushDownAnim.setPushDownAnimTo(holder.getBinding().diaCard).setOnSingleClickListener(v -> {




            /*if the product has been selected before now we want to modify*/
            if (!s_CartObjects.isEmpty()) {
                for (CartObject selectedProductHelper : s_CartObjects) {
                    if (selectedProductHelper.getDia().equals(dia)) {

                        //By calling this method we update dialog value like procuct selected quantity
                     //   showProductSelectionDialog.setSelectedProduct(selectedProductHelper);

                    }
                }
            }

            FragmentTransaction ft =((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
            Fragment prev = ((AppCompatActivity) mContext).getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            ShowProductSelectionDialog dialogFragment = new ShowProductSelectionDialog(mContext, dia, new ShowProductSelectionDialog.OnDialogSelectListener() {
                @Override
                public void onFinalSelect(CartObject selectedProductHelper) {
                    /*Then update apearance of that card*/
                    holder.getBinding().diaCard.setCardBackgroundColor(Color.GREEN);
                    holder.getBinding().remove.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinalCancel() {

                }
            });
            dialogFragment.show(ft, "dialog");

        });
        holder.getBinding().textViewDia.setText(dia);

        /*On remove button click */
        holder.getBinding().remove.setOnClickListener(v -> {
            if (!s_CartObjects.isEmpty()) {

                for (Iterator<CartObject> iterator = s_CartObjects.iterator(); iterator.hasNext(); ) {
                    CartObject selected = iterator.next();
                    if (selected.getDia().equals(dia)) {
                   //     Log.d("tareq_test", "product matched" + selected.getProductName());
                        iterator.remove();

                        holder.getBinding().remove.setVisibility(View.GONE);

                        holder.getBinding().diaCard.setCardBackgroundColor(Color.WHITE);
                    }
                }

               /* for ( CartObject selectedProductHelper : s_CartObjects) {
                    if (selectedProductHelper.getDia().equals(dia)) {

                        mOnSelectProductListener.onRemoveSelectedProduct(selectedProductHelper);

                       // break;

                    }

                }*/
            }
        });

       // holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return diameters.size();
    }

    public  class DiameterViewHolder extends RecyclerView.ViewHolder{

        private ActivityOrderNewDiameterCardBinding mBinding;

        public DiameterViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

        }

        public ActivityOrderNewDiameterCardBinding getBinding() {
            return mBinding;
        }
    }

    public interface OnSelectProductListener {
        /**
         * Update UI when product select for order
         *
         * @param selectedProduct SelectedProductHelper selected product details
         */
        void onSetSelectedProduct(CartObject selectedProduct);

        /**
         * Update UI when product is removed from order
         *
         * @param selectedProduct SelectedProductHelper removed product details
         */
         void  onRemoveSelectedProduct(CartObject selectedProduct);
    }
}
