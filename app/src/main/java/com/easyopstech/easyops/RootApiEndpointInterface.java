package com.easyopstech.easyops;

import com.easyopstech.easyops.model.AddNewOrder;
import com.easyopstech.easyops.model.Outlets;
import com.easyopstech.easyops.model.app_version.AppVersion;
import com.easyopstech.easyops.model.BrandResponse;
import com.easyopstech.easyops.model.CategoryResponse;
import com.easyopstech.easyops.model.CreateOutlet;
import com.easyopstech.easyops.model.DeliverProductResponse;
import com.easyopstech.easyops.model.DeliveryResponse;
import com.easyopstech.easyops.model.model_sales_return_old.DeliveryResponseOld;
import com.easyopstech.easyops.model.diameter.DiameterResponse;
import com.easyopstech.easyops.model.district.DistrictResponse;
import com.easyopstech.easyops.model.GodownRespons;
import com.easyopstech.easyops.model.IMEIandVerison;
import com.easyopstech.easyops.model.InspectionResponse;
import com.easyopstech.easyops.model.OrderResponse;
import com.easyopstech.easyops.model.outlet_type.OutletTypeResponse;
import com.easyopstech.easyops.model.Payment;
import com.easyopstech.easyops.model.PaymentResponse;
import com.easyopstech.easyops.model.performance.orders_model.PerformanceOrderResponse;
import com.easyopstech.easyops.model.purchase_history.PurchaseHistoryResponse;
import com.easyopstech.easyops.model.reason.ReasonResponse;
import com.easyopstech.easyops.model.route_plan.RouteDetailsResponse;
import com.easyopstech.easyops.model.route_plan.RouteResponse;
import com.easyopstech.easyops.model.sales_return.SalesReturnHistory;
import com.easyopstech.easyops.model.sales_return.SalesReturnResponse;
import com.easyopstech.easyops.model.sales_return.SellsReturnResponsePost;
import com.easyopstech.easyops.model.model_sales_return_old.SellsReturnResponseOld;
import com.easyopstech.easyops.model.target.OutletTarget;
import com.easyopstech.easyops.model.thana.ThanaResponse;
import com.easyopstech.easyops.model.trade_promotion.TradePromotion;
import com.easyopstech.easyops.model.update_password.ChangePassword;
import com.easyopstech.easyops.model.update_password.ChangePasswordResponse;
import com.easyopstech.easyops.model.update_profile.UpdateProfile;
import com.easyopstech.easyops.model.update_profile.UpdateProfileResponse;
import com.easyopstech.easyops.model.UserLocation;
import com.easyopstech.easyops.model.price_variation.PriceVariationResponse;
import com.easyopstech.easyops.model.variant_product.VariantProductResponse;
import com.easyopstech.easyops.model.performance.payments_model.PaymentsResponse;
import com.easyopstech.easyops.performance.leaderboard.db_model.LeaderboardTotalPerticipatesResponse;
import com.easyopstech.easyops.performance.leaderboard.db_model.ranking.RankingResponse;
import com.easyopstech.easyops.performance.leaderboard.db_model.top_user.LeaderboardTopUserPositionResponse;
import com.easyopstech.easyops.sales_return.model.get.SalesReturnGetResponse;
import com.easyopstech.easyops.sales_return.model.post.SalesReturn2019Response;
import com.easyopstech.easyops.sales_return.model.post.SalesReturnPostBody;

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

public interface RootApiEndpointInterface {
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
    Call<Outlets> getOutlets();

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

    @POST("v2/visit/store")
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



    //For LeaderBoard
    @GET("v1/leaderboard-checker-user-position")
    Call<LeaderboardTopUserPositionResponse> getTopCheckerUserPosition(@Query("time") String time);
    @GET("v1/leaderboard-collector_user_position")
    Call<LeaderboardTopUserPositionResponse> getTopCollectorUserPosition(@Query("time") String time);
    @GET("v1/leaderboard-user-position")
    Call<LeaderboardTopUserPositionResponse> getTopInvoicerUserPosition(@Query("time") String time);


    @GET("v1/leaderboard-total-collector-perticipates")
    Call<LeaderboardTotalPerticipatesResponse> getTotalCollectorPerticipants(@Query("time") String time);
    @GET("v1/leaderboard-total-checker-perticipates")
    Call<LeaderboardTotalPerticipatesResponse> getTotalCheckerPerticipants(@Query("time") String time);
    @GET("v1/leaderboard-total-perticipates")
    Call<LeaderboardTotalPerticipatesResponse> getTotalInvoicerPerticipants(@Query("time") String time);



    @GET("v1/leaderboard-user-ranking")
    Call<RankingResponse> getInvoicerUserRanking(@Query("time") String time);

    @GET("v1/leaderboard-collector_user_ranking")
    Call<RankingResponse> getCollectorUserRanking(@Query("time") String time);

    @GET("v1/leaderboard-checker_user_ranking")
    Call<RankingResponse> getCheckerUserRanking(@Query("time") String time);



}