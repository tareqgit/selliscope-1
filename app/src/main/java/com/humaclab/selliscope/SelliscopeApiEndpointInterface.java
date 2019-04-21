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

    @GET("delivery/order")
    Call<SalesReturnResponse> getSalesReturnDAta();

    @GET("sales-return/reason")
    Call<ReasonResponse> getSalesReturnReasony();

    @GET("sales-return")
    Call<SalesReturnHistory> getSalesReturnHistory();

    @GET("product/price-variation")
    Call<PriceVariationResponse> getPriceVariation();

    @GET("product/trade-promotion")
    Call<TradePromotion> getTradePromotion();

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

    @GET("payment/{outlet_id}")
    Call<Payment> getPayment(@Path("outlet_id") int outletId);


    //Outlet Wise Target
    @GET("target/outlet/{outlet_id}")
    Call<OutletTarget> getPutletTarget(@Path("outlet_id") int outletId);

    @GET("payment")
    Call<Payment> getPayment();

    @GET("delivery")
    Call<DeliveryResponse> getDelivery();
    //for old sellisreturn Mode
    @GET("return-products")
    Call<DeliveryResponseOld> getSalesReturnOld(@Query("outlet_id") int outletID);

    @GET("return-products")
    Call<DeliveryResponse> getSalesReturn();

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

    @GET("target/user")
    Call<OutletTarget> getTarget();

    @GET("diameter")
    Call<DiameterResponse> getDiameter();

    @GET("outlet/{outlet_id}/purchase-history")
    Call<PurchaseHistoryResponse> getPurchaseHistory(@Path("outlet_id") int outletID);

    @GET("route-plan")
    Call<RouteResponse> getRoutes();

    @GET("route-plan/{route_id}")
    Call<RouteDetailsResponse> getRouteDetails(@Path("route_id") int routeId);

    @GET("app-version")
    Call<AppVersion> getAppsversion();
    //POST methods

    @POST("login")
    Call<ResponseBody> getUser();

    @POST("outlet/store")
    Call<ResponseBody> createOutlet(@Body CreateOutlet createOutlet);

    @POST("visit/store")
    Call<ResponseBody> sendUserLocation(@Body UserLocation userLocation);

    @POST("order/variant/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);
    /*@POST("order/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);*/

    @POST("payment/collect")
    Call<PaymentResponse.PaymentSucessfull> payNow(@Body PaymentResponse payment);

    @POST("delivery/store")
    Call<DeliverProductResponse> deliverProduct(@Body DeliverProductResponse deliverProduct);

/*    @POST("return-product")
    Call<SellsReturnResponsePost> returnProduct(@Body SellsReturnResponsePost.SellsReturn returnProduct);*/
    @POST("sales-return/store ")
    Call<SellsReturnResponsePost> returnProduct(@Body SellsReturnResponsePost.SellsReturn returnProduct);
    //for ols sellisreturn Model
    @POST("return-product")
    Call<SellsReturnResponseOld> returnProductOld(@Body SellsReturnResponseOld.SellsReturn returnProduct);
    @POST("inspection/store")
    Call<InspectionResponse> inspectOutlet(@Body InspectionResponse.Inspection inspection);

    @POST("version-imei")
    Call<ResponseBody> sendIMEIAndVersion(@Body IMEIandVerison imeIandVerison);

    @POST("change-password")
    Call<ChangePasswordResponse> changePassword(@Body ChangePassword changePassword);

    @PUT("outlet/{id}/update")
    Call<ResponseBody> updateOutlet(@Path("id") int outletID, @Body CreateOutlet createOutlet);

    @POST("profile/update")
    Call<UpdateProfileResponse> updateProfile(@Body UpdateProfile updateProfile);

    @GET("performance/order")
    Call<PerformanceOrderResponse> performanceOrder(@Query("date_from") String date_from, @Query("date_to") String date_to);

    @GET("performance/payment")
    Call<PaymentsResponse> performancePayments(@Query("date_from") String date_from, @Query("date_to") String date_to);

}