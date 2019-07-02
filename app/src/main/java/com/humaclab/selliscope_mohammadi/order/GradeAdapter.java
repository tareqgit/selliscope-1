/*
 * Created by Tareq Islam on 6/30/19 12:56 PM
 *
 *  Last modified 6/30/19 12:56 PM
 */

package com.humaclab.selliscope_mohammadi.order;

import android.animation.ObjectAnimator;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.cart.model.CartObject;
import com.humaclab.selliscope_mohammadi.databinding.ActivityOrderNewGradeCardBinding;

import java.util.ArrayList;
import java.util.List;

import static com.humaclab.selliscope_mohammadi.order.OrderNewActivity.s_CartObjects;

/***
 * Created by mtita on 30,June,2019.
 */
public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder>{
    private final ExpansionLayoutCollection expansionsCollection = new ExpansionLayoutCollection();


    Context mContext;

    List<CartObject> mCartObjects = new ArrayList<>();

    public GradeAdapter(Context context, List<CartObject> cartObjects) {
        mContext = context;
        mCartObjects = cartObjects;

    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GradeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_order_new_grade_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
    //    Toast.makeText(mContext, "po"+position, Toast.LENGTH_SHORT).show();
        CartObject  cartObject = mCartObjects.get(position);
        holder.getBinding().radioButtonGrade.setText(cartObject.getGrade());
        holder.getBinding().constrainBody.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(holder.getBinding().headerIndicator, View.ROTATION, 90).start();
           holder.getBinding().constrainHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.getBinding().constrainBody.getVisibility()== View.GONE){
                    ObjectAnimator.ofFloat(holder.getBinding().headerIndicator, View.ROTATION, 270).start();
                    holder.getBinding().constrainBody.setVisibility(View.VISIBLE);
                    holder.getBinding().radioButtonGrade.setChecked(true);
                    cartObject.setSelected(true);
                }else{
                    ObjectAnimator.ofFloat(holder.getBinding().headerIndicator, View.ROTATION, 90).start();
                    holder.getBinding().constrainBody.setVisibility(View.GONE);
                    holder.getBinding().radioButtonGrade.setChecked(false);
                    cartObject.setSelected(false);
                }
            }
        });



           holder.getBinding().textInputLayoutPrice.getEditText().addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {

               }

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {
                  try {


                     if (!holder.getBinding().textInputLayoutPrice.getEditText().getText().toString().isEmpty() && !holder.getBinding().textInputLayoutQuantity.getEditText().getText().toString().isEmpty()) {
                         cartObject.setRate(Double.parseDouble(s.toString()));

                         Double price = Double.parseDouble(holder.getBinding().textInputLayoutPrice.getEditText().getText().toString());
                         Double qty = Double.parseDouble(holder.getBinding().textInputLayoutQuantity.getEditText().getText().toString());
                         if (qty > 0 && price > 0) {
                             holder.getBinding().textViewTotal.setText(String.valueOf(qty * price));
                             cartObject.setTotal_price(qty*price);
                             cartObject.setRate(price);
                             cartObject.setQty(qty);
                         }

                     }
                 }catch (Exception ex){
                     Log.d("tareq_test" , "exception in price and quantity");
                 }
               }

               @Override
               public void afterTextChanged(Editable s) {

               }
           });

        holder.getBinding().textInputLayoutQuantity.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {


                    if (!holder.getBinding().textInputLayoutPrice.getEditText().getText().toString().isEmpty() && !holder.getBinding().textInputLayoutQuantity.getEditText().getText().toString().isEmpty()) {

                        cartObject.setQty(Double.parseDouble(s.toString()));

                        Double price = Double.parseDouble(holder.getBinding().textInputLayoutPrice.getEditText().getText().toString());
                        Double qty = Double.parseDouble(holder.getBinding().textInputLayoutQuantity.getEditText().getText().toString());
                        if (qty > 0 && price > 0) {
                            holder.getBinding().textViewTotal.setText(String.valueOf(qty * price));
                            cartObject.setTotal_price(qty*price);
                            cartObject.setRate(price);
                            cartObject.setQty(qty);

                        }

                    }
                }catch (Exception ex){
                    Log.d("tareq_test" , "exception in price and quantity");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
        for(CartObject cartObject1: s_CartObjects){
            if(cartObject.getDia().equals(cartObject1.getDia())&& cartObject.getGrade().equals(cartObject1.getGrade())){
                holder.getBinding().textInputLayoutQuantity.getEditText().setText(cartObject1.getQty().toString());
                holder.getBinding().textInputLayoutPrice.getEditText().setText(cartObject1.getRate().toString());
                holder.getBinding().radioButtonGrade.setChecked(true);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mCartObjects.size();
    }

    class GradeViewHolder extends RecyclerView.ViewHolder{

       private ActivityOrderNewGradeCardBinding mBinding;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public ActivityOrderNewGradeCardBinding getBinding() {
            return mBinding;
        }
    }
}
