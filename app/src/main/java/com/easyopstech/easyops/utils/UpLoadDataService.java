package com.easyopstech.easyops.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.easyopstech.easyops.RootApiEndpointInterface;
import com.easyopstech.easyops.RootApplication;
import com.easyopstech.easyops.model.AddNewOrder;
import com.easyopstech.easyops.model.InspectionResponse;
import com.easyopstech.easyops.sales_return.db.ReturnProductDatabase;
import com.easyopstech.easyops.sales_return.db.ReturnProductEntity;
import com.easyopstech.easyops.sales_return.model.post.JsonMemberReturn;
import com.easyopstech.easyops.sales_return.model.post.SalesReturn2019Response;
import com.easyopstech.easyops.sales_return.model.post.SalesReturn2019SelectedProduct;
import com.easyopstech.easyops.sales_return.model.post.SalesReturnPostBody;
import com.easyopstech.easyops.utility_db.db.UtilityDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * Created by mtita on 18,August,2019.
 */
public class UpLoadDataService {
    private Context context;
    private DatabaseHandler databaseHandler;
    private RootApiEndpointInterface apiService;
    private ReturnProductDatabase returnProductDatabase;
    private UtilityDatabase mUtilityDatabase;

    public UpLoadDataService(Context context) {
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
        SessionManager sessionManager = new SessionManager(context);
        this.apiService = RootApplication.getRetrofitInstance(
                sessionManager.getUserEmail(),
                sessionManager.getUserPassword(),
                false)
                .create(RootApiEndpointInterface.class);
    }

