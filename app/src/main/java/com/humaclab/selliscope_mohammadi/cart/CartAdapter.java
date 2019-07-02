/*
 * Created by Tareq Islam on 7/1/19 1:56 PM
 *
 *  Last modified 7/1/19 1:56 PM
 */

package com.humaclab.selliscope_mohammadi.cart;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.cart.model.CartObject;
import com.humaclab.selliscope_mohammadi.databinding.ActivityCartCardBinding;

import java.util.Iterator;
import java.util.List;

import static com.humaclab.selliscope_mohammadi.order.OrderNewActivity.s_CartObjects;

/***
 * Created by mtita on 01,July,2019.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    Context mContext;
    List<CartObject> mCartObjects;
    private OnItemRemove mOnItemRemove;



    public CartAdapter(Context context, List<CartObject> cartObjects, OnItemRemove onItemRemove) {
        mContext = context;
        mCartObjects = cartObjects;
        mOnItemRemove = onItemRemove;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_cart_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartObject cartObject = mCartObjects.get(position);
        holder.mBinding.setCart(cartObject);
        holder.mBinding.imageViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Iterator<CartObject> iterator = s_CartObjects.iterator(); iterator.hasNext(); ) {
                    CartObject selected = iterator.next();
                    if (selected.getDia().equals(cartObject.getDia()) && selected.getGrade().equals(cartObject.getGrade())) {
                        //     Log.d("tareq_test", "product matched" + selected.getProductName());
                        iterator.remove();
                        notifyDataSetChanged();

                    }
                }
                mOnItemRemove.onRemove();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCartObjects.size();
    }


    public class CartViewHolder extends RecyclerView.ViewHolder{

        private ActivityCartCardBinding mBinding;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public ActivityCartCardBinding getBinding() {
            return mBinding;
        }
    }

    public interface OnItemRemove{
        void onRemove();
    }
}
