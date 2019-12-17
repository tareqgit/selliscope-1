package com.easyopstech.easyops.adapters.adapter_selesReturnOld;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.RootApplication;
import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.databinding.SellsReturnDetailsItemOldBinding;
import com.easyopstech.easyops.model.model_sales_return_old.DeliveryResponseOld;
import com.easyopstech.easyops.model.model_sales_return_old.SellsReturnResponseOld;
import com.easyopstech.easyops.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leon on 5/18/17.
 */

public class SellsReturnDetailsRecyclerAdapterOld extends RecyclerView.Adapter<SellsReturnDetailsRecyclerAdapterOld.SellsReturnDetailsViewHolder> {
    private Context context;
    private List<DeliveryResponseOld.Product> products;
    private DeliveryResponseOld.DeliveryList deliveryList;

    public SellsReturnDetailsRecyclerAdapterOld(Context context, DeliveryResponseOld.DeliveryList deliveryList) {
        this.context = context;
        this.deliveryList = deliveryList;
        this.products = this.deliveryList.productList;
    }

    @Override
    public SellsReturnDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sells_return_details_item_old, parent, false);
        return new SellsReturnDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SellsReturnDetailsViewHolder holder, int position) {
        final DeliveryResponseOld.Product product = products.get(position);
        holder.getBinding().setProduct(product);
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
                SellsReturnResponseOld.SellsReturn sellsReturn = new SellsReturnResponseOld.SellsReturn();
                sellsReturn.outletID = deliveryList.outletId;
                sellsReturn.orderID = deliveryList.deliveryId;
                sellsReturn.productID = product.productId;
                sellsReturn.quantity = Integer.parseInt(holder.et_qty.getText().toString());
                sellsReturn.cause = holder.sp_return_cause.getSelectedItem().toString();

                //Sells return api request
                SessionManager sessionManager = new SessionManager(context);
                RootApiEndpointInterface apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                        sessionManager.getUserPassword(), false).create(RootApiEndpointInterface.class);
                Call<SellsReturnResponseOld> call = apiService.returnProductOld(sellsReturn);
                call.enqueue(new Callback<SellsReturnResponseOld>() {
                    @Override
                    public void onResponse(Call<SellsReturnResponseOld> call, Response<SellsReturnResponseOld> response) {
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
                    public void onFailure(Call<SellsReturnResponseOld> call, Throwable t) {
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
        private SellsReturnDetailsItemOldBinding binding;
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

        public SellsReturnDetailsItemOldBinding getBinding() {
            return binding;
        }
    }
}