    public void uploadOrder_and_ReturnData(UploadCompleteListener uploadCompleteListener) {



        if (NetworkUtility.isNetworkAvailable(context)) {

            try {
                if (databaseHandler.getOrder().size() != 0) {
                    Log.d("tareq_test", "Uploading order to server");
                    returnProductDatabase = (ReturnProductDatabase) ReturnProductDatabase.getInstance(context);

                    uploadOrderToServer(new UploadCompleteListener() {
                        @Override
                        public void uploadComplete() {
                            if (uploadCompleteListener != null)
                                uploadCompleteListener.uploadComplete();
                        }

                        @Override
                        public void uploadFailed(String reason) {
                            if (uploadCompleteListener != null)
                                uploadCompleteListener.uploadFailed(reason);
                        }
                    });
                } else {



                    if (uploadCompleteListener != null)
                        uploadCompleteListener.uploadFailed("No offline order is available.");


                }



            } catch (Exception e) {
                e.printStackTrace();
            }



        } else {

            if (uploadCompleteListener != null)
                uploadCompleteListener.uploadFailed("Network: Network disabled");
//            Toast.makeText(context, "Network disabled", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadInspectionData(UploadCompleteListener uploadCompleteListener){
        mUtilityDatabase= (UtilityDatabase) UtilityDatabase.getInstance(context);
        Executors.newSingleThreadExecutor().execute(()->{
            List<InspectionResponse.Inspection> inspectionList = mUtilityDatabase.returnInspectionDao().getInspections();

            if(!inspectionList.isEmpty()){
                if(NetworkUtility.isNetworkAvailable(context)) {
                    for (InspectionResponse.Inspection inspection : inspectionList  ) {


                        Call<InspectionResponse> call = apiService.inspectOutlet(inspection);
                        call.enqueue(new Callback<InspectionResponse>() {
                            @Override
                            public void onResponse(Call<InspectionResponse> call, Response<InspectionResponse> response) {

                                if (response.code() == 201) {

                                    Executors.newSingleThreadExecutor().execute(() -> mUtilityDatabase.returnInspectionDao().deleteInspection(inspection));

                                }
                            }

                            @Override
                            public void onFailure(Call<InspectionResponse> call, Throwable t) {

                                t.printStackTrace();
                            }
                        });
                    }
                    uploadCompleteListener.uploadComplete(); //as for loop ends
                }else{
                    uploadCompleteListener.uploadFailed("Internet connection not available");
                }
            }else{
                uploadCompleteListener.uploadFailed("No Inspection on local DB");
            }
        });
    }



    private void uploadOrderToServer(UploadCompleteListener uploadCompleteListener) {
        List<AddNewOrder> addNewOrderList = databaseHandler.getOrder();

        for (final AddNewOrder addNewOrder : addNewOrderList) {
            Log.d("tareq_test", "offline" + new Gson().toJson(addNewOrder));

            Call<AddNewOrder.OrderResponse> call = apiService.addOrder(addNewOrder);
            call.enqueue(new Callback<AddNewOrder.OrderResponse>() {
                @Override
                public void onResponse(Call<AddNewOrder.OrderResponse> call, Response<AddNewOrder.OrderResponse> response) {
                    Log.d("tareq_test", "Offline order: Order completed." + response.code());
                    if (response.code() == 201) {


                        new Thread(() -> {
                            if (returnProductDatabase.returnProductDao().getAllReturnProduct(addNewOrder.newOrder.products.get(0).order_return_id).size() != 0) {
                                Log.d("tareq_test", "Sales Return Item found with " + addNewOrder.newOrder.products.get(0).order_return_id + " is " + returnProductDatabase.returnProductDao().getAllReturnProduct(addNewOrder.newOrder.products.get(0).order_return_id).size());


                                postSalesRetunModule(response);

                            } else {
                                Log.d("tareq_test", "No Return Product found");
                                if (uploadCompleteListener != null)
                                    uploadCompleteListener.uploadComplete();
                            }
                        }).start();

                        databaseHandler.removeOrder(addNewOrder.newOrder.outletId);
                        Log.d("tareq_test", "Offline order: Order removed.");
                    } else {
                        if (uploadCompleteListener != null)
                            uploadCompleteListener.uploadFailed("Order Failed Bad response:" + response.code());
                    }
                }

                private void postSalesRetunModule(Response<AddNewOrder.OrderResponse> response) {
                    if (response.body() != null) {
                        postSalesReturn(apiService, response.body().result.order.id, response.body().result.order.outlet_id, addNewOrder.newOrder.products.get(0).order_return_id, new UploadCompleteListener() {
                            @Override
                            public void uploadComplete() {
                                if (uploadCompleteListener != null)
                                    uploadCompleteListener.uploadComplete();


                            }

                            @Override
                            public void uploadFailed(String reason) {
                                if (uploadCompleteListener != null)
                                    uploadCompleteListener.uploadFailed(reason + "trying again sales return");

                                postSalesRetunModule(response);

                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<AddNewOrder.OrderResponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("tareq_test", "failed to send order update call");
                    if (uploadCompleteListener != null)
                        uploadCompleteListener.uploadFailed("failed to send order update call: " + t.getMessage());
                }
            });
        }
    }


    private void postSalesReturn(RootApiEndpointInterface apiService, int orderId, int outletId, int order_return_id, UploadCompleteListener uploadCompleteListener) {
        List<SalesReturn2019SelectedProduct> salesReturn2019SelectedProductList = new ArrayList<>();
        for (ReturnProductEntity salesreturnProduct : returnProductDatabase.returnProductDao().getAllReturnProduct(order_return_id)) {
            salesReturn2019SelectedProductList.add(new SalesReturn2019SelectedProduct.Builder()
                    .withProductId(salesreturnProduct.product_id)
                    .withProductQty((double) salesreturnProduct.quantity)
                    .withProductRate(salesreturnProduct.rate)
                    .withReturn_date(salesreturnProduct.return_date)
                    .withReason(salesreturnProduct.cause)
                    .withProductSKU(salesreturnProduct.sku)
                    .withProductVariantRow(salesreturnProduct.variant_row)
                    .withGodown_id(salesreturnProduct.godown_id)
                    .build()
            );
        }

        JsonMemberReturn jsonMemberReturn = new JsonMemberReturn();
        jsonMemberReturn.setOrderId(orderId);
        jsonMemberReturn.setOutletId(outletId);
        jsonMemberReturn.setProducts(salesReturn2019SelectedProductList);

        SalesReturnPostBody salesReturnPostBody = new SalesReturnPostBody();
        salesReturnPostBody.setJsonMemberReturn(jsonMemberReturn);

        Log.d("tareq_test", "Sales Return body: " + new Gson().toJson(salesReturnPostBody));
        apiService.postSalesReturn(salesReturnPostBody).enqueue(new Callback<SalesReturn2019Response>() {
            @Override
            public void onResponse(Call<SalesReturn2019Response> call, Response<SalesReturn2019Response> response) {

                if (response.isSuccessful()) {
                    Log.d("tareq_test", "Salesreturn successfull");

                    new Thread(() -> {
                        returnProductDatabase.returnProductDao().deleteAllReturnProduct(order_return_id);
                        if (uploadCompleteListener != null) uploadCompleteListener.uploadComplete();
                    }).start();


                } else {
                    Log.d("tareq_test", "Salesreturn failed" + response.code());
                    if (uploadCompleteListener != null)
                        uploadCompleteListener.uploadFailed("Salesreturn failed Bad Responses" + response.code());
                }
            }

            @Override
            public void onFailure(Call<SalesReturn2019Response> call, Throwable t) {
                Log.e("tareq_test", "PostSalesReturn api not working|: " + t.getMessage());
                if (uploadCompleteListener != null)
                    uploadCompleteListener.uploadFailed("PostSalesReturn api not working|: " + t.getMessage());

            }
        });
    }

    public interface UploadCompleteListener {

        void uploadComplete();

        void uploadFailed(String reason);
    }
}
