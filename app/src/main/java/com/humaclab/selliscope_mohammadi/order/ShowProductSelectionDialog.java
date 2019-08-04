/*
 * Created by Tareq Islam on 6/27/19 6:03 PM
 *
 *  Last modified 6/27/19 6:03 PM
 */

package com.humaclab.selliscope_mohammadi.order;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.cart.model.CartObject;
import com.humaclab.selliscope_mohammadi.databinding.ActivityOrderNewProductDialogBinding;
import com.humaclab.selliscope_mohammadi.utils.DatabaseHandler;
import com.mti.pushdown_ext_onclick_single.PushDownAnim;

import java.util.ArrayList;
import java.util.List;

import static com.humaclab.selliscope_mohammadi.order.OrderNewActivity.s_CartObjects;

/***
 * Created by mtita on 27,June,2019.
 */
public class ShowProductSelectionDialog extends DialogFragment {
    private Context context;
    private OnDialogSelectListener mOnDialogSelectListener;

    ActivityOrderNewProductDialogBinding mBinding;
    private GradeAdapter mGradeAdapter;
    List<CartObject> mCartObjectArrayList=new ArrayList<>();
    String dia;

    public ShowProductSelectionDialog(Context context,String dia, OnDialogSelectListener onDialogSelectListener) {
        this.context = context;
        mOnDialogSelectListener = onDialogSelectListener;
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        this.dia=dia;


        for (String str : (databaseHandler.getVariants(2, databaseHandler.getProductIds().size()==0? 0 :databaseHandler.getProductIds().get(0)))){
            mCartObjectArrayList.add(new CartObject.Builder().withDia(dia).withGrade(str).build());
        }




    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
       /* if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }*/
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.activity_order_new_product_dialog, container,false);
        mBinding.recyclerviewGrade.setLayoutManager(new LinearLayoutManager(context));
        mGradeAdapter=new GradeAdapter(context,mCartObjectArrayList);
        mBinding.recyclerviewGrade.setAdapter(mGradeAdapter);
        mBinding.textViewDia.setText(dia);
        PushDownAnim.setPushDownAnimTo(mBinding.textViewOK).setOnSingleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration
                Log.d("tareq_test" , "from dialog"+ new Gson().toJson(mCartObjectArrayList));
                for (CartObject cartObject:mCartObjectArrayList    ) {
                    if(cartObject.isSelected() ){
                        if(cartObject.getTotal_price()!=null && cartObject.getTotal_price()>0) {
                            s_CartObjects.add(cartObject);
                            mOnDialogSelectListener.onFinalSelect(cartObject);
                        }
                    }
                }

              dismiss();
            }
        });
        PushDownAnim.setPushDownAnimTo(mBinding.textViewCancel).setOnSingleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); //for onclick vibration
                Log.d("tareq_test" , ""+ new Gson().toJson(mCartObjectArrayList));
                mOnDialogSelectListener.onFinalCancel();
              dismiss();
            }
        });
        return mBinding.getRoot();
    }



    public interface OnDialogSelectListener{
        void onFinalSelect(CartObject selectedProductHelper);
        void onFinalCancel();
    }
}
