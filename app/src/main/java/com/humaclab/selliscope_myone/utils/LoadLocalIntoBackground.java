package com.humaclab.selliscope_myone.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.model.district.DistrictResponse;
import com.humaclab.selliscope_myone.model.outletType.OutletTypeResponse;
import com.humaclab.selliscope_myone.model.Outlets;
import com.humaclab.selliscope_myone.model.ProductResponse;
import com.humaclab.selliscope_myone.model.thana.ThanaResponse;
import com.humaclab.selliscope_myone.model.promotion.PromotionQuantityResponse;
import com.humaclab.selliscope_myone.model.promotion.PromotionValueResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by leon on 29/11/17.
 */

/**
 * update sqlite data from remote database
 */
public class LoadLocalIntoBackground {
    private Context context;
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;
    private SelliscopeApiEndpointInterface apiService;

    public LoadLocalIntoBackground(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.databaseHandler = new DatabaseHandler(context);
        this.apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
    }

    public void loadAll() {
        if (!sessionManager.isAllDataLoaded()) {
           // this.loadPromotion();
            this.loadProduct();
            this.loadOutlet();
          //  this.loadOutletType();
          //  this.loadDistrict();
          //  this.loadThana();
            sessionManager.setAllDataLoaded();
        }
    }

    public void loadProduct() {

        if (sessionManager.isLoggedIn()) {
            Call<ProductResponse> call = apiService.getProducts();
            call.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                    if (response.code() == 200) {
                     Log.d("tareq_test" , "Products: " + new Gson().toJson(response.body()));
                        try {
                            List<ProductResponse.Product> products = response.body().getProductResult().getProducts();
                            //for removing previous data
                            databaseHandler.removeProductCategoryBrand();
                            for (final ProductResponse.Product result : products) {
                                //as product api  doesn't provide us stock
                                //so we are getting stock from another api
                                // and when we get product's info and stock info, we update the database
                                Call<StockResponse> call1 = apiService.getProductStock(result.getId());
                                call1.enqueue(new Callback<StockResponse>() {
                                    @Override
                                    public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                                        Log.d("tareq_test", "stock" + response.body().getStock().getStock());
                                        databaseHandler.addProduct(
                                                result.getId(),
                                                result.getName(),
                                                result.getPrice(),
                                                result.getImg(),
                                                result.getBrand(),
                                                result.getCategory(),
                                                response.body().getStock().getStock()
                                        );


                                    }

                                    @Override
                                    public void onFailure(Call<StockResponse> call, Throwable t) {
                                        Log.d("tareq_test" , "failed"+ t.getMessage());
                                    }
                                });
                            }

                            //inserting category value from
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadCategory();
                                    loadBrand();
                                }
                            }, 60000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProductResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void loadPromotion() {
        databaseHandler.removePromotion();
        Call<PromotionQuantityResponse> call = apiService.getPromotionQuantity();
        Call<PromotionValueResponse> call1 = apiService.getPromotionValue();

        call.enqueue(new Callback<PromotionQuantityResponse>() {
            @Override
            public void onResponse(Call<PromotionQuantityResponse> call, Response<PromotionQuantityResponse> response) {
                databaseHandler.addProductPromotionQuantity(response.body().getPromotionQuantityItems());
            }

            @Override
            public void onFailure(Call<PromotionQuantityResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
        call1.enqueue(new Callback<PromotionValueResponse>() {
            @Override
            public void onResponse(Call<PromotionValueResponse> call, Response<PromotionValueResponse> response) {
                databaseHandler.addProductPromotionValue(response.body().getPromotionValueItems());
            }

            @Override
            public void onFailure(Call<PromotionValueResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadCategory() {
        databaseHandler.addCategory();
    }

    private void loadBrand() {
        databaseHandler.addBrand();
    }

    public void loadOutlet() {
        Call<ResponseBody> call = apiService.getOutlets();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.code() == 200) {
                    try {
                        //converting res
                        Outlets.Successful getOutletListSuccessful = gson.fromJson(response.body().string(), Outlets.Successful.class);
                        if (getOutletListSuccessful.outletsResult.outlets.size() != databaseHandler.getSizeOfOutlet()) {
                            databaseHandler.removeOutlet();
                            databaseHandler.addOutlet(getOutletListSuccessful.outletsResult.outlets);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadDistrict() {
        Call<DistrictResponse> call = apiService.getDistricts();
        call.enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                if (response.code() == 200) {
                    try {
                        databaseHandler.setDistrict(response.body().getResult().getDistrict());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DistrictResponse> call, Throwable t) {
                Log.d("Response", t.toString());
            }
        });
    }

    private void loadThana() {
        Call<ThanaResponse> call = apiService.getThanas();
        call.enqueue(new Callback<ThanaResponse>() {
            @Override
            public void onResponse(Call<ThanaResponse> call, Response<ThanaResponse> response) {
                if (response.code() == 200) {
                    try {
                        databaseHandler.setThana(response.body().getResult().getThana());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ThanaResponse> call, Throwable t) {
                Log.d("Response", t.toString());

            }
        });
    }

    private void loadOutletType() {
        Call<OutletTypeResponse> call = apiService.getOutletTypes();
        call.enqueue(new Callback<OutletTypeResponse>() {
            @Override
            public void onResponse(Call<OutletTypeResponse> call, Response<OutletTypeResponse> response) {
                if (response.code() == 200) {
                    try {
                        databaseHandler.setOutletType(response.body().getResult().getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<OutletTypeResponse> call, Throwable t) {
                Log.d("Response", t.toString());
            }
        });
    }
}
