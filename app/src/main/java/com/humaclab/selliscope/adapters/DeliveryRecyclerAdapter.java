package com.humaclab.selliscope.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.Utils.SessionManager;
import com.humaclab.selliscope.model.DeliverProductResponse;
import com.humaclab.selliscope.model.DeliveryResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tonmoy on 5/18/17.
 */

public class DeliveryRecyclerAdapter extends RecyclerView.Adapter<DeliveryRecyclerAdapter.DeliveryDetailsViewHolder> {
    private Context context;
    private List<DeliveryResponse.Product> products;
    private DeliveryResponse.DeliveryList deliveryList;

    public DeliveryRecyclerAdapter(Context context, DeliveryResponse.DeliveryList deliveryList) {
        this.context = context;
        this.deliveryList = deliveryList;
        this.products = this.deliveryList.productList;
    }

    @Override
    public DeliveryDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_details_item, parent, false);
        return new DeliveryDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeliveryDetailsViewHolder holder, int position) {
        final DeliveryResponse.Product product = products.get(position);
        holder.getBinding().setVariable(BR.product, product);
        holder.getBinding().executePendingBindings();

        final int[] qty = {Integer.parseInt(holder.et_qty.getText().toString())};
        holder.btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty[0] < product.qty) {
                    holder.et_qty.setText(String.valueOf(qty[0] + 1));
                    qty[0] = qty[0] + 1;
                } else {
                    holder.et_qty.setText("1");
                    qty[0] = 1;
                }
            }
        });
        holder.btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty[0] > 1) {
                    holder.et_qty.setText(String.valueOf(qty[0] - 1));
                    qty[0] = qty[0] - 1;
                } else {
                    holder.et_qty.setText("0");
                    qty[0] = 0;
                }
            }
        });
        holder.btn_deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliverProductResponse deliverProductResponse = new DeliverProductResponse();
                DeliverProductResponse.Order order = new DeliverProductResponse.Order();
                DeliverProductResponse.Order.Product product1 = new DeliverProductResponse.Order.Product();
//                deliverProductResponse.order.products = new ArrayList<>();

                order.products = new ArrayList<>();
                product1.productId = product.productId;
                product1.qty = Integer.parseInt(holder.et_qty.getText().toString());
                order.products.add(product1);
                order.orderId = deliveryList.deliveryId;
                order.outletId = deliveryList.outletId;
                deliverProductResponse.order = order;

                //Deliver api request
                SessionManager sessionManager = new SessionManager(context);
                SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                        sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
                Call<DeliverProductResponse> call = apiService.deliverProduct(deliverProductResponse);
                call.enqueue(new Callback<DeliverProductResponse>() {
                    @Override
                    public void onResponse(Call<DeliverProductResponse> call, Response<DeliverProductResponse> response) {
                        if (response.code() == 201) {
                            holder.btn_deliver.setEnabled(false);
                            holder.btn_deliver.setBackgroundColor(Color.parseColor("#dddddd"));
                            Toast.makeText(context, "Product delivered successfully", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            Toast.makeText(context, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DeliverProductResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                System.out.println("Deliver: " + new Gson().toJson(deliverProductResponse));
                //Deliver api request
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class DeliveryDetailsViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        private Button btn_decrease, btn_increase, btn_deliver;
        private EditText et_qty;

        public DeliveryDetailsViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            btn_decrease = (Button) itemView.findViewById(R.id.btn_decrease);
            btn_increase = (Button) itemView.findViewById(R.id.btn_increase);
            btn_deliver = (Button) itemView.findViewById(R.id.btn_deliver);
            et_qty = (EditText) itemView.findViewById(R.id.et_qty);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
