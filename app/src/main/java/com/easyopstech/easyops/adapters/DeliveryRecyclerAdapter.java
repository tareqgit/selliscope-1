package com.easyopstech.easyops.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.easyopstech.easyops.R;
import com.easyopstech.easyops.databinding.DeliveryDetailsItemBinding;
import com.easyopstech.easyops.model.DeliveryResponse;
import com.easyopstech.easyops.model.GodownRespons;
import com.easyopstech.easyops.utils.ImportentFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonmoy on 5/18/17.
 */

public class DeliveryRecyclerAdapter extends RecyclerView.Adapter<DeliveryRecyclerAdapter.DeliveryDetailsViewHolder> {
    private Context context;
    private List<DeliveryResponse.Product> products;
    private DeliveryResponse.DeliveryList deliveryList;
    private List<GodownRespons.Godown> godownList;
    private List<Integer> godownId = new ArrayList<>();
    private List<String> godownName = new ArrayList<>();

    private ProgressDialog pd;

    public DeliveryRecyclerAdapter(Context context, DeliveryResponse.DeliveryList deliveryList, List<GodownRespons.Godown> godownList) {
        this.context = context;
        this.deliveryList = deliveryList;
        this.products = this.deliveryList.productList;
        this.godownList = godownList;
    }

    @Override
    public DeliveryDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_details_item, parent, false);
        return new DeliveryDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeliveryDetailsViewHolder holder, int position) {
        final DeliveryResponse.Product product = products.get(position);
        holder.getBinding().setProduct(product);
        holder.getBinding().executePendingBindings();

        pd = new ProgressDialog(context);
        pd.setMessage("Delivering your product....");
        pd.setCancelable(false);

        //For godwon spinner
        godownId.clear();
        godownId.add(0);
        godownName.clear();
        godownName.add("Select Godown");
        for (GodownRespons.Godown godown : godownList) {
            godownId.add(godown.getId());
            godownName.add(godown.getName());
        }
        /*holder.sp_godown.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_item, godownName));
        holder.sp_godown.setSelection(0);*/
        //For godwon spinner

        //For qty increase and decrease
        final int[] qty = {Integer.parseInt(holder.et_qty.getText().toString())};
        holder.btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty[0] < product.qty) {
                    holder.et_qty.setText(String.valueOf(qty[0] + 1));
                    ImportentFunction.deliveryArrayList.set(holder.getAdapterPosition(),Integer.parseInt(String.valueOf(qty[0] + 1)));
                    qty[0] = qty[0] + 1;
                } else {
                    holder.et_qty.setText("1");
                    ImportentFunction.deliveryArrayList.set(holder.getAdapterPosition(),Integer.parseInt(String.valueOf("1")));
                    qty[0] = 1;
                }

            }
        });
        holder.btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty[0] > 1) {
                    holder.et_qty.setText(String.valueOf(qty[0] - 1));
                    ImportentFunction.deliveryArrayList.set(holder.getAdapterPosition(),Integer.parseInt(String.valueOf(qty[0] - 1)));
                    qty[0] = qty[0] - 1;
                } else {
                    holder.et_qty.setText("0");
                    ImportentFunction.deliveryArrayList.set(holder.getAdapterPosition(),Integer.parseInt(String.valueOf("0")));
                    qty[0] = 0;
                }

            }
        });
        //For qty increase and decrease

        holder.et_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(""))
                {
                ImportentFunction.deliveryArrayList.set(holder.getAdapterPosition(),Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        /*holder.btn_deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.sp_godown.getSelectedItemPosition() != 0) {

                    pd.show();
                    DeliverProductResponse deliverProductResponse = new DeliverProductResponse();
                    DeliverProductResponse.Order order = new DeliverProductResponse.Order();
                    DeliverProductResponse.Order.Product product1 = new DeliverProductResponse.Order.Product();

                    order.products = new ArrayList<>();
                    product1.productId = product.productId;
                    product1.variantRow = product.variantRow;
                    product1.qty = Integer.parseInt(holder.et_qty.getText().toString());
                    product1.godownId = godownId.get(holder.sp_godown.getSelectedItemPosition());
                    order.products.add(product1);
                    order.orderId = deliveryList.deliveryId;
                    order.outletId = deliveryList.outletId;
                    deliverProductResponse.order = order;

                    //Deliver api request
                    if (Integer.parseInt(holder.et_qty.getText().toString()) != 0) {
                        SessionManager sessionManager = new SessionManager(context);
                        RootApiEndpointInterface apiService = RootApplication.getRetrofitInstance(sessionManager.getUserEmail(),
                                sessionManager.getUserPassword(), false).create(RootApiEndpointInterface.class);
                        Call<DeliverProductResponse> call = apiService.deliverProduct(deliverProductResponse);
                        System.out.println("Delivery : " + new Gson().toJson(deliverProductResponse));
                        call.enqueue(new Callback<DeliverProductResponse>() {
                            @Override
                            public void onResponse(Call<DeliverProductResponse> call, Response<DeliverProductResponse> response) {
                                pd.dismiss();
                                if (response.code() == 201) {
                                    holder.btn_deliver.setEnabled(false);
                                    holder.btn_deliver.setBackgroundColor(Color.parseColor("#dddddd"));
                                    Toast.makeText(context, "Product delivered successfully", Toast.LENGTH_SHORT).show();
                                    ((Activity)context).finish();
                                } else if (response.code() == 200) {
                                    Toast.makeText(context, response.body().result, Toast.LENGTH_LONG).show();
                                } else if (response.code() == 401) {
                                    Toast.makeText(context, "Invalid Response from server.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, response.code()+"Server Error! Try Again Later!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<DeliverProductResponse> call, Throwable t) {
                                pd.dismiss();
                                t.printStackTrace();
                            }
                        });
                    } else {
                        pd.dismiss();
                        Toast.makeText(context, "You cannot deliver 0 amount of product.", Toast.LENGTH_LONG).show();
                    }
                    //Deliver api request
                } else {
                    Toast.makeText(context, "Please select a godown.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        if(products!=null)
        return products.size();
        else
            return 0;
    }

    public class DeliveryDetailsViewHolder extends RecyclerView.ViewHolder {
        private DeliveryDetailsItemBinding binding;
        private Spinner sp_godown;
        private Button btn_decrease, btn_increase, btn_deliver;
        private EditText et_qty;

        public DeliveryDetailsViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
           /* sp_godown = itemView.findViewById(R.id.sp_godown);*/
            btn_decrease = itemView.findViewById(R.id.btn_decrease);
            btn_increase = itemView.findViewById(R.id.btn_increase);
           /* btn_deliver = itemView.findViewById(R.id.btn_deliver);*/
            et_qty = itemView.findViewById(R.id.et_qty);
        }

        public DeliveryDetailsItemBinding getBinding() {
            return binding;
        }
    }
}
