package com.humaclab.selliscope.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.humaclab.selliscope.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope.SelliscopeApplication;
import com.humaclab.selliscope.model.BrandResponse;
import com.humaclab.selliscope.model.CategoryResponse;
import com.humaclab.selliscope.model.district.DistrictResponse;
import com.humaclab.selliscope.model.outlet_type.OutletTypeResponse;
import com.humaclab.selliscope.model.Outlets;
import com.humaclab.selliscope.model.price_variation.PriceVariationResponse;
import com.humaclab.selliscope.model.price_variation.Product;
import com.humaclab.selliscope.model.reason.ReasonResponse;
import com.humaclab.selliscope.model.route_plan.RouteDetailsResponse;
import com.humaclab.selliscope.model.thana.ThanaResponse;
import com.humaclab.selliscope.model.trade_promotion.Result;
import com.humaclab.selliscope.model.trade_promotion.TradePromotion;
import com.humaclab.selliscope.model.variant_product.ProductsItem;
import com.humaclab.selliscope.model.variant_product.VariantProductResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
            this.loadOutlet(false);
            this.loadOutletType();
            this.loadDistrict();
            this.loadThana();
            this.loadReason();
            sessionManager.setAllDataLoaded();
        }
    }

    public void updateAllData() {
        this.loadProduct();
        this.loadOutlet(true);
        this.loadOutletType();
        this.loadDistrict();
        this.loadThana();
        this.loadReason();
    }

    public void loadProduct() {
        if (sessionManager.isLoggedIn()) {
            Call<VariantProductResponse> call = apiService.getProducts();
            call.enqueue(new Callback<VariantProductResponse>() {
                @Override
                public void onResponse(Call<VariantProductResponse> call, Response<VariantProductResponse> response) {
                    if (response.code() == 200) {
                        try {
                            List<ProductsItem> products = response.body().getResult().getProducts();
                            //for removing previous data
                            databaseHandler.removeProductCategoryBrand();
                            databaseHandler.addProduct(products);
                            loadCategory();
                            loadBrand();
                            loadPriceVariation();
                            loadTradePromoion();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<VariantProductResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void loadPriceVariation() {
            Call<PriceVariationResponse> call = apiService.getPriceVariation();
            call.enqueue(new Callback<PriceVariationResponse>() {
                @Override
                public void onResponse(Call<PriceVariationResponse> call, Response<PriceVariationResponse> response) {
                    if(response.isSuccessful()) {
                        if (response.code() == 200) {
                            try {
                                List<Product> products = response.body().getResult().getProducts();
                                databaseHandler.add_price_Variation(products);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    else {
                        response.errorBody();
                    }
                }

                @Override
                public void onFailure(Call<PriceVariationResponse> call, Throwable t) {
                    t.getMessage();

                }
            });
    }
    private void loadTradePromoion() {
        Call<TradePromotion> call = apiService.getTradePromotion();
        call.enqueue(new Callback<TradePromotion>() {
            @Override
            public void onResponse(Call<TradePromotion> call, Response<TradePromotion> response) {
                if(response.isSuccessful()) {
                    if (response.code() == 200) {
                        try {
                            List<Result> results = response.body().getResult();
                            databaseHandler.add_trade_promotion(results);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                else {
                    response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<TradePromotion> call, Throwable t) {
                t.getMessage();

            }
        });
    }
    private void loadCategory() {
        Call<CategoryResponse> call = apiService.getCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.code() == 200) {
                    try {
                        List<CategoryResponse.CategoryResult> categories = response.body().result.categoryResults;
                        for (CategoryResponse.CategoryResult result : categories) {
                            databaseHandler.addCategory(result.id, result.name);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void loadBrand() {
        Call<BrandResponse> call = apiService.getBrands();
        call.enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.code() == 200) {
                    try {
                        List<BrandResponse.BrandResult> brands = response.body().result.brandResults;
                        for (BrandResponse.BrandResult result : brands) {
                            databaseHandler.addBrand(result.id, result.name);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void loadOutlet(final boolean fullUpdate) {
        Call<ResponseBody> call = apiService.getOutlets();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.code() == 200) {
                    try {
                        Outlets getOutletListSuccessful = gson.fromJson(response.body().string(), Outlets.class);
                        if (!fullUpdate) {
                            if (getOutletListSuccessful.outletsResult.outlets.size() != databaseHandler.getSizeOfOutlet()) {
                                databaseHandler.removeOutlet();
                                databaseHandler.addOutlet(getOutletListSuccessful.outletsResult.outlets);
                            }
                        } else {
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


    public void saveOutletRoutePlan(List<RouteDetailsResponse.OutletItem> outletItemList){
        //databaseHandler.removeOutlet();
        databaseHandler.updateOutletRoutePlan(outletItemList);
    }

    private void loadDistrict() {
        Call<DistrictResponse> call = apiService.getDistricts();
        call.enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                Gson gson = new Gson();
                Timber.d("Response " + response.code() + " " + response.body().toString());
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
                Gson gson = new Gson();
                Timber.d("Response " + response.code() + " " + response.body().toString());
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
                Gson gson = new Gson();
                Timber.d("Response " + response.code() + " " + response.body().toString());
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

    private void loadReason(){
        Call<ReasonResponse> resultCall = apiService.getSalesReturnReasony();
        resultCall.enqueue(new Callback<ReasonResponse>() {
            @Override
            public void onResponse(Call<ReasonResponse> call, Response<ReasonResponse> response) {
                if(response.code() == 200){
                    try {
                        databaseHandler.setSellsResponseReason(response.body().getResult());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReasonResponse> call, Throwable t) {

            }
        });
    }



}
