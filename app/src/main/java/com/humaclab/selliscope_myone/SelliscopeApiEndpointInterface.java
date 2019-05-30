package com.humaclab.selliscope_myone;

import com.humaclab.selliscope_myone.model.AddNewOrder;
import com.humaclab.selliscope_myone.model.diameter.DiameterResponse;
import com.humaclab.selliscope_myone.model.GodownRespons;
import com.humaclab.selliscope_myone.model.IMEIandVerison;
import com.humaclab.selliscope_myone.model.LoginResponse;
import com.humaclab.selliscope_myone.model.OrderResponse;
import com.humaclab.selliscope_myone.model.outletType.OutletTypeResponse;
import com.humaclab.selliscope_myone.model.Payment;
import com.humaclab.selliscope_myone.model.PaymentResponse;
import com.humaclab.selliscope_myone.model.ProductResponse;
import com.humaclab.selliscope_myone.model.UserLocation;
import com.humaclab.selliscope_myone.order_history.api.response_model.OrderHistoryResponse;
import com.humaclab.selliscope_myone.outlet_paging.api.response_model.OutletSearchResponse;

import com.humaclab.selliscope_myone.product_paging.api.response_model.ProductSearchResponse;
import com.humaclab.selliscope_myone.model.StockResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Leon on 9/3/2017.
 */

public interface SelliscopeApiEndpointInterface {
    //GET methods






    @GET("product/stocks")
    Call<StockResponse> getProductStock(@Query("product") String productID);


    @GET("payment")
    Call<Payment> getPayment();






    @GET("outlet/type")
    Call<OutletTypeResponse> getOutletTypes();


    @GET("target")
    Call<ResponseBody> getTargets();

    @GET("diameter")
    Call<DiameterResponse> getDiameter();

    //POST methods

    @POST("login")
    Call<LoginResponse> getUser(@Body LoginResponse.LoginInformation loginInformation);


    @POST("version-imei")
    Call<ResponseBody> sendIMEIAndVersion(@Body IMEIandVerison imeIandVerison);


    @POST("visit/store")
    Call<ResponseBody> sendUserLocation(@Body UserLocation userLocation);

    /*@POST("order/variant/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);*/
    @POST("order/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);

    @POST("payment/collect")
    Call<PaymentResponse.PaymentSucessfull> payNow(@Body PaymentResponse payment);



    //new For paging
    /**
     * Get outlet by search and paging
     */
    @GET("outlet")
    Call<OutletSearchResponse> searchOutlets(@Query("query") String query,
                                             @Query("page") int page)  ;

    /*
      Get outlet by search and paging
    */
    @GET("product")
    Call<ProductSearchResponse> searchProducts(@Query("query") String query,
                                                       @Query("page") int page)  ;



    @GET("outlet/{outletID}/purchase-history")
            Call<OrderHistoryResponse> getOrderHistory(@Path("outletID") String outletID, @Query("date_from") String date_from, @Query("date_to") String date_to, @Query("query") String query);

}