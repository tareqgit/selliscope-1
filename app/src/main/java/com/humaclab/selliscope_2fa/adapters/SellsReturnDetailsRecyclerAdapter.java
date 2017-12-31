package com.humaclab.selliscope_2fa.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.humaclab.selliscope_2fa.BR;
import com.humaclab.selliscope_2fa.R;
import com.humaclab.selliscope_2fa.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_2fa.SelliscopeApplication;
import com.humaclab.selliscope_2fa.model.DeliveryResponse;
import com.humaclab.selliscope_2fa.model.SellsReturnResponse;
import com.humaclab.selliscope_2fa.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leon on 5/18/17.
 */

public class SellsReturnDetailsRecyclerAdapter extends RecyclerView.Adapter<SellsReturnDetailsRecyclerAdapter.SellsReturnDetailsViewHolder> {
    private Context context;
    private List<DeliveryResponse.Product> products;
    private DeliveryResponse.DeliveryList deliveryList;

    public SellsReturnDetailsRecyclerAdapter(Context context, DeliveryResponse.DeliveryList deliveryList) {
        this.context = context;
        this.deliveryList = deliveryList;
        this.products = this.deliveryList.productList;
    }

    @Override
    public SellsReturnDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sells_return_details_item, parent, false);
        return new SellsReturnDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SellsReturnDetailsViewHolder holder, int position) {
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
        holder.btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SellsReturnResponse.SellsReturn sellsReturn = new SellsReturnResponse.SellsReturn();
                sellsReturn.outletID = deliveryList.outletId;
                sellsReturn.orderID = deliveryList.deliveryId;
                sellsReturn.productID = product.productId;
                sellsReturn.quantity = Integer.parseInt(holder.et_qty.getText().toString());
                sellsReturn.cause = holder.sp_return_cause.getSelectedItem().toString();

                //Sells return api request
                SessionManager sessionManager = new SessionManager(context);
                SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                        sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
                Call<SellsReturnResponse> call = apiService.returnProduct(sellsReturn);
                call.enqueue(new Callback<SellsReturnResponse>() {
                    @Override
                    public void onResponse(Call<SellsReturnResponse> call, Response<SellsReturnResponse> response) {
                        if (response.code() == 201) {
                            holder.btn_return.setEnabled(false);
                            holder.btn_return.setBackgroundColor(Color.parseColor("#dddddd"));
                            Toast.makeText(context, "Product returned successfully", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            Toast.makeText(context, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SellsReturnResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                //Sells return api request
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class SellsReturnDetailsViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        private Button btn_decrease, btn_increase, btn_return;
        private Spinner sp_return_cause;
        private EditText et_qty;

        public SellsReturnDetailsViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            btn_decrease = (Button) itemView.findViewById(R.id.btn_decrease);
            btn_increase = (Button) itemView.findViewById(R.id.btn_increase);
            btn_return = (Button) itemView.findViewById(R.id.btn_return);
            sp_return_cause = (Spinner) itemView.findViewById(R.id.sp_return_cause);
            et_qty = (EditText) itemView.findViewById(R.id.et_qty);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
