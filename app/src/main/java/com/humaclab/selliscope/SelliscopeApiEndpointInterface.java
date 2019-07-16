package com.humaclab.selliscope;

import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.model.app_version.AppVersion;
import com.humaclab.selliscope.model.BrandResponse;
import com.humaclab.selliscope.model.CategoryResponse;
import com.humaclab.selliscope.model.CreateOutlet;
import com.humaclab.selliscope.model.DeliverProductResponse;
import com.humaclab.selliscope.model.DeliveryResponse;
import com.humaclab.selliscope.model.model_sales_return_old.DeliveryResponseOld;
import com.humaclab.selliscope.model.diameter.DiameterResponse;
import com.humaclab.selliscope.model.district.DistrictResponse;
import com.humaclab.selliscope.model.GodownRespons;
import com.humaclab.selliscope.model.IMEIandVerison;
import com.humaclab.selliscope.model.InspectionResponse;
import com.humaclab.selliscope.model.OrderResponse;
import com.humaclab.selliscope.model.outlet_type.OutletTypeResponse;
import com.humaclab.selliscope.model.Payment;
import com.humaclab.selliscope.model.PaymentResponse;
import com.humaclab.selliscope.model.performance.orders_model.PerformanceOrderResponse;
import com.humaclab.selliscope.model.purchase_history.PurchaseHistoryResponse;
import com.humaclab.selliscope.model.reason.ReasonResponse;
import com.humaclab.selliscope.model.route_plan.RouteDetailsResponse;
import com.humaclab.selliscope.model.route_plan.RouteResponse;
import com.humaclab.selliscope.model.sales_return.SalesReturnHistory;
import com.humaclab.selliscope.model.sales_return.SalesReturnResponse;
import com.humaclab.selliscope.model.sales_return.SellsReturnResponsePost;
import com.humaclab.selliscope.model.model_sales_return_old.SellsReturnResponseOld;
import com.humaclab.selliscope.model.target.OutletTarget;
import com.humaclab.selliscope.model.thana.ThanaResponse;
import com.humaclab.selliscope.model.trade_promotion.TradePromotion;
import com.humaclab.selliscope.model.update_password.ChangePassword;
import com.humaclab.selliscope.model.update_password.ChangePasswordResponse;
import com.humaclab.selliscope.model.update_profile.UpdateProfile;
import com.humaclab.selliscope.model.update_profile.UpdateProfileResponse;
import com.humaclab.selliscope.model.UserLocation;
import com.humaclab.selliscope.model.price_variation.PriceVariationResponse;
import com.humaclab.selliscope.model.variant_product.VariantProductResponse;
import com.humaclab.selliscope.model.performance.payments_model.PaymentsResponse;
import com.humaclab.selliscope.sales_return.model.get.SalesReturnGetResponse;
import com.humaclab.selliscope.sales_return.model.post.SalesReturn2019Response;
import com.humaclab.selliscope.sales_return.model.post.SalesReturnPostBody;

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

    @GET("v1/delivery/order")
    Call<SalesReturnResponse> getSalesReturnDAta();

    @GET("v1/sales-return/reason")
    Call<ReasonResponse> getSalesReturnReasony();

    @GET("v1/sales-return")
    Call<SalesReturnHistory> getSalesReturnHistory();

    @GET("v1/product/price-variation")
    Call<PriceVariationResponse> getPriceVariation();

    @GET("v1/product/trade-promotion")
    Call<TradePromotion> getTradePromotion();

    @GET("v1/outlet")
    Call<ResponseBody> getOutlets();

    @GET("v1/product/category")
    Call<CategoryResponse> getCategories();

    @GET("v1/product/brand")
    Call<BrandResponse> getBrands();

    /*@GET("product")
    Call<ProductResponse> getProducts();*/
    @GET("v1/variant-product")
    Call<VariantProductResponse> getProducts();

    @GET("v1/order")
    Call<OrderResponse> getOrders();

    @GET("v1/payment/{outlet_id}")
    Call<Payment> getPayment(@Path("outlet_id") int outletId);


    //Outlet Wise Target
    @GET("v1/target/outlet/{outlet_id}")
    Call<OutletTarget> getPutletTarget(@Path("outlet_id") int outletId);

    @GET("v1/payment")
    Call<Payment> getPayment();

    @GET("v1/delivery")
    Call<DeliveryResponse> getDelivery();
    //for old sellisreturn Mode
    @GET("v1/return-products")
    Call<DeliveryResponseOld> getSalesReturnOld(@Query("outlet_id") int outletID);

    @GET("v1/return-products")
    Call<DeliveryResponse> getSalesReturn();

    @GET("v1/godown")
    Call<GodownRespons> getGodown();

    @GET("v1/district")
    Call<DistrictResponse> getDistricts();

    @GET("v1/thana")
    Call<ThanaResponse> getThanas();

    @GET("v1/outlet/type")
    Call<OutletTypeResponse> getOutletTypes();

    @GET("v1/visit")
    Call<ResponseBody> getVisits();

    @GET("v1/target")
    Call<ResponseBody> getTargets();

    @GET("v1/target/user")
    Call<OutletTarget> getTarget();

    @GET("v1/diameter")
    Call<DiameterResponse> getDiameter();

    @GET("v1/outlet/{outlet_id}/purchase-history")
    Call<PurchaseHistoryResponse> getPurchaseHistory(@Path("outlet_id") int outletID);

    @GET("v1/route-plan")
    Call<RouteResponse> getRoutes();

    @GET("v1/route-plan/{route_id}")
    Call<RouteDetailsResponse> getRouteDetails(@Path("route_id") int routeId);

    @GET("v1/app-version")
    Call<AppVersion> getAppsversion();
    //POST methods

    @POST("v1/login")
    Call<ResponseBody> getUser();

    @POST("v1/outlet/store")
    Call<ResponseBody> createOutlet(@Body CreateOutlet createOutlet);

    @POST("v1/visit/store")
    Call<ResponseBody> sendUserLocation(@Body UserLocation userLocation);

    @POST("v1/order/variant/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);
    /*@POST("order/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);*/

    @POST("v1/payment/collect")
    Call<PaymentResponse.PaymentSucessfull> payNow(@Body PaymentResponse payment);

    @POST("v1/delivery/store")
    Call<DeliverProductResponse> deliverProduct(@Body DeliverProductResponse deliverProduct);

/*    @POST("return-product")
    Call<SellsReturnResponsePost> returnProduct(@Body SellsReturnResponsePost.SellsReturn returnProduct);*/
    @POST("v1/sales-return/store ")
    Call<SellsReturnResponsePost> returnProduct(@Body SellsReturnResponsePost.SellsReturn returnProduct);
    //for ols sellisreturn Model
    @POST("v1/return-product")
    Call<SellsReturnResponseOld> returnProductOld(@Body SellsReturnResponseOld.SellsReturn returnProduct);
    @POST("v1/inspection/store")
    Call<InspectionResponse> inspectOutlet(@Body InspectionResponse.Inspection inspection);

    @POST("v1/version-imei")
    Call<ResponseBody> sendIMEIAndVersion(@Body IMEIandVerison imeIandVerison);

    @POST("v1/change-password")
    Call<ChangePasswordResponse> changePassword(@Body ChangePassword changePassword);

    @PUT("v1/outlet/{id}/update")
    Call<ResponseBody> updateOutlet(@Path("id") int outletID, @Body CreateOutlet createOutlet);

    @POST("v1/profile/update")
    Call<UpdateProfileResponse> updateProfile(@Body UpdateProfile updateProfile);

    @GET("v1/performance/order")
    Call<PerformanceOrderResponse> performanceOrder(@Query("date_from") String date_from, @Query("date_to") String date_to);

    @GET("v1/performance/payment")
    Call<PaymentsResponse> performancePayments(@Query("date_from") String date_from, @Query("date_to") String date_to);


    @POST("v2/sales-return/store")
    Call<SalesReturn2019Response> postSalesReturn(@Body SalesReturnPostBody salesReturn);

    @GET("v2/sales-return/{outlet_id}")
    Call<SalesReturnGetResponse> getSalesReturn(@Path("outlet_id") int outletId);
}