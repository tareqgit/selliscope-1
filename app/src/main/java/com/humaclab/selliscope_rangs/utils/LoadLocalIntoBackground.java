package com.humaclab.selliscope_rangs.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.humaclab.selliscope_rangs.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_rangs.SelliscopeApplication;
import com.humaclab.selliscope_rangs.model.District.DistrictResponse;
import com.humaclab.selliscope_rangs.model.OutletType.OutletTypeResponse;
import com.humaclab.selliscope_rangs.model.Outlets;
import com.humaclab.selliscope_rangs.model.ProductResponse;
import com.humaclab.selliscope_rangs.model.Thana.ThanaResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by leon on 29/11/17.
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
            this.loadProduct();
            this.loadOutlet();
            this.loadOutletType();
            this.loadDistrict();
            this.loadThana();
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
                        System.out.println("Products: " + new Gson().toJson(response.body()));
                        try {
                            List<ProductResponse.Product> products = response.body().getProductResult().getProducts();
                            //for removing previous data
                            databaseHandler.removeProductCategoryBrand();
                            for (final ProductResponse.Product result : products) {
                                Call<StockResponse> call1 = apiService.getProductStock(result.getId());
                                call1.enqueue(new Callback<StockResponse>() {
                                    @Override
                                    public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
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

                                    }
                                });
                            }

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
