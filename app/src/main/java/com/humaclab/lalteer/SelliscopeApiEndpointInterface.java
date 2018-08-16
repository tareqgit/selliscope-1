package com.humaclab.lalteer;

import com.humaclab.lalteer.model.AddNewOrder;
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
import com.humaclab.lalteer.model.PurchaseHistory.PurchaseHistoryResponse;
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
import com.humaclab.lalteer.model.VariantProduct.VariantProductResponse;

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

    @GET("outlets")
    Call<ResponseBody> getOutlets();

    @GET("category")
    Call<CategoryResponse> getCategories();

    @GET("brands")
    Call<BrandResponse> getBrands();

    /*@GET("product")
    Call<ProductResponse> getProducts();*/
    @GET("variant-product")
    Call<VariantProductResponse> getProducts();

    @GET("order")
    Call<OrderResponse> getOrders();

    @GET("payment/{outlet_id}")
    Call<Payment> getPayment(@Path("outlet_id") int outletId);

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

    @GET("outlets-type")
    Call<OutletTypeResponse> getOutletTypes();

    @GET("visit")
    Call<ResponseBody> getVisits();

    @GET("target")
    Call<ResponseBody> getTargets();

    @GET("diameter")
    Call<DiameterResponse> getDiameter();

    @GET("outlets/{outlet_id}/purchase-history")
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

    @POST("outlets/create/")
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

    @PUT("outlets/{id}/edit/")
    Call<ResponseBody> updateOutlet(@Path("id") int outletID, @Body CreateOutlet createOutlet);

    @POST("profile/update")
    Call<UpdateProfileResponse> updateProfile(@Body UpdateProfile updateProfile);
}