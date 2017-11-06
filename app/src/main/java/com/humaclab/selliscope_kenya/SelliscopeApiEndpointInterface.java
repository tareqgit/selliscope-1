package com.humaclab.selliscope_kenya;

import com.humaclab.selliscope_kenya.model.AddNewOrder;
import com.humaclab.selliscope_kenya.model.BrandResponse;
import com.humaclab.selliscope_kenya.model.CategoryResponse;
import com.humaclab.selliscope_kenya.model.CreateOutlet;
import com.humaclab.selliscope_kenya.model.DeliverProductResponse;
import com.humaclab.selliscope_kenya.model.DeliveryResponse;
import com.humaclab.selliscope_kenya.model.GodownRespons;
import com.humaclab.selliscope_kenya.model.InspectionResponse;
import com.humaclab.selliscope_kenya.model.OrderResponse;
import com.humaclab.selliscope_kenya.model.Payment;
import com.humaclab.selliscope_kenya.model.PaymentResponse;
import com.humaclab.selliscope_kenya.model.SellsReturnResponse;
import com.humaclab.selliscope_kenya.model.UserLocation;
import com.humaclab.selliscope_kenya.model.VariantProduct.VariantProductResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Leon on 9/3/2017.
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

    /*@GET("product")
    Call<ProductResponse> getProducts();*/
    @GET("variant-product")
    Call<VariantProductResponse> getProducts();

    @GET("order")
    Call<OrderResponse> getOrders();

    @GET("payment")
    Call<Payment> getPayment();

    @GET("delivery")
    Call<DeliveryResponse> getDelivery();

    @GET("delivery")
    Call<DeliveryResponse> getSalesReturn(@Query("outlet_id") int outletID);

    @GET("godown")
    Call<GodownRespons> getGodown();

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

    @POST("outlet/store")
    Call<ResponseBody> createOutlet(@Body CreateOutlet createOutlet);

    @PUT("outlet/{id}/update")
    Call<ResponseBody> updateOutlet(@Path("id") int outletID, @Body CreateOutlet createOutlet);

    @POST("order/variant/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);
    /*@POST("order/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);*/

    @POST("payment/collect")
    Call<PaymentResponse.PaymentSucessfull> payNow(@Body PaymentResponse payment);

    @POST("delivery/store")
    Call<DeliverProductResponse> deliverProduct(@Body DeliverProductResponse deliverProduct);

    @POST("return-product")
    Call<SellsReturnResponse> returnProduct(@Body SellsReturnResponse.SellsReturn returnProduct);

    @POST("inspection/store")
    Call<InspectionResponse> inspectOutlet(@Body InspectionResponse.Inspection inspection);
}