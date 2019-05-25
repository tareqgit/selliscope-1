package com.humaclab.selliscope_myone;

import com.humaclab.selliscope_myone.model.AddNewOrder;
import com.humaclab.selliscope_myone.model.BrandResponse;
import com.humaclab.selliscope_myone.model.CategoryResponse;
import com.humaclab.selliscope_myone.model.CreateOutlet;
import com.humaclab.selliscope_myone.model.DeliverProductResponse;
import com.humaclab.selliscope_myone.model.DeliveryResponse;
import com.humaclab.selliscope_myone.model.diameter.DiameterResponse;
import com.humaclab.selliscope_myone.model.district.DistrictResponse;
import com.humaclab.selliscope_myone.model.GodownRespons;
import com.humaclab.selliscope_myone.model.IMEIandVerison;
import com.humaclab.selliscope_myone.model.InspectionResponse;
import com.humaclab.selliscope_myone.model.LoginResponse;
import com.humaclab.selliscope_myone.model.OrderResponse;
import com.humaclab.selliscope_myone.model.outletType.OutletTypeResponse;
import com.humaclab.selliscope_myone.model.Payment;
import com.humaclab.selliscope_myone.model.PaymentResponse;
import com.humaclab.selliscope_myone.model.ProductResponse;
import com.humaclab.selliscope_myone.model.SellsReturnResponse;
import com.humaclab.selliscope_myone.model.thana.ThanaResponse;
import com.humaclab.selliscope_myone.model.UserLocation;
import com.humaclab.selliscope_myone.outlet_paging.api.response_model.OutletSearchResponse;
import com.humaclab.selliscope_myone.model.promotion.PromotionQuantityResponse;
import com.humaclab.selliscope_myone.model.promotion.PromotionValueResponse;
import com.humaclab.selliscope_myone.product_paging.api.response_model.ProductSearchResponse;
import com.humaclab.selliscope_myone.utils.StockResponse;

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

    @GET("product/promotion")
    Call<PromotionQuantityResponse> getPromotionQuantity();

    @GET("product/promotion/value")
    Call<PromotionValueResponse> getPromotionValue();

    /*@GET("variant-product")
    Call<VariantProductResponse> getProducts();*/
/*    @GET("product/stock/{productID}")
    Call<StockResponse> getProductStock(@Path("productID") String productID);*/

    @GET("product/stocks")
    Call<StockResponse> getProductStock(@Query("product") String productID);

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


    @POST("version-imei")
    Call<ResponseBody> sendIMEIAndVersion(@Body IMEIandVerison imeIandVerison);


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

    @PUT("outlet/{id}/update")
    Call<ResponseBody> updateOutlet(@Path("id") String outletID, @Body CreateOutlet createOutlet);


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



}