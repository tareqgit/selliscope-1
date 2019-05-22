/*
 * Created by Tareq Islam on 3/19/19 4:48 PM
 *
 *  Last modified 3/19/19 4:48 PM
 */

package com.humaclab.selliscope_myone.productDialog;



import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.activity.OrderActivity;
import com.humaclab.selliscope_myone.model.variantProduct.ProductsItem;
import com.humaclab.selliscope_myone.utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by mtita on 19,March,2019.
 */
public class ProductDialogFragment extends DialogFragment implements ProductDialogAdapter.OnItemClickListener {
  public   List<ProductsItem> products = new ArrayList<>();

    RecyclerView mRecyclerView;
    ProductDialogAdapter mProductDialogAdapter;
    private DatabaseHandler databaseHandler;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHandler = new DatabaseHandler(getActivity());
        products=databaseHandler.getProduct(0,0);

      //  setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView=inflater.inflate(R.layout.product_dialog_fragment,container);

        /*Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(view1 -> cancelUpload());
        toolbar.setTitle("My Dialog");*/

        //RECYCER
        mRecyclerView= (RecyclerView) rootView.findViewById(R.id.mRecyerID);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));

        //ADAPTER
        mProductDialogAdapter=new ProductDialogAdapter(this.getActivity(),products,this);
        mRecyclerView.setAdapter(mProductDialogAdapter);

        this.getDialog().setTitle("Products");

        rootView.findViewById(R.id.caneclImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        ( (EditText) rootView.findViewById(R.id.searchProduct_EditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    products = databaseHandler.getSearchedProduct(s.toString());
                     mProductDialogAdapter.updateData(products);
                     mProductDialogAdapter.notifyDataSetChanged();
                } else {
                        products=databaseHandler.getProduct(0,0);
                    mProductDialogAdapter.updateData(products);
                    mProductDialogAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    @Override
    public void onClick(ProductsItem productsItem) {
      // Toast.makeText(getActivity().getApplicationContext(), ""+ productsItem.getId(), Toast.LENGTH_SHORT).show();
        OrderActivity orderActivity=(OrderActivity) getActivity();
     //   orderActivity.addProduct(productID.get(sp_product_name.getSelectedItemPosition()), isVariant[0], false, null);
    if(Double.parseDouble(productsItem.getStock().toString()) >0) {
        orderActivity.addProduct(productsItem.getId(), false, false, null);
        dismiss();
    }else {
        Toast.makeText(orderActivity, "Not enough product in stock", Toast.LENGTH_SHORT).show();
    }
    }
}
