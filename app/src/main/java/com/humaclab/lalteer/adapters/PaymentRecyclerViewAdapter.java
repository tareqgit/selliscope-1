package com.humaclab.lalteer.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humaclab.lalteer.BR;
import com.humaclab.lalteer.R;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.activity.PaymentActivity;
import com.humaclab.lalteer.model.Payment;
import com.humaclab.lalteer.model.PaymentResponse;
import com.humaclab.lalteer.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tareq on 5/3/19.
 */

public class PaymentRecyclerViewAdapter extends RecyclerView.Adapter<PaymentRecyclerViewAdapter.PaymentViewHolder> implements PaymentActivity.OnImageResultAchiveListener {
    private Context context;
    private final int CAMERA_REQUEST = 3214;
    public List<Payment.OrderList> orderLists;
    private SelliscopeApiEndpointInterface apiService;
    private ProgressDialog pd;
    private OnPaymentListener mOnPaymentListener;

    private static List<Integer> orderIDList = new ArrayList<>();

   static PaymentResponse.Payment temo_Payment;
    private int butClickedPos = 0;
    private int butClickedPos_spinner = 0;

    /*public PaymentRecyclerViewAdapter(Context context, List<Payment.OrderList> orderLists) {
        this.context = context;
        this.orderLists = orderLists;
    }*/
    public PaymentRecyclerViewAdapter(Context context, List<Payment.OrderList> orderLists, OnPaymentListener onPaymentListener) {
        this.context = context;
        this.orderLists = orderLists;
        this.mOnPaymentListener = onPaymentListener;
        temo_Payment = new PaymentResponse.Payment();

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
        // holder.setIsRecyclable(false);
        final Payment.OrderList orderList = orderLists.get(position);

        holder.getBinding().setVariable(BR.payments, orderList);
        holder.getBinding().executePendingBindings();

/*
//If there is value in this field than get that value
        if(temo_Payment!=null) {
            if (temo_Payment.amount != 0)
                temo_Payment.amount = Integer.parseInt(holder.et_payment.getText().toString().equals("") ? "0" : holder.et_payment.getText().toString().trim());
            if (temo_Payment.deposit_to!=null && !temo_Payment.deposit_to.equals("") )
                temo_Payment.deposit_to = holder.depositTo.getText().toString();
            if (temo_Payment.deposit_slip!=null && !temo_Payment.deposit_slip.equals("") )
                temo_Payment.deposit_slip = holder.depositSlip.getText().toString();
            if (temo_Payment.bank_name!=null  && !temo_Payment.bank_name.equals("") )
                temo_Payment.bank_name = holder.bankName.getText().toString();
            if (temo_Payment.cheque_no!=null &&  !temo_Payment.cheque_no.equals("") )
                temo_Payment.cheque_no = holder.chequeNumber.getText().toString();
            if (temo_Payment.cheque_date!=null && !temo_Payment.cheque_date.equals("") )
                temo_Payment.cheque_date = holder.date_view.getText().toString();
        }
        Log.d("tareq_test", butClickedPos + " - " + orderList.orderId);
*/


        if (orderList.orderId == butClickedPos) {
            holder.sp_payment_type.setSelection(butClickedPos_spinner);
            Log.d("tareq_test", "h_ " + temo_Payment.amount + " " + temo_Payment.deposit_to);

            holder.et_payment.setText("" + temo_Payment.amount);
            holder.bankName.setText(temo_Payment.bank_name);
            holder.depositTo.setText(temo_Payment.deposit_to);

            holder.chequeNumber.setText(temo_Payment.cheque_no);
            holder.depositSlip.setText(temo_Payment.deposit_slip);
            holder.date_view.setText(temo_Payment.cheque_date);
        } else {

            holder.sp_payment_type.setSelection(0);
            holder.et_payment.setText("");
            holder.bankName.setText("");
            holder.depositTo.setText("");

            holder.chequeNumber.setText("");
            holder.depositSlip.setText("");
            holder.date_view.setText("Cheque Date");
        }


        holder.mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                ((Activity) v.getContext()).startActivityForResult(cameraIntent, CAMERA_REQUEST);
                butClickedPos = orderList.orderId;
                butClickedPos_spinner = holder.sp_payment_type.getSelectedItemPosition();

                temo_Payment.amount = Integer.parseInt(holder.et_payment.getText().toString().equals("") ? "0" : holder.et_payment.getText().toString().trim());
                temo_Payment.deposit_to = holder.depositTo.getText().toString();
                temo_Payment.deposit_slip = holder.depositSlip.getText().toString();
                temo_Payment.bank_name = holder.bankName.getText().toString();
                temo_Payment.cheque_no = holder.chequeNumber.getText().toString();
                temo_Payment.cheque_date = holder.date_view.getText().toString();
                Log.d("tareq_test", "id " + butClickedPos);
            }
        });

        ((PaymentActivity) context).setOnImageResultAchiveListener(this);

        //for setting image when user captured an image else no need to set
        if (orderList.orderId == butClickedPos) {
            if (orderList.getPhoto() != null)
                holder.mImageButton.setImageBitmap(orderList.getPhoto());
        } else {
            holder.mImageButton.setImageResource(R.drawable.addimage);
        }


        holder.sp_payment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {

                    case 0: {
                        holder.bankName.setVisibility(View.GONE);
                        holder.sp_bank_name.setVisibility(View.GONE);
                        holder.chequeNumber.setVisibility(View.GONE);
                        holder.depositTo.setVisibility(View.VISIBLE);
                        holder.date_view.setVisibility(View.GONE);
                        holder.depositSlip.setVisibility(View.VISIBLE);
                        holder.mImageButton.setVisibility(View.VISIBLE);

                        break;
                    }
                    case 1: {
                        holder.bankName.setVisibility(View.VISIBLE);
                        holder.sp_bank_name.setVisibility(View.VISIBLE);
                        holder.chequeNumber.setVisibility(View.VISIBLE);
                        holder.depositTo.setVisibility(View.VISIBLE);
                        holder.depositSlip.setVisibility(View.VISIBLE);
                        holder.date_view.setVisibility(View.VISIBLE);
                        holder.mImageButton.setVisibility(View.VISIBLE);
                        butClickedPos = orderList.orderId;
                        butClickedPos_spinner = holder.sp_payment_type.getSelectedItemPosition();

                        Log.d("tareq_test", "id " + butClickedPos);
                        break;
                    }
                    case 2: {
                        holder.bankName.setVisibility(View.VISIBLE);
                        holder.sp_bank_name.setVisibility(View.VISIBLE);
                        holder.chequeNumber.setVisibility(View.GONE);
                        holder.depositTo.setVisibility(View.VISIBLE);
                        holder.depositSlip.setVisibility(View.VISIBLE);
                        holder.date_view.setVisibility(View.VISIBLE);
                        holder.mImageButton.setVisibility(View.VISIBLE);
                        butClickedPos = orderList.orderId;
                        butClickedPos_spinner = holder.sp_payment_type.getSelectedItemPosition();
                        Log.d("tareq_test", "id " + butClickedPos);
                        break;
                    }
                    default: {
                        Toast.makeText(context, "default", Toast.LENGTH_SHORT).show();
                    }

                }


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
//                payment.type = (holder.sp_payment_type.getSelectedItemPosition() == 0) ? 1 : 2;
                payment.type = holder.sp_payment_type.getSelectedItemPosition() + 1;

                if (holder.et_payment.getText().toString().equals("")) {
                    View view = holder.et_payment;
                    holder.et_payment.setError(context.getString(R.string.error_field_required));
                    view.requestFocus();
                    pd.dismiss();
                    return;
                }else if(Double.parseDouble(holder.et_payment.getText().toString())> Double.parseDouble(holder.tvGrandTotal.getText().toString())) {
                    View view = holder.et_payment;
                    holder.et_payment.setError("Payment Value Exceed");
                    view.requestFocus();
                    pd.dismiss();
                }else {
                    payment.amount = Double.parseDouble(holder.et_payment.getText().toString());

                }

             payment.bank_name = payment.type!=1 ?  holder.sp_bank_name.getSelectedItem().toString()+" ("+holder.bankName.getText().toString() +")" : "";



                if (payment.type == 2) { //for cheque
                    if (holder.chequeNumber.getText().toString().equals("")) {
                        View view = holder.chequeNumber;
                        holder.chequeNumber.setError(context.getString(R.string.error_field_required));
                        view.requestFocus();
                        pd.dismiss();
                        return;
                    }


                    if (holder.date_view.getText().toString().equals("")) {
                        View view = holder.date_view;
                        holder.date_view.setError(context.getString(R.string.error_field_required));
                        view.requestFocus();
                        pd.dismiss();
                        return;

                    }

                    if (holder.sp_bank_name.getSelectedItemPosition()==0) {
                        View view = holder.sp_bank_name;
                        TextView errorText = (TextView)holder.sp_bank_name.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Bank name must be selected");//changes the selected item text to this

                        view.requestFocus();
                        pd.dismiss();
                        return;

                    }


                }
                payment.cheque_no = holder.chequeNumber.getText().toString();
                payment.cheque_date = payment.type !=1? holder.date_view.getText().toString() : "";
                payment.deposit_to = holder.depositTo.getText().toString();
                payment.deposit_from = sessionManager.getUserEmail().toString();
                if (payment.type == 3) {
                    if (holder.depositSlip.getText().toString().equals("")) {
                        View view = holder.depositSlip;
                        holder.depositSlip.setError(context.getString(R.string.error_field_required));
                        view.requestFocus();
                        pd.dismiss();
                        return;

                    }

                    if (holder.sp_bank_name.getSelectedItemPosition()==0) {
                        View view = holder.sp_bank_name;
                        TextView errorText = (TextView)holder.sp_bank_name.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Bank name must be selected");//changes the selected item text to this

                        view.requestFocus();
                        pd.dismiss();
                        return;

                    }

                }
                payment.deposit_slip = holder.depositSlip.getText().toString();

                payment.img = orderList.getImg(); //as this is getting from activity using listener
                paymentResponse.payment = payment;

                Log.d("tareq_test", "payment res: " + new Gson().toJson(paymentResponse));
                Call<PaymentResponse.PaymentSucessfull> call = apiService.payNow(paymentResponse);
                call.enqueue(new Callback<PaymentResponse.PaymentSucessfull>() {
                    @Override
                    public void onResponse(Call<PaymentResponse.PaymentSucessfull> call, Response<PaymentResponse.PaymentSucessfull> response) {
                        if (response.code() == 200) {
                            try {
                                if (response.body() != null) {
                                    Log.d("Payment response", new Gson().toJson(response.body().result));
                                }
                                pd.dismiss();
                                Toast.makeText(context, "Payment received successfully.", Toast.LENGTH_SHORT).show();
                                orderLists.remove(position);
                                PaymentRecyclerViewAdapter.this.notifyItemRemoved(position);
                                PaymentRecyclerViewAdapter.this.notifyItemRangeChanged(position, orderLists.size());
                                mOnPaymentListener.onPaymentComplete();
                            } catch (Exception e) {
                                pd.dismiss();
                                e.printStackTrace();
                            }
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

    /**
     * callback for capturing image from onActivityResult
     *
     * @param image
     */
    @Override
    public void onImageAchive(Bitmap image, String img) {
        //Update new data into holder; this is the only way
        int pos = 0;
        for (int i = 0; i < orderLists.size(); i++) {
            if (orderLists.get(i).orderId == butClickedPos)
                pos = i;
        }

        orderLists.get(pos).setPhoto(image);
        orderLists.get(pos).setImg(img);
        notifyDataSetChanged();

    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        public ViewDataBinding binding;
        private Button btn_pay;
        private Spinner sp_payment_type, sp_bank_name;
        private EditText et_payment, bankName, chequeNumber, depositSlip, depositTo;
        private TextView date_view, tvGrandTotal;
        private ImageView mImageButton;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            btn_pay = (Button) itemView.findViewById(R.id.btn_pay);
            sp_payment_type = (Spinner) itemView.findViewById(R.id.sp_payment_type);
            et_payment = (EditText) itemView.findViewById(R.id.et_payment);
            bankName = (EditText) itemView.findViewById(R.id.et_branch_name);
            sp_bank_name=(Spinner) itemView.findViewById(R.id.spinner_bank_name);
            chequeNumber = (EditText) itemView.findViewById(R.id.et_cheque_no);
            depositSlip = (EditText) itemView.findViewById(R.id.et_deposit_slip);
            mImageButton = itemView.findViewById(R.id.imageButton);
            date_view = itemView.findViewById(R.id.textView_date);
            depositTo = itemView.findViewById(R.id.et_deposit_to);
            tvGrandTotal=itemView.findViewById(R.id.tv_grand_total);


            /**
             * on Bank Item selected show all else set visibility gone
             */


            /**
             * Start camera for image
             */
      /*      itemView.findViewById(R.id.imageButton).setOnClickListener((v)->{
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                ((Activity) itemView.getContext()).startActivityForResult(cameraIntent, CAMERA_REQUEST);

            //  mOnT_imageClickListener.onImageClick();
            });*/


            /**
             * open date picker for setting date
             */
            date_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    final int day = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(itemView.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            ((TextView) date_view).setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                        }
                    }, year, month, day);
                    datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                    datePickerDialog.show();
                }
            });


        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }

    public interface OnPaymentListener {
        void onPaymentComplete();
    }
}
