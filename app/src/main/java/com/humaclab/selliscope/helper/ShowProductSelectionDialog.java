package com.humaclab.selliscope.helper;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.os.Build;

import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.databinding.ItemOrderProductSelectionBinding;
import com.humaclab.selliscope.dbmodel.PriceVariationStartEnd;
import com.humaclab.selliscope.dbmodel.TradePromoionData;
import com.humaclab.selliscope.model.variant_product.ProductsItem;
import com.humaclab.selliscope.utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by leon on 14/3/18.
 */

public class ShowProductSelectionDialog {
    private Context context;
    private ProductsItem productsItem;
    private SelectedProductHelper selectedProductHelper;
    private ItemOrderProductSelectionBinding binding;
    private AlertDialog alertDialog;
    private OnDialogSelectListener mOnDialogSelectListener;
    //  private OrderProductRecyclerAdapter.OrderProductViewHolder orderProductRecyclerAdapter;
    //Set a base Rate Global
    private double priceOfRate;
    private String outletType;
    private double discount = 0.0;
    private double totalPrice = 0.0;
    private DatabaseHandler mDatabaseHandler;

    public ShowProductSelectionDialog(Context context, ProductsItem productsItem, String outletType, OnDialogSelectListener onDialogSelectListener) {
        this.context = context;
        this.productsItem = productsItem;
        this.alertDialog = new AlertDialog.Builder(context).create();
        this.outletType = outletType;
        mOnDialogSelectListener = onDialogSelectListener;
        mDatabaseHandler = new DatabaseHandler(context);
    }


    public void setSelectedProduct(SelectedProductHelper selectedProductHelper) {
        this.selectedProductHelper = selectedProductHelper;
    }

    public void showDialog() {
        //Set the global Number
        if (productsItem.getPrice() != null)
            priceOfRate = Double.valueOf(productsItem.getPrice().replace(",", ""));
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_order_product_selection, null, false);
        binding.tvProductName.setText( productsItem.getName());
        binding.tvProductPrice.setText(String.format(Locale.ENGLISH,"%,.2f", priceOfRate));
        binding.tvProductStock.setText(String.format(Locale.ENGLISH,"%,.2f", Double.parseDouble(productsItem.getStock())));

        if (selectedProductHelper != null) {
            if(!selectedProductHelper.isFree()) {
                binding.etProductQty.setText(selectedProductHelper.getProductQuantity());
                binding.tvTotalPrice.setText(String.format(Locale.ENGLISH, "%,.2f", Double.valueOf(binding.tvProductPrice.getText().toString().replace(",", "")) * Double.valueOf(binding.etProductQty.getText().toString())));

                try {
                    List<TradePromoionData> tradePromoionData = mDatabaseHandler.getTradePromoion(productsItem.getId());
                    if (tradePromoionData.size() > 0) {
                        binding.tvOfferName.setText("Free: " + tradePromoionData.get(0).getFreeProductName());
                        binding.tvOfferQty.setText(String.format(Locale.ENGLISH, "%,.2f", (Integer.parseInt(binding.etProductQty.getText().toString()) / Integer.parseInt(tradePromoionData.get(0).getPromoionValue())) * Integer.parseInt(tradePromoionData.get(0).getFreeProductQty())));
                    }
                } catch (NumberFormatException e) {
                    Log.d("tareq_test", "Eroor while reloading Product Selection Dialog: " + e.getMessage());
                }
            }

        }

        binding.etProductQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                discount = 0.0;
                binding.tvOfferName.setText("");
                binding.tvOfferQty.setText("");
                binding.tvDiscount.setText((String.format(Locale.ENGLISH, "%,.2f", discount)));
                binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%,.2f", totalPrice));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Price Variation

