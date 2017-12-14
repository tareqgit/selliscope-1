package com.humaclab.selliscope_myone.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_myone.BR;
import com.humaclab.selliscope_myone.R;
import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.Utils.SessionManager;
import com.humaclab.selliscope_myone.model.Payment;
import com.humaclab.selliscope_myone.model.PaymentResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tonmoy on 5/22/17.
 */

public class PaymentRecyclerViewAdapter extends RecyclerView.Adapter<PaymentRecyclerViewAdapter.PaymentViewHolder> {
    private Context context;
    private List<Payment.OrderList> orderLists;
    private SelliscopeApiEndpointInterface apiService;
    private ProgressDialog pd;

    public PaymentRecyclerViewAdapter(Context context, List<Payment.OrderList> orderLists) {
        this.context = context;
        this.orderLists = orderLists;
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_item, parent, false);
        pd = new ProgressDialog(view.getContext());
        pd.setMessage("Payment is in process...");
        pd.setCancelable(false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PaymentViewHolder holder, final int position) {
        final Payment.OrderList orderList = orderLists.get(position);
        holder.getBinding().setVariable(BR.payments, orderList);
        holder.getBinding().executePendingBindings();
        holder.btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                SessionManager sessionManager = new SessionManager(context);
                apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                        sessionManager.getUserPassword(), false)
                        .create(SelliscopeApiEndpointInterface.class);
                PaymentResponse paymentResponse = new PaymentResponse();
                PaymentResponse.Payment payment = new PaymentResponse.Payment();
                payment.order_id = orderList.orderId;
                payment.outlet_id = orderList.outletId;
                payment.type = (holder.sp_payment_type.getSelectedItemPosition() == 0) ? 1 : 2;
                payment.amount = Integer.parseInt(holder.et_payment.getText().toString());
                paymentResponse.payment = payment;
                Call<PaymentResponse.PaymentSucessfull> call = apiService.payNow(paymentResponse);
                call.enqueue(new Callback<PaymentResponse.PaymentSucessfull>() {
                    @Override
                    public void onResponse(Call<PaymentResponse.PaymentSucessfull> call, Response<PaymentResponse.PaymentSucessfull> response) {
                        if (response.code() == 201) {
                            try {
                                Log.d("Payment response", new Gson().toJson(response.body().result));
                                pd.dismiss();
                                Toast.makeText(context, "Payment received successfully.", Toast.LENGTH_SHORT).show();
                                orderLists.remove(position);
                                PaymentRecyclerViewAdapter.this.notifyItemRemoved(position);
                                PaymentRecyclerViewAdapter.this.notifyItemRangeChanged(position, orderLists.size());
                            } catch (Exception e) {
                                pd.dismiss();
                                e.printStackTrace();
                            }
                        } else if (response.code() == 400) {
                            pd.dismiss();
                        } else {
                            pd.dismiss();
                            Toast.makeText(context,
                                    "Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse.PaymentSucessfull> call, Throwable t) {
                        pd.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        private Button btn_pay;
        private Spinner sp_payment_type;
        private EditText et_payment;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            btn_pay = (Button) itemView.findViewById(R.id.btn_pay);
            sp_payment_type = (Spinner) itemView.findViewById(R.id.sp_payment_type);
            et_payment = (EditText) itemView.findViewById(R.id.et_payment);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
