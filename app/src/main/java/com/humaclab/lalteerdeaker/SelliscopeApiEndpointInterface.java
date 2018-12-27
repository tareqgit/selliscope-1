package com.humaclab.lalteerdeaker;

import com.humaclab.lalteerdeaker.model.AddNewOrder;
import com.humaclab.lalteerdeaker.model.AppVersion.AppVersion;
import com.humaclab.lalteerdeaker.model.BrandResponse;
import com.humaclab.lalteerdeaker.model.CategoryResponse;
import com.humaclab.lalteerdeaker.model.CreateOutlet;
import com.humaclab.lalteerdeaker.model.DeliverProductResponse;
import com.humaclab.lalteerdeaker.model.DeliveryResponse;
import com.humaclab.lalteerdeaker.model.Diameter.DiameterResponse;
import com.humaclab.lalteerdeaker.model.District.DistrictResponse;
import com.humaclab.lalteerdeaker.model.GodownRespons;
import com.humaclab.lalteerdeaker.model.IMEIandVerison;
import com.humaclab.lalteerdeaker.model.InspectionResponse;
import com.humaclab.lalteerdeaker.model.OrderResponse;
import com.humaclab.lalteerdeaker.model.OutletType.OutletTypeResponse;
import com.humaclab.lalteerdeaker.model.Payment;
import com.humaclab.lalteerdeaker.model.PaymentResponse;
import com.humaclab.lalteerdeaker.model.Products.ProductResponse;
import com.humaclab.lalteerdeaker.model.PromotionalAds.PromotionalAds;
import com.humaclab.lalteerdeaker.model.PurchaseHistory.PurchaseHistoryResponse;
import com.humaclab.lalteerdeaker.model.RoutePlan.RouteDetailsResponse;
import com.humaclab.lalteerdeaker.model.RoutePlan.RouteResponse;
import com.humaclab.lalteerdeaker.model.SellsReturnResponse;
import com.humaclab.lalteerdeaker.model.Target.OutletTarget;
import com.humaclab.lalteerdeaker.model.Thana.ThanaResponse;
import com.humaclab.lalteerdeaker.model.UpdatePassword.ChangePassword;
import com.humaclab.lalteerdeaker.model.UpdatePassword.ChangePasswordResponse;
import com.humaclab.lalteerdeaker.model.UpdateProfile.UpdateProfile;
import com.humaclab.lalteerdeaker.model.UpdateProfile.UpdateProfileResponse;
import com.humaclab.lalteerdeaker.model.UserLocation;

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

    @GET("dealers")
    Call<ResponseBody> getOutlets();

    @GET("category")
    Call<CategoryResponse> getCategories();

    @GET("brands")
    Call<BrandResponse> getBrands();

    @GET("products")
    Call<ProductResponse> getProducts();

    /*@GET("variant-product")
    Call<VariantProductResponse> getProducts();*/

    @GET("order")
    Call<OrderResponse> getOrders();

    @GET("outlet-payment-order/{outlet_id}")
    Call<Payment> getPayment(@Path("outlet_id") int outletId);

    @GET("payment")
    Call<Payment> getPayment();

    @GET("order/delivery")
    Call<DeliveryResponse> getDelivery();

    @GET("delivery")
    Call<DeliveryResponse> getSalesReturn(@Query("outlet_id") int outletID);

    @GET("godown")
    Call<GodownRespons> getGodown();

     @GET("district")
    Call<DistrictResponse> getDistricts();

     @GET("thana")
    Call<ThanaResponse> getThanas();

    @GET("dealers-type")
    Call<OutletTypeResponse> getOutletTypes();

    @GET("visit")
    Call<ResponseBody> getVisits();

    @GET("target")
    Call<ResponseBody> getTargets();

    @GET("diameter")
    Call<DiameterResponse> getDiameter();

    @GET("dealers/{outlet_id}/purchase-history")
    Call<PurchaseHistoryResponse> getPurchaseHistory(@Path("outlet_id") int outletID);

    @GET("target/user")
    Call<OutletTarget> getTarget();

     @GET("route-plan")
    Call<RouteResponse> getRoutes();

     @GET("route-plan/{route_id}")
    Call<RouteDetailsResponse> getRouteDetails(@Path("route_id") int routeId);
    //POST methods

    @GET("login")
    Call<ResponseBody> getUser();

    @GET("app-version")
    Call<AppVersion> getAppsversion();

    //Outlet Wise Target
    @GET("target/dealer/{outlet_id}")
    Call<OutletTarget> getPutletTarget(@Path("outlet_id") int outletId);

    @GET("banner")
    Call<PromotionalAds> getPromotionalAds();


    @POST("dealers/create/")
    Call<ResponseBody> createOutlet(@Body CreateOutlet createOutlet);

     @POST("visit/store/")
    Call<ResponseBody> sendUserLocation(@Body UserLocation userLocation);

    @POST("order/store/")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);
    /*@POST("order/store")
    Call<AddNewOrder.OrderResponse> addOrder(@Body AddNewOrder order);*/

    @POST("payment/collect/")
    Call<PaymentResponse.PaymentSucessfull> payNow(@Body PaymentResponse payment);

    @POST("order/delivery/store/")
    Call<DeliverProductResponse> deliverProduct(@Body DeliverProductResponse deliverProduct);

    @POST("return-product")
    Call<SellsReturnResponse> returnProduct(@Body SellsReturnResponse.SellsReturn returnProduct);

    @POST("inspection/store/")
    Call<InspectionResponse> inspectOutlet(@Body InspectionResponse.Inspection inspection);

    @POST("version-imei/")
    Call<ResponseBody> sendIMEIAndVersion(@Body IMEIandVerison imeIandVerison);

    @POST("change-password")
    Call<ChangePasswordResponse> changePassword(@Body ChangePassword changePassword);

    @PUT("dealers/{id}/edit/")
    Call<ResponseBody> updateOutlet(@Path("id") int outletID, @Body CreateOutlet createOutlet);

    @POST("profile/update")
    Call<UpdateProfileResponse> updateProfile(@Body UpdateProfile updateProfile);
}