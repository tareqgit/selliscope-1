package com.humaclab.selliscope.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.humaclab.selliscope.BR;
import com.humaclab.selliscope.R;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.model.reason.ReasonResponse;
import com.humaclab.selliscope.model.sales_return.SalesReturnResponse;
import com.humaclab.selliscope.model.sales_return.SellsReturnResponsePost;
import com.humaclab.selliscope.utils.DatabaseHandler;
import com.humaclab.selliscope.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Leon on 5/18/17.
 */

public class SellsReturnDetailsRecyclerAdapter extends RecyclerView.Adapter<SellsReturnDetailsRecyclerAdapter.SellsReturnDetailsViewHolder> {
    private Context context;
    private List<SalesReturnResponse.Product> products;
    private SalesReturnResponse.DeliveryList deliveryList;
    private Calendar myCalendar = Calendar.getInstance();
    private int reasonID;
    DatabaseHandler  databaseHandler ;
    public SellsReturnDetailsRecyclerAdapter(Context context, SalesReturnResponse.DeliveryList deliveryList) {
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
        final SalesReturnResponse.Product product = products.get(position);
        holder.getBinding().setVariable(BR.product, product);
        holder.getBinding().executePendingBindings();

        final int[] qty = {Integer.parseInt(holder.et_qty.getText().toString())};
        holder.btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty[0] < product.deliveryQty) {
                    holder.et_qty.setText(String.valueOf(qty[0] + 1));
                    qty[0] = qty[0] + 1;
                } else {
                    holder.et_qty.setText("1");
                    qty[0] = 1;
                }
            }
        });
        String date_n = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        holder.btn_dob.setText(date_n);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                holder.btn_dob.setText(sdf.format(myCalendar.getTime()));

            }
        };

        holder.btn_cng_date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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


        holder.sp_return_cause.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReasonResponse.Result result = (ReasonResponse.Result) parent.getItemAtPosition(position);
                reasonID = result.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        databaseHandler = new DatabaseHandler(context);
        List<ReasonResponse.Result> resultList = databaseHandler.getSellsResponseReason();
        holder.sp_return_cause.setAdapter(new ReasonTypeAdapter(context ,resultList));
        holder.btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SellsReturnResponsePost.SellsReturn sellsReturn = new SellsReturnResponsePost.SellsReturn();
                sellsReturn.outletID = deliveryList.outletId;
                sellsReturn.orderID = deliveryList.deliveryId;
                sellsReturn.productID = product.productId;
                sellsReturn.mReturnDate = holder.btn_dob.getText().toString();
                sellsReturn.mVariantRow = product.variantRow;
                sellsReturn.mNote = holder.note.getText().toString();
                //sellsReturn.mReasonId = holder.sp_return_cause.getSelectedItemPosition()+1;
                sellsReturn.mReasonId = reasonID;
                sellsReturn.mSku = "";
                sellsReturn.quantity = Integer.parseInt(holder.et_qty.getText().toString());



                //Sells return api request
                SessionManager sessionManager = new SessionManager(context);
                SelliscopeApiEndpointInterface apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                        sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
                Call<SellsReturnResponsePost> call = apiService.returnProduct(sellsReturn);
                call.enqueue(new Callback<SellsReturnResponsePost>() {
                    @Override
                    public void onResponse(Call<SellsReturnResponsePost> call, Response<SellsReturnResponsePost> response) {
                        if (response.code() == 200) {
                            holder.btn_return.setEnabled(false);
                            holder.btn_return.setBackgroundColor(Color.parseColor("#dddddd"));
                            Toast.makeText(context, "Product returned successfully", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            Toast.makeText(context, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, response.code()+" Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SellsReturnResponsePost> call, Throwable t) {
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
        private TextView btn_dob,note;
        private ImageButton btn_cng_date_of_birth;


        public SellsReturnDetailsViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            btn_decrease = (Button) itemView.findViewById(R.id.btn_decrease);
            btn_increase = (Button) itemView.findViewById(R.id.btn_increase);
            btn_return = (Button) itemView.findViewById(R.id.btn_return);
            sp_return_cause = (Spinner) itemView.findViewById(R.id.sp_return_cause);
            et_qty = (EditText) itemView.findViewById(R.id.et_qty);
            btn_dob = (TextView) itemView.findViewById(R.id.btn_dob);
            note = (TextView) itemView.findViewById(R.id.note);
            btn_cng_date_of_birth = (ImageButton) itemView.findViewById(R.id.btn_cng_date_of_birth);

        }

        public ViewDataBinding getBinding() {
            return binding;
        }

      /*  void getOutletTypes() {
            List<ReasonResponse.Result> resultList = databaseHandler.getSellsResponseReason();
            reasonTypeAdapter = new ReasonTypeAdapter(context, resultList);
            sp_return_cause.setAdapter(reasonTypeAdapter);
            //For selecting previous outlet type
            int position = 0;
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getName().equals(result.getName()))
                    position = i;
            }
            sp_return_cause.setSelection(position);
            //For selecting previous outlet type
        }*/
    }
}
