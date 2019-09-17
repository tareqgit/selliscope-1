package com.humaclab.lalteer;

import com.humaclab.lalteer.model.AddNewOrder;
import com.humaclab.lalteer.model.AppVersion.AppVersion;
import com.humaclab.lalteer.model.BrandResponse;
import com.humaclab.lalteer.model.CategoryResponse;
import com.humaclab.lalteer.model.CreateOutlet;
import com.humaclab.lalteer.model.DeliverProductResponse;
import com.humaclab.lalteer.model.DeliveryResponse;
import com.humaclab.lalteer.model.Diameter.DiameterResponse;
import com.humaclab.lalteer.model.District.DistrictResponse;
import com.humaclab.lalteer.model.GodownRespons;
import com.humaclab.lalteer.model.IMEIandVerison;
import com.humaclab.lalteer.model.InspectionResponse;
import com.humaclab.lalteer.model.OrderResponse;
import com.humaclab.lalteer.model.OutletType.OutletTypeResponse;
import com.humaclab.lalteer.model.Payment;
import com.humaclab.lalteer.model.PaymentResponse;
import com.humaclab.lalteer.model.Products.ProductResponse;
import com.humaclab.lalteer.model.PromotionalAds.PromotionalAds;
import com.humaclab.lalteer.model.advance_payment.AdvancePaymentPostResponse;
import com.humaclab.lalteer.model.advance_payment.AdvancePaymentsItem;
import com.humaclab.lalteer.model.advance_payment.AdvancePaymentsSendItem;
import com.humaclab.lalteer.model.advance_payment.AdvancedPaymentResponse;
import com.humaclab.lalteer.model.checked_in_dealer.CheckedInDealerResponse;
import com.humaclab.lalteer.model.purchase_history.PurchaseHistoryResponse;
import com.humaclab.lalteer.model.RoutePlan.RouteDetailsResponse;
import com.humaclab.lalteer.model.RoutePlan.RouteResponse;
import com.humaclab.lalteer.model.SellsReturnResponse;
import com.humaclab.lalteer.model.Target.OutletTarget;
import com.humaclab.lalteer.model.Thana.ThanaResponse;
import com.humaclab.lalteer.model.UpdatePassword.ChangePassword;
import com.humaclab.lalteer.model.UpdatePassword.ChangePasswordResponse;
import com.humaclab.lalteer.model.UpdateProfile.UpdateProfile;
import com.humaclab.lalteer.model.UpdateProfile.UpdateProfileResponse;
import com.humaclab.lalteer.model.UserLocation;
import com.humaclab.lalteer.model.performance.orders_model.PerformanceOrderResponse;
import com.humaclab.lalteer.model.performance.paymentsModel.PaymentsResponse;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Leon on 9/3/2017.
 */

public interface SelliscopeApiEndpointInterface {
    //GET methods

    @GET("dealers")
    Single<Response<ResponseBody>> getOutlets();

    @GET("category")
    Single<Response<CategoryResponse>> getCategories();

    @GET("brands")
    Single<Response<BrandResponse>> getBrands();

    @GET("products")
    Single<Response<ProductResponse>> getProducts();

    /*@GET("variant-product")
    Call<VariantProductResponse> getProducts();*/

    @GET("order")
    Single<Response<OrderResponse>> getOrders();

    @GET("outlet-payment-order/{outlet_id}")
    Single<Response<Payment>> getPayment(@Path("outlet_id") int outletId);

    @GET("payment")
    Single<Response<Payment>> getPayment();

    @GET("order/delivery")
    Single<Response<DeliveryResponse>> getDelivery();

    @GET("delivery")
    Single<Response<DeliveryResponse>> getSalesReturn(@Query("outlet_id") int outletID);

    @GET("godown")
    Call<GodownRespons> getGodown();

     @GET("district")
    Single<Response<DistrictResponse>> getDistricts();

     @GET("thana")
    Single<Response<ThanaResponse>> getThanas();

    @GET("dealers-type")
    Single<Response<OutletTypeResponse>> getOutletTypes();

  /*  @GET("visit")
    Call<ResponseBody> getVisits();*/

    @GET("target")
    Single<Response<ResponseBody>> getTargets();

    @GET("diameter")
    Single<Response<DiameterResponse>> getDiameter();

    @GET("dealers/{outlet_id}/purchase-history")
    Single<Response<PurchaseHistoryResponse>> getPurchaseHistory(@Path("outlet_id") int outletID);

    @GET("target/user")
    Single<Response<OutletTarget>> getTarget();

     @GET("route-plan")
    Single<Response<RouteResponse>> getRoutes();

     @GET("route-plan/{route_id}")
    Single<Response<RouteDetailsResponse>> getRouteDetails(@Path("route_id") int routeId);
    //POST methods

    @GET("login")
    Single<Response<ResponseBody>> getUser();

    @GET("app-version")
    Single<Response<AppVersion>> getAppsversion();

    //Outlet Wise Target
    @GET("target/dealer/{outlet_id}")
    Single<Response<OutletTarget>> getOutletTarget(@Path("outlet_id") int outletId);

    @GET("banner")
    Single<Response<PromotionalAds>> getPromotionalAds();


    @POST("dealers/create/")
    Single<Response<ResponseBody>> createOutlet(@Body CreateOutlet createOutlet);


    @GET("check-in-dealer")
    Single<Response<CheckedInDealerResponse>> getCheckedInDealers();


     @POST("visit/store/")
    Call<ResponseBody> sendUserLocation(@Body UserLocation userLocation);

    @POST("order/store/")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);
    /*@POST("order/store")
    Single<Response<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);*/

    @POST("payment/collect/")
   Call<PaymentResponse.PaymentSucessfull> payNow(@Body PaymentResponse payment);

    @POST("order/delivery/store/")
   Call<DeliverProductResponse> deliverProduct(@Body DeliverProductResponse deliverProduct);

    @POST("return-product")
    Call<SellsReturnResponse> returnProduct(@Body SellsReturnResponse.SellsReturn returnProduct);

    @POST("inspection/store/")
    Call<InspectionResponse> inspectOutlet(@Body InspectionResponse.Inspection inspection);

    @POST("version-imei/")
    Single<Response<ResponseBody>> sendIMEIAndVersion(@Body IMEIandVerison imeIandVerison);

    @POST("change-password")
   Call<ChangePasswordResponse> changePassword(@Body ChangePassword changePassword);

    @PUT("dealers/{id}/edit/")
    Call<ResponseBody> updateOutlet(@Path("id") int outletID, @Body CreateOutlet createOutlet);

    @POST("profile/update")
    Call<UpdateProfileResponse> updateProfile(@Body UpdateProfile updateProfile);

    @GET("performance/order")
    Call<PerformanceOrderResponse> performanceOrder(@Query("date_from") String date_from, @Query("date_to") String date_to);

    @GET("performance/payment")
    Call<PaymentsResponse> performancePayments(@Query("date_from") String date_from, @Query("date_to") String date_to);

    @GET("getAdvance/{outlet_id}")
  Single<Response<AdvancedPaymentResponse>> getAdvancePayments(@Path("outlet_id") int outletId);

    @POST("getAdvance/{outlet_id}")
    Call<AdvancePaymentPostResponse> postAdvancePayment(@Path("outlet_id") int outletId,@Body AdvancePaymentsSendItem updateProfile);
}