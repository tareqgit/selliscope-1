package com.humaclab.lalteer.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tonmoy on 5/22/17.
 */

public class PaymentRecyclerViewAdapter extends RecyclerView.Adapter<PaymentRecyclerViewAdapter.PaymentViewHolder> implements PaymentActivity.OnImageResultAchiveListener {
    private Context context;
    private final int CAMERA_REQUEST = 3214;
    public List<Payment.OrderList> orderLists;
    private SelliscopeApiEndpointInterface apiService;
    private ProgressDialog pd;
private static int butClickedPos=0;
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

        holder.mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                ((Activity) v.getContext()).startActivityForResult(cameraIntent, CAMERA_REQUEST);
                butClickedPos=position;
            }
        });

        ((PaymentActivity) context).setOnImageResultAchiveListener(this);

        //for setting image when user captured an image else no need to set
        if(orderList.getPhoto()!=null) holder.mImageButton.setImageBitmap(orderList.getPhoto());

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
                payment.type = holder.sp_payment_type.getSelectedItemPosition() +1;

                payment.amount = Integer.parseInt(holder.et_payment.getText().toString());

                payment.bank_name=holder.bankName.getText().toString();
                payment.cheque_no=holder.chequeNumber.getText().toString();
                payment.deposit_slip=holder.depositSlip.getText().toString();
                payment.cheque_date=holder.date_view.getText().toString();
                payment.img= orderLists.get(butClickedPos).getImg(); //as this is getting from activity using listener
                paymentResponse.payment = payment;

                Log.d("tareq_test" , ""+new Gson().toJson(paymentResponse));
                Call<PaymentResponse.PaymentSucessfull> call = apiService.payNow(paymentResponse);
                call.enqueue(new Callback<PaymentResponse.PaymentSucessfull>() {
                    @Override
                    public void onResponse(Call<PaymentResponse.PaymentSucessfull> call, Response<PaymentResponse.PaymentSucessfull> response) {
                        if (response.code() == 200) {
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

    /**
     * callback for capturing image from onActivityResult
     * @param image
     */
    @Override
    public void onImageAchive(Bitmap image, String img) {
        //Update new data into holder; this is the only way
         orderLists.get(butClickedPos).setPhoto(image);
         orderLists.get(butClickedPos).setImg(img);
          notifyDataSetChanged();

    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        public ViewDataBinding binding;
        private Button btn_pay;
        private Spinner sp_payment_type;
        private EditText et_payment, bankName, chequeNumber, depositSlip;
        private TextView date_view;
        private ImageView mImageButton;
        public PaymentViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            btn_pay = (Button) itemView.findViewById(R.id.btn_pay);
            sp_payment_type = (Spinner) itemView.findViewById(R.id.sp_payment_type);
            et_payment = (EditText) itemView.findViewById(R.id.et_payment);
            bankName = (EditText) itemView.findViewById(R.id.et_bank_name);
            chequeNumber = (EditText) itemView.findViewById(R.id.et_cheque_no);
            depositSlip = (EditText) itemView.findViewById(R.id.et_deposit_slip);
            mImageButton=itemView.findViewById(R.id.imageButton);
            date_view = itemView.findViewById(R.id.textView_date);


            /**
             * on Bank Item selected show all else set visibility gone
             */
            sp_payment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==2){
                        bankName.setVisibility(View.VISIBLE);
                        chequeNumber.setVisibility(View.VISIBLE);
                        depositSlip.setVisibility(View.VISIBLE);
                        date_view.setVisibility(View.VISIBLE);
                        mImageButton.setVisibility(View.VISIBLE);

                    }else{
                        bankName.setVisibility(View.GONE);
                        chequeNumber.setVisibility(View.GONE);
                        depositSlip.setVisibility(View.GONE);

                        date_view.setVisibility(View.GONE);
                        mImageButton.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


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
                    DatePickerDialog datePickerDialog= new DatePickerDialog(itemView.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            ( (TextView) date_view).setText(year + "-"+ (month+1) + "-"+dayOfMonth);
                        }
                    },year,month,day);
                    datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                    datePickerDialog.show();
                }
            });



        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }


}
