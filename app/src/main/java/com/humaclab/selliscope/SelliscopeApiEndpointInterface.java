package com.humaclab.selliscope;

import com.humaclab.selliscope.model.AddNewOrder;
import com.humaclab.selliscope.model.AppVersion.AppVersion;
import com.humaclab.selliscope.model.BrandResponse;
import com.humaclab.selliscope.model.CategoryResponse;
import com.humaclab.selliscope.model.CreateOutlet;
import com.humaclab.selliscope.model.DeliverProductResponse;
import com.humaclab.selliscope.model.DeliveryResponse;
import com.humaclab.selliscope.model.Diameter.DiameterResponse;
import com.humaclab.selliscope.model.District.DistrictResponse;
import com.humaclab.selliscope.model.GodownRespons;
import com.humaclab.selliscope.model.IMEIandVerison;
import com.humaclab.selliscope.model.InspectionResponse;
import com.humaclab.selliscope.model.OrderResponse;
import com.humaclab.selliscope.model.OutletType.OutletTypeResponse;
import com.humaclab.selliscope.model.Payment;
import com.humaclab.selliscope.model.PaymentResponse;
import com.humaclab.selliscope.model.PurchaseHistory.PurchaseHistoryResponse;
import com.humaclab.selliscope.model.RoutePlan.RouteDetailsResponse;
import com.humaclab.selliscope.model.RoutePlan.RouteResponse;
import com.humaclab.selliscope.model.SalesReturn.SalesReturnHistory;
import com.humaclab.selliscope.model.SalesReturn.SalesReturnResponse;
import com.humaclab.selliscope.model.SalesReturn.SellsReturnResponsePost;
import com.humaclab.selliscope.model.Target.OutletTarget;
import com.humaclab.selliscope.model.Thana.ThanaResponse;
import com.humaclab.selliscope.model.TradePromotion.TradePromotion;
import com.humaclab.selliscope.model.UpdatePassword.ChangePassword;
import com.humaclab.selliscope.model.UpdatePassword.ChangePasswordResponse;
import com.humaclab.selliscope.model.UpdateProfile.UpdateProfile;
import com.humaclab.selliscope.model.UpdateProfile.UpdateProfileResponse;
import com.humaclab.selliscope.model.UserLocation;
import com.humaclab.selliscope.model.PriceVariation.PriceVariationResponse;
import com.humaclab.selliscope.model.VariantProduct.VariantProductResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Leon on 9/3/2017.
 */

public interface SelliscopeApiEndpointInterface {
    //GET methods

    @GET("delivery/order")
    Call<SalesReturnResponse> getSalesReturnDAta();

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
}