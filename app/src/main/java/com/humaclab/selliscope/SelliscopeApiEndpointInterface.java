package com.humaclab.selliscope;

import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.model.BrandResponse;
import com.humaclab.selliscope.model.CategoryResponse;
import com.humaclab.selliscope.model.CreateOutlet;
import com.humaclab.selliscope.model.OrderResponse;
import com.humaclab.selliscope.model.PaymentResponse;
import com.humaclab.selliscope.model.ProductResponse;
import com.humaclab.selliscope.model.UserLocation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Nahid on 3/5/2017.
 */
public interface SelliscopeApiEndpointInterface {
    @POST("login")
    Call<ResponseBody> getUser();

    @GET("outlet")
    Call<ResponseBody> getOutlets();

    @GET("product/category")
    Call<CategoryResponse> getCategories();

    @GET("product/brand")
    Call<BrandResponse> getBrands();

    @GET("product")
    Call<ProductResponse> getProducts();

    @GET("order")
    Call<OrderResponse> getOrders();

    @POST("visit/store")
    Call<ResponseBody> sendUserLocation(@Body UserLocation userLocation);

    @GET("district")
    Call<ResponseBody> getDistricts();

    @GET("thana")
    Call<ResponseBody> getThanas(@Query("district_id") int districtId);

    @GET("outlet/type")
    Call<ResponseBody> getOutletTypes();

    @GET("visit")
    Call<ResponseBody> getVisits();

    @GET("target")
    Call<ResponseBody> getTargets();

//    @POST("outlet/store")
    @POST("outlet/upload")
    Call<ResponseBody> createOutlet(@Body CreateOutlet createOutlet);

    @POST("outlet/{id}/update")
    Call<ResponseBody> updateOutlet(@Path("id") int outletID, @Body CreateOutlet createOutlet);

    @POST("order/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);
    @POST("payment/collect")
    Call<PaymentResponse.PaymentSucessfull> payNow(@Body PaymentResponse payment);
}