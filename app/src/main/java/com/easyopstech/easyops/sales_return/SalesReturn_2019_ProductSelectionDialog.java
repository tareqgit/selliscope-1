package com.easyopstech.easyops.sales_return;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.ActivitySalesReturn2019ProductSelectionDiologBinding;
import com.easyopstech.easyops.sales_return.model.post.SalesReturn2019SelectedProduct;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;

import java.util.Objects;

import static com.easyopstech.easyops.sales_return.SalesReturn_2019_Activity.sSalesReturn2019SelectedProducts;

/***
 * Created by mtita on 09,July,2019.
 */
public class SalesReturn_2019_ProductSelectionDialog extends DialogFragment {
    ActivitySalesReturn2019ProductSelectionDiologBinding mDiologBinding;
    private Context mContext;
    SalesReturn2019SelectedProduct.Builder mSelectedProduct;
    private OnDialogSelectListener mOnDialogSelectListener;

    public SalesReturn_2019_ProductSelectionDialog(Context context, SalesReturn2019SelectedProduct.Builder selectedProduct, OnDialogSelectListener onDialogSelectListener) {
        mContext = context;
        mSelectedProduct = selectedProduct;
        mOnDialogSelectListener = onDialogSelectListener;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
       /* if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }*/
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDiologBinding = DataBindingUtil.inflate(inflater, R.layout.activity_sales_return_2019_product_selection_diolog, container, false);
        initViews();
        return mDiologBinding.getRoot();
    }

    private void initViews() {

        try {
            for (SalesReturn2019SelectedProduct selectedProduct : sSalesReturn2019SelectedProducts){
             
                if(selectedProduct.getProductId() == mSelectedProduct.build().getProductId() && selectedProduct.getProductVariantRow()== mSelectedProduct.build().getProductVariantRow()){
                    mDiologBinding.editTextSku.setText(selectedProduct.getProductSKU());
                    mDiologBinding.editTextRate.setText(String.valueOf(selectedProduct.getProductRate()));
                    mDiologBinding.editTextQty.setText(String.valueOf(selectedProduct.getProductQty()));
                    mDiologBinding.textViewTotal.setText(String.valueOf(selectedProduct.getProductTotal()));
                    mDiologBinding.spinnerReason.setSelection(selectedProduct.getReason());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("tareq_test" , "Error in product return selection dialog retap");
        }

        mDiologBinding.textViewheader.setText(mSelectedProduct.build().getProductName());

        mDiologBinding.editTextRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!mDiologBinding.editTextRate.getText().toString().isEmpty() && !mDiologBinding.editTextQty.getText().toString().isEmpty()) {

                        mSelectedProduct.withProductRate(Double.valueOf(s.toString()));

                        Double price = Double.parseDouble(mDiologBinding.editTextRate.getText().toString());
                        Double qty = Double.parseDouble(mDiologBinding.editTextQty.getText().toString());

                        if (qty > 0 && price > 0) {
                            mDiologBinding.textViewTotal.setText(String.valueOf(qty * price));
                            mSelectedProduct.withProductTotal(qty * price);
                            mSelectedProduct.withProductRate(price);
                            mSelectedProduct.withProductQty(qty);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDiologBinding.editTextQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!mDiologBinding.editTextRate.getText().toString().isEmpty() && !mDiologBinding.editTextQty.getText().toString().isEmpty()) {

                        mSelectedProduct.withProductQty(Double.valueOf(s.toString()));

                        Double price = Double.parseDouble(mDiologBinding.editTextRate.getText().toString());
                        Double qty = Double.parseDouble(mDiologBinding.editTextQty.getText().toString());

                        if (qty > 0 && price > 0) {
                            mDiologBinding.textViewTotal.setText(String.valueOf(qty * price));
                            mSelectedProduct.withProductTotal(qty * price);
                            mSelectedProduct.withProductRate(price);
                            mSelectedProduct.withProductQty(qty);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        PushDownAnim.setPushDownAnimTo(mDiologBinding.textViewCancel).setOnSingleClickListener(v -> dismiss());

        PushDownAnim.setPushDownAnimTo(mDiologBinding.textViewOkay).setOnSingleClickListener(v -> {

            if( mDiologBinding.editTextRate.getText().toString().isEmpty() || mDiologBinding.editTextRate.getText().toString().equals("0")){
                mDiologBinding.editTextRate.setError("value can't be empty or 0");
                return;
            }
            if( mDiologBinding.editTextQty.getText().toString().isEmpty() || mDiologBinding.editTextQty.getText().toString().equals("0")){
                mDiologBinding.editTextQty.setError("value can't be empty or 0");
                return;
            }
            SalesReturn2019SelectedProduct selectedProduct=mSelectedProduct
                    .withProductSKU(mDiologBinding.editTextSku.getText().toString())
                    .withReason(mDiologBinding.spinnerReason.getSelectedItemPosition()+1)
                    .build();
            sSalesReturn2019SelectedProducts.add(selectedProduct);
            mOnDialogSelectListener.onFinalSelect(selectedProduct);
            Log.d("tareq_test" , ""+new Gson().toJson(mSelectedProduct));
            dismiss();
        });
    }


    public interface OnDialogSelectListener {
        void onFinalSelect(SalesReturn2019SelectedProduct selectedProduct);

        void onFinalCancel();
    }
}
