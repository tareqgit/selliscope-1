package com.nexzen.salebeebd;

import com.nexzen.salebeebd.model.AddNewOrder;
import com.nexzen.salebeebd.model.AppVersion.AppVersion;
import com.nexzen.salebeebd.model.BrandResponse;
import com.nexzen.salebeebd.model.CategoryResponse;
import com.nexzen.salebeebd.model.CreateOutlet;
import com.nexzen.salebeebd.model.DeliverProductResponse;
import com.nexzen.salebeebd.model.DeliveryResponse;
import com.nexzen.salebeebd.model.Diameter.DiameterResponse;
import com.nexzen.salebeebd.model.District.DistrictResponse;
import com.nexzen.salebeebd.model.GodownRespons;
import com.nexzen.salebeebd.model.IMEIandVerison;
import com.nexzen.salebeebd.model.InspectionResponse;
import com.nexzen.salebeebd.model.OrderResponse;
import com.nexzen.salebeebd.model.OutletType.OutletTypeResponse;
import com.nexzen.salebeebd.model.Payment;
import com.nexzen.salebeebd.model.PaymentResponse;
import com.nexzen.salebeebd.model.PurchaseHistory.PurchaseHistoryResponse;
import com.nexzen.salebeebd.model.RoutePlan.RouteDetailsResponse;
import com.nexzen.salebeebd.model.RoutePlan.RouteResponse;
import com.nexzen.salebeebd.model.SellsReturnResponse;
import com.nexzen.salebeebd.model.Target.OutletTarget;
import com.nexzen.salebeebd.model.Thana.ThanaResponse;
import com.nexzen.salebeebd.model.UpdatePassword.ChangePassword;
import com.nexzen.salebeebd.model.UpdatePassword.ChangePasswordResponse;
import com.nexzen.salebeebd.model.UpdateProfile.UpdateProfile;
import com.nexzen.salebeebd.model.UpdateProfile.UpdateProfileResponse;
import com.nexzen.salebeebd.model.UserLocation;
import com.nexzen.salebeebd.model.VariantProduct.VariantProductResponse;

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

    @POST("return-product")
    Call<SellsReturnResponse> returnProduct(@Body SellsReturnResponse.SellsReturn returnProduct);

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