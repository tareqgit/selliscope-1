package com.humaclab.selliscope_rangs;

import com.humaclab.selliscope_rangs.model.AddNewOrder;
import com.humaclab.selliscope_rangs.model.BrandResponse;
import com.humaclab.selliscope_rangs.model.CategoryResponse;
import com.humaclab.selliscope_rangs.model.CreateOutlet;
import com.humaclab.selliscope_rangs.model.DeliverProductResponse;
import com.humaclab.selliscope_rangs.model.DeliveryResponse;
import com.humaclab.selliscope_rangs.model.Diameter.DiameterResponse;
import com.humaclab.selliscope_rangs.model.District.DistrictResponse;
import com.humaclab.selliscope_rangs.model.GodownRespons;
import com.humaclab.selliscope_rangs.model.IMEIandVerison;
import com.humaclab.selliscope_rangs.model.InspectionResponse;
import com.humaclab.selliscope_rangs.model.LoginResponse;
import com.humaclab.selliscope_rangs.model.OrderResponse;
import com.humaclab.selliscope_rangs.model.OutletType.OutletTypeResponse;
import com.humaclab.selliscope_rangs.model.Payment;
import com.humaclab.selliscope_rangs.model.PaymentResponse;
import com.humaclab.selliscope_rangs.model.ProductResponse;
import com.humaclab.selliscope_rangs.model.SellsReturnResponse;
import com.humaclab.selliscope_rangs.model.Thana.ThanaResponse;
import com.humaclab.selliscope_rangs.model.UserLocation;
import com.humaclab.selliscope_rangs.utils.StockResponse;

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
    //GET methods

    @GET("outlet")
    Call<ResponseBody> getOutlets();

    @GET("product/category")
    Call<CategoryResponse> getCategories();

    @GET("product/brand")
    Call<BrandResponse> getBrands();

    @GET("product")
    Call<ProductResponse> getProducts();

    /*@GET("variant-product")
    Call<VariantProductResponse> getProducts();*/
    @GET("product/stock/{productID}")
    Call<StockResponse> getProductStock(@Path("productID") String productID);

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

    @GET("district")
    Call<DistrictResponse> getDistricts();

    @GET("thana")
    Call<ThanaResponse> getThanas();

    @GET("outlet/type")
    Call<OutletTypeResponse> getOutletTypes();

    @GET("visit")
    Call<ResponseBody> getVisits();

    @GET("target")
    Call<ResponseBody> getTargets();

    @GET("diameter")
    Call<DiameterResponse> getDiameter();

    //POST methods

    @POST("login")
    Call<LoginResponse> getUser(@Body LoginResponse.LoginInformation loginInformation);

    @POST("outlet/store")
    Call<ResponseBody> createOutlet(@Body CreateOutlet createOutlet);

    @POST("visit/store")
    Call<ResponseBody> sendUserLocation(@Body UserLocation userLocation);

    /*@POST("order/variant/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);*/
    @POST("order/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);

    @POST("payment/collect")
    Call<PaymentResponse.PaymentSucessfull> payNow(@Body PaymentResponse payment);

    @POST("delivery/store")
    Call<DeliverProductResponse> deliverProduct(@Body DeliverProductResponse deliverProduct);

    @POST("return-product")
    Call<SellsReturnResponse> returnProduct(@Body SellsReturnResponse.SellsReturn returnProduct);

    @POST("inspection/store")
    Call<InspectionResponse> inspectOutlet(@Body InspectionResponse.Inspection inspection);

    @POST("version-imei")
    Call<ResponseBody> sendIMEIAndVersion(@Body IMEIandVerison imeIandVerison);

    @PUT("outlet/{id}/update")
    Call<ResponseBody> updateOutlet(@Path("id") String outletID, @Body CreateOutlet createOutlet);
}