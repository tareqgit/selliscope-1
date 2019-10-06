/*
 * Created by Tareq Islam on 6/9/19 4:07 PM
 *
 *  Last modified 6/9/19 4:07 PM
 */

package com.humaclab.lalteer.utils;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.databinding.CusDialogBinding;
import com.humaclab.lalteer.model.advance_payment.AdvancePaymentPostResponse;
import com.humaclab.lalteer.model.advance_payment.AdvancePaymentsItem;
import com.humaclab.lalteer.model.advance_payment.AdvancePaymentsSendItem;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * Created by mtita on 09,June,2019.
 */
public class MyDialog extends DialogFragment {


    private SelliscopeApiEndpointInterface mApiService;
    private CusDialogBinding mBinding;
    private  int outletId;
    OnAdvanceDialogDismiss mOnDismissListener;

    public void setOnDismissListener(OnAdvanceDialogDismiss onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    private MyDialog(int outletId, SelliscopeApiEndpointInterface apiService) {
        this.outletId=outletId;
        mApiService = apiService;
    }

    public static MyDialog newInstance(int outletId,SelliscopeApiEndpointInterface apiService)
    {

        MyDialog myDialog=new MyDialog(outletId, apiService);
        Bundle args=new Bundle();

        myDialog.setArguments(args);

        return myDialog;


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.cus_dialog, container, false);
        getDialog().getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;

        //for having custom rounded background we need to change the default background into transparent
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        initFields();

        return mBinding.getRoot();

    }

    private void initFields() {


            mBinding.loadingProgressBar.setVisibility(View.GONE);



       PushDownAnim.setPushDownAnimTo(mBinding.doneBtnTextview).setOnSingleClickListener(v->{
        //   v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration

           if(checkValidation()) {
               AdvancePaymentsSendItem advancePaymentsItem = new AdvancePaymentsSendItem(Double.parseDouble(mBinding.textInputLayoutAmount.getEditText().getText().toString().trim()),
                       mBinding.textInoutComment.getEditText().getText().toString(),
                       mBinding.textInputLayoutProducts.getEditText().getText().toString(),
                       mBinding.textInputLayoutBank.getEditText().getText().toString(),
                       CurrentTimeUtilityClass.getCurrentTimeStamp().toString());

               mBinding.loadingProgressBar.setVisibility(View.VISIBLE);
               postAdvancePayments(advancePaymentsItem);
           }

       });
    }


   private boolean checkValidation(){
       if(  mBinding.textInputLayoutAmount.getEditText().getText().toString().isEmpty() || mBinding.textInputLayoutAmount.getEditText().getText().toString().equals("0")) {
           mBinding.textInputLayoutAmount.getEditText().setError( "Amount  is required!" );
           return false;
       }

             if(  mBinding.textInputLayoutBank.getEditText().getText().toString().isEmpty()) {
            mBinding.textInputLayoutBank.getEditText().setError( "Bank name is required!" );
            return false;
        }

       if(  mBinding.textInputLayoutProducts.getEditText().getText().toString().isEmpty()) {
           mBinding.textInputLayoutProducts.getEditText().setError( "Products name is required!" );
           return false;
       }

        return true;

    }


    private void postAdvancePayments(AdvancePaymentsSendItem advancePaymentsItem){

        Log.d("tareq_test" , "send "+new Gson().toJson(advancePaymentsItem));
        mApiService.postAdvancePayment(outletId,advancePaymentsItem).enqueue(new Callback<AdvancePaymentPostResponse>() {
            @Override
            public void onResponse(Call<AdvancePaymentPostResponse> call, Response<AdvancePaymentPostResponse> response) {
                mBinding.loadingProgressBar.setVisibility(View.GONE);
               if(response.isSuccessful()){
                   if (response.body() != null) {
                       Toast.makeText(getContext(), ""+response.body().getResult(), Toast.LENGTH_SHORT).show();
                   }

                   mOnDismissListener.onDismiss(); //setting callback
                   dismiss(); //for closing dialog
               }else{
                   Toast.makeText(getContext(), "Internal Server Error"+ response.code(), Toast.LENGTH_SHORT).show();
               }

            }

            @Override
            public void onFailure(Call<AdvancePaymentPostResponse> call, Throwable t) {
                mBinding.loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public interface OnAdvanceDialogDismiss{
        void onDismiss();
    }
}
