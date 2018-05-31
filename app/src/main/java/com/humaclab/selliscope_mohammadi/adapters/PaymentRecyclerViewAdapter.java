package com.humaclab.selliscope_mohammadi.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.selliscope_mohammadi.BR;
import com.humaclab.selliscope_mohammadi.R;
import com.humaclab.selliscope_mohammadi.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_mohammadi.SelliscopeApplication;
import com.humaclab.selliscope_mohammadi.model.Payment;
import com.humaclab.selliscope_mohammadi.model.PaymentResponse;
import com.humaclab.selliscope_mohammadi.utils.SessionManager;

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

        holder.sp_payment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                    holder.ll_check_details.setVisibility(View.VISIBLE);
                else
                    holder.ll_check_details.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                switch (holder.sp_payment_type.getSelectedItemPosition()) {
                    case 0:
                        payment.type = 1;
                        break;
                    case 1:
                        payment.type = 2;
                        break;
                    case 2:
                        payment.type = 3;
                        break;
                }

                if (payment.type == 2) {
                    payment.depositedBank = holder.et_deposited_bank.getText().toString();
                    payment.depositedAccount = holder.et_deposited_account.getText().toString();
                    payment.depositForm = holder.et_deposit_form.getText().toString();
                }
                if (payment.type == 3) {
                    payment.depositedBank = holder.et_deposited_bank.getText().toString();
                    payment.depositedAccount = holder.et_deposited_account.getText().toString();
                    payment.depositForm = holder.et_deposit_form.getText().toString();
                }
                if (holder.et_payment.getText().toString().isEmpty()) {
                    holder.et_payment.setError(context.getString(R.string.error_field_required));
                    pd.dismiss();
                    return;
                } else {
                    payment.amount = Integer.parseInt(holder.et_payment.getText().toString());
                }
                paymentResponse.payment = payment;
                if (payment.type == 1) {
                    if (!holder.et_payment.getText().toString().isEmpty()) {
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
                    } else {
                        Toast.makeText(context, "PAyment Is Empty", Toast.LENGTH_SHORT).show();
                    }
                } else if (payment.type > 1) {
                    if (!isEmpty(holder)) {
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
                } else {
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
            }
        });
    }

    private boolean isEmpty(PaymentViewHolder holder) {
        View view;
        int count = 0;
        if (holder.et_deposited_bank.getText().toString().isEmpty()) {
            view = holder.et_deposited_bank;
            holder.et_deposited_bank.setError(context.getString(R.string.error_field_required));
            view.requestFocus();
            count++;
        }
        if (holder.et_deposited_account.getText().toString().isEmpty()) {
            view = holder.et_deposited_account;
            holder.et_deposited_account.setError(context.getString(R.string.error_field_required));
            view.requestFocus();
            count++;
        }
        if (holder.et_deposit_form.getText().toString().isEmpty()) {
            view = holder.et_deposit_form;
            holder.et_deposit_form.setError(context.getString(R.string.error_field_required));
            view.requestFocus();
            count++;
        }
        return count != 0;
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        private Button btn_pay;
        private Spinner sp_payment_type;
        private LinearLayout ll_check_details;
        private EditText et_payment, et_deposited_bank, et_deposited_account, et_deposit_form;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            btn_pay = itemView.findViewById(R.id.btn_pay);
            sp_payment_type = itemView.findViewById(R.id.sp_payment_type);
            et_payment = itemView.findViewById(R.id.et_payment);
            ll_check_details = itemView.findViewById(R.id.ll_check_details);
            et_deposited_bank = itemView.findViewById(R.id.et_deposited_bank);
            et_deposited_account = itemView.findViewById(R.id.et_deposited_account);
            et_deposit_form = itemView.findViewById(R.id.et_deposit_form);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