                List<PriceVariationStartEnd> priceVariationList = mDatabaseHandler.getpriceVariationStartEndEprice(productsItem.getId(), outletType, Integer.parseInt(productsItem.getVariantRow()));
                List<TradePromoionData> tradePromoionData = mDatabaseHandler.getTradePromoion(productsItem.getId());
                String outletTypeName = outletType;
                int size = priceVariationList.size();
                try {
                    String inputPrice = s.toString();
                    double inputFinalPrice = Double.valueOf(inputPrice);
                    if (!inputPrice.equals(""))
                        for (int i = 0; i < size; i++) {

                            if (Math.max(priceVariationList.get(i).getStartRange(), inputFinalPrice) == Math.min(inputFinalPrice, priceVariationList.get(i).getEndRange())) {
                                //binding.tvProductPrice.setText(String.valueOf(priceVariationList.get(i).getPriceRange()));
                                priceOfRate = priceVariationList.get(i).getPriceRange();
                                //binding.tvProductPrice.setText(String.valueOf(priceOfRate));
                                break;
                            } else {
                                priceOfRate = Double.valueOf(productsItem.getPrice());
                            }

                        }
                    binding.tvProductPrice.setText(String.format(Locale.ENGLISH, "%,.2f", priceOfRate));
                    //binding.tvTotalPrice.setText(String.format("%.2f", Double.valueOf(binding.tvProductPrice.getText().toString().replace(",", "")) * Double.valueOf(binding.etProductQty.getText().toString())));
                    binding.tvTotalPrice.setText(String.format(Locale.ENGLISH, "%,.2f",  Double.valueOf(binding.tvProductPrice.getText().toString().replace(",", "")) * Double.valueOf(binding.etProductQty.getText().toString())));

                    totalPrice = Double.valueOf(String.valueOf( binding.tvTotalPrice.getText()).replace(",",""));
                    if (Double.valueOf(tradePromoionData.get(0).getPromoionValue()) <= inputFinalPrice && !inputPrice.equals("")) {

                        int countOfferNumber = (int) (inputFinalPrice / Double.valueOf(tradePromoionData.get(0).getPromoionValue()));
                        double offerValue = Double.valueOf(tradePromoionData.get(0).getOfferValue());

                        switch (tradePromoionData.get(0).getOfferType()) {
                            case "Percentage":

                                discount = (totalPrice * offerValue) / 100;
                                binding.tvOfferName.setText(tradePromoionData.get(0).getPromoionTitle());
                                binding.tvOfferQty.setText( tradePromoionData.get(0).getOfferType());
                                binding.tvDiscount.setText((String.format(Locale.ENGLISH, "%,.2f", discount)));
                                break;
                            case "Flat":
                                discount = offerValue * countOfferNumber;
                                binding.tvOfferName.setText(tradePromoionData.get(0).getPromoionTitle());
                                binding.tvOfferQty.setText(tradePromoionData.get(0).getOfferType());
                                binding.tvDiscount.setText((String.format(Locale.ENGLISH, "%,.2f", discount)));
                                break;
                            case "Qty":
                                discount = 0.0;
                                binding.tvOfferName.setText("Free: " + tradePromoionData.get(0).getFreeProductName());
                                binding.tvOfferQty.setText(String.valueOf((Integer.parseInt(binding.etProductQty.getText().toString()) / Integer.parseInt(tradePromoionData.get(0).getPromoionValue())) * Integer.parseInt(tradePromoionData.get(0).getFreeProductQty())));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    binding.layoutSelect.setBackground(context.getDrawable(R.color.background));
                                }
                                binding.tvDiscount.setText((String.valueOf(discount)));

                                break;
                            default:
                                discount = 0.0;
                                break;

                        }
                        //Changing for specific option
                       /* binding.tvOffer.setText(tradePromoionData.get(0).getPromoionTitle());
                        binding.tvDiscountName.setText(tradePromoionData.get(0).getOfferType());
                        binding.tvDiscount.setText((String.valueOf(discount)));*/
                        /*
                            if(tradePromoionData.get(0).getOfferType().equals("Percentage")){
                            double totalPrice = Double.valueOf(String.valueOf(binding.tvTotalPrice.getText()));
                            discount =  (totalPrice*offerValue) / 100 ;
                        }
                        else if(tradePromoionData.get(0).getOfferType().equals("Flat")){
                            discount = offerValue * countOfferNumber;
                        }

                        binding.tvOffer.setText(tradePromoionData.get(0).getPromoionTitle());
                        binding.tvDiscountName.setText(tradePromoionData.get(0).getOfferType());
                        binding.tvDiscount.setText((String.valueOf(discount)));
                        */

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                binding.tvGrandTotal.setText(String.format(Locale.ENGLISH, "%,.2f", totalPrice - discount));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSelectProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etProductQty.getText().toString().equals("")) {
                    if (!binding.etProductQty.getText().toString().equals("0")) {

                        List<TradePromoionData> tradePromoionData = mDatabaseHandler.getTradePromoion(productsItem.getId());

                        SelectedProductHelper selectedOrderProduct = new SelectedProductHelper(
                                String.valueOf(productsItem.getId()),
                                productsItem.getName(),
                                binding.etProductQty.getText().toString(),
                                //productsItem.getPrice(),
                                binding.tvProductPrice.getText().toString(),
                                binding.tvTotalPrice.getText().toString(),
                                productsItem.getVariantRow() == null ? "0" : productsItem.getVariantRow(),
                                binding.tvDiscount.getText().toString() == null ? "0" : binding.tvDiscount.getText().toString(),
                                binding.tvGrandTotal.getText().toString() == null ? "0" : binding.tvGrandTotal.getText().toString(),
                                false,
                                binding.tvOfferName.getText().toString().length()>0? binding.tvOfferName.getText().toString()+" - "+binding.tvOfferQty.getText().toString():"PromotionalDiscount"

                        );

                        List<SelectedProductHelper> selectedProductHelperList= new ArrayList<>();
                        selectedProductHelperList.add(selectedOrderProduct);
                        try {
                            if(tradePromoionData.size()>0) {
                                SelectedProductHelper selectedFreeProduct = new SelectedProductHelper(
                                        String.valueOf(tradePromoionData.get(0).getFreeProductId()),
                                        tradePromoionData.get(0).getFreeProductName(),
                                        //Dividing total quantity by promotion value then multiplied by the free quantity
                                        String.valueOf((Integer.parseInt(binding.etProductQty.getText().toString()) / Integer.parseInt(tradePromoionData.get(0).getPromoionValue())) * Integer.parseInt(tradePromoionData.get(0).getFreeProductQty())),
                                        "0",
                                        "0",
                                        "0",
                                        "0",
                                        "0",
                                        true
                                );
                                selectedProductHelperList.add(selectedFreeProduct);
                            }
                        } catch (NumberFormatException e) {Log.e("tareq_test" , "Free Product not found");
                        }
                        Log.d("tareq_test" , "Slected "+new Gson().toJson(selectedProductHelperList));
                        mOnDialogSelectListener.onOrderSelect(selectedProductHelperList);
                        //  orderProductRecyclerAdapter.onSetSelectedProduct(selectedProduct);
                        alertDialog.dismiss();
                        Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        View view = binding.etProductQty;
                        binding.etProductQty.setError("0 should not be apply");
                        view.requestFocus();
                    }
                } else {
                    View view = binding.etProductQty;
                    binding.etProductQty.setError(context.getString(R.string.error_field_required));
                    view.requestFocus();
                }
            }
        });

        binding.btnCancel.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.setView(binding.getRoot());
        alertDialog.show();
    }

    public interface OnDialogSelectListener {
        void onOrderSelect(List<SelectedProductHelper> selectedProductHelpers);
    }
}
