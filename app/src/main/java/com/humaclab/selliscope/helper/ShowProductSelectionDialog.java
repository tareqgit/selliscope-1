package com.humaclab.selliscope.helper;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.humaclab.selliscope.R;
import com.humaclab.selliscope.adapters.OrderProductRecyclerAdapter;
import com.humaclab.selliscope.databinding.ItemOrderProductSelectionBinding;
import com.humaclab.selliscope.dbmodel.PriceVariationStartEnd;
import com.humaclab.selliscope.dbmodel.TradePromoionData;
import com.humaclab.selliscope.model.VariantProduct.ProductsItem;
import com.humaclab.selliscope.utils.DatabaseHandler;

import java.util.List;

/**
 * Created by leon on 14/3/18.
 */

public class ShowProductSelectionDialog {
    private Context context;
    private ProductsItem productsItem;
    private SelectedProductHelper selectedProductHelper;
    private ItemOrderProductSelectionBinding binding;
    private AlertDialog alertDialog;
    private OrderProductRecyclerAdapter.OrderProductViewHolder orderProductRecyclerAdapter;
    //Set a base Rate Global
    private double priceOfRate;
    private String outletType;
    private double discount  = 0.0;
    private double totalPrice = 0.0;

    public ShowProductSelectionDialog(OrderProductRecyclerAdapter.OrderProductViewHolder orderProductRecyclerAdapter, Context context, ProductsItem productsItem, String outletType) {
        this.orderProductRecyclerAdapter = orderProductRecyclerAdapter;
        this.context = context;
        this.productsItem = productsItem;
        this.alertDialog = new AlertDialog.Builder(context).create();
        this.outletType = outletType;
    }

    public void setSelectedProduct(SelectedProductHelper selectedProductHelper) {
        this.selectedProductHelper = selectedProductHelper;
    }

    public void showDialog() {
        //Set the global Number
        priceOfRate = Double.valueOf(productsItem.getPrice().replace(",",""));
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_order_product_selection, null, false);
        binding.tvProductName.setText(productsItem.getName());
        binding.tvProductPrice.setText(String.valueOf(priceOfRate));
        binding.tvProductStock.setText(productsItem.getStock());

        if (selectedProductHelper != null) {
            binding.etProductQty.setText(selectedProductHelper.getProductQuantity());
            binding.tvTotalPrice.setText(String.format("%.2f", Double.valueOf(binding.tvProductPrice.getText().toString().replace(",", "")) * Double.valueOf(binding.etProductQty.getText().toString())));
        }

        binding.etProductQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                discount  = 0.0;
                binding.tvOffer.setText("");
                binding.tvDiscountName.setText("");
                binding.tvDiscount.setText((String.valueOf(discount)));
                binding.tvGrandTotal.setText(String.valueOf(totalPrice));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Price Variation
                DatabaseHandler databaseHandler = new DatabaseHandler(context);
                List<PriceVariationStartEnd> priceVariationList = databaseHandler.getpriceVariationStartEndEprice(productsItem.getId(),outletType, Integer.parseInt(productsItem.getVariantRow()));
                List<TradePromoionData> tradePromoionData = databaseHandler.getTradePromoion(productsItem.getId());
                String outletTypeName= outletType;
                int size = priceVariationList.size();
                try {
                    String inputPrice = s.toString();
                    double inputFinalPrice = Double.valueOf(inputPrice);
                    if (!inputPrice.equals(""))
                        for(int i = 0; i<size; i++ ){

                            if(Math.max(priceVariationList.get(i).getStartRange(), inputFinalPrice) == Math.min(inputFinalPrice, priceVariationList.get(i).getEndRange())){
                                //binding.tvProductPrice.setText(String.valueOf(priceVariationList.get(i).getPriceRange()));
                                priceOfRate = priceVariationList.get(i).getPriceRange();
                                //binding.tvProductPrice.setText(String.valueOf(priceOfRate));
                                break;
                            }
                            else {
                                priceOfRate = Double.valueOf(productsItem.getPrice());
                            }

                        }
                        binding.tvProductPrice.setText(String.valueOf(priceOfRate));
                        //binding.tvTotalPrice.setText(String.format("%.2f", Double.valueOf(binding.tvProductPrice.getText().toString().replace(",", "")) * Double.valueOf(binding.etProductQty.getText().toString())));
                        binding.tvTotalPrice.setText(String.format("%.2f", Double.valueOf(binding.tvProductPrice.getText().toString().replace(",", "")) * Double.valueOf(binding.etProductQty.getText().toString())));

                        totalPrice = Double.valueOf(String.valueOf(binding.tvTotalPrice.getText()));
                    if ( Double.valueOf(tradePromoionData.get(0).getPromoionValue())<= inputFinalPrice && !inputPrice.equals("")) {

                        int countOfferNumber = (int) (inputFinalPrice/Double.valueOf(tradePromoionData.get(0).getPromoionValue()));
                        double offerValue = Double.valueOf(tradePromoionData.get(0).getOfferValue());

                        switch (tradePromoionData.get(0).getOfferType()){
                            case "Percentage":

                                discount =  (totalPrice*offerValue) / 100 ;
                                binding.tvOffer.setText(tradePromoionData.get(0).getPromoionTitle());
                                binding.tvDiscountName.setText(tradePromoionData.get(0).getOfferType());
                                binding.tvDiscount.setText((String.valueOf(discount)));
                                break;
                            case "Flat":
                                discount = offerValue * countOfferNumber;
                                binding.tvOffer.setText(tradePromoionData.get(0).getPromoionTitle());
                                binding.tvDiscountName.setText(tradePromoionData.get(0).getOfferType());
                                binding.tvDiscount.setText((String.valueOf(discount)));
                                break;
                            case "Qty":
                                discount = 0.0;
                                binding.tvDiscountName.setText(tradePromoionData.get(0).getProduct_qty());
                                binding.tvOffer.setText("Free: "+tradePromoionData.get(0).getProduct_name());
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

                binding.tvGrandTotal.setText(String.valueOf(totalPrice-discount));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSelectProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etProductQty.getText().toString().equals("")) {
                    if(!binding.etProductQty.getText().toString().equals("0")) {
                        SelectedProductHelper selectedProduct = new SelectedProductHelper(
                                String.valueOf(productsItem.getId()),
                                productsItem.getName(),
                                binding.etProductQty.getText().toString(),
                                //productsItem.getPrice(),
                                binding.tvProductPrice.getText().toString(),
                                binding.tvTotalPrice.getText().toString(),
                                productsItem.getVariantRow() == null ? "0" : productsItem.getVariantRow(),
                                binding.tvDiscount.getText().toString() == null ? "0" : binding.tvDiscount.getText().toString(),
                                binding.tvGrandTotal.getText().toString() == null ? "0" : binding.tvGrandTotal.getText().toString()
                        );

                        orderProductRecyclerAdapter.onSetSelectedProduct(selectedProduct);
                        alertDialog.dismiss();
                        Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
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

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(binding.getRoot());
        alertDialog.show();
    }
}
