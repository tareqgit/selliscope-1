package com.humaclab.selliscope.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import javax.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by leon on 29/11/17.
 */

public class LoadLocalIntoBackground {
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;
    private SelliscopeApiEndpointInterface apiService;
    private Context mContext;
    public LoadLocalIntoBackground(Context context) {
        mContext=context;
        this.sessionManager = new SessionManager(context);
        this.databaseHandler = new DatabaseHandler(context);
        this.apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
    }


    public void loadAll(LoadCompleteListener loadCompleteListener) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            /*   if (!sessionManager.isAllDataLoaded()) {*/
            this.loadProduct(new LoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    loadOutlet(new LoadCompleteListener() {
                        @Override
                        public void onLoadComplete() {
                            loadOutletType(new LoadCompleteListener() {
                                @Override
                                public void onLoadComplete() {

                                    loadDistrict(new LoadCompleteListener() {
                                        @Override
                                        public void onLoadComplete() {
                                            loadThana(new LoadCompleteListener() {
                                                @Override
                                                public void onLoadComplete() {

                                                    if (loadCompleteListener != null)
                                                        loadCompleteListener.onLoadComplete();
                                                    //  this.loadReason();
                                                    /*               sessionManager.setAllDataLoaded();*/
                                                }

                                                @Override
                                                public void onLoadFailed(String msg) {
                                                    if (loadCompleteListener != null)
                                                        loadCompleteListener.onLoadFailed(msg);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onLoadFailed(String msg) {
                                            if (loadCompleteListener != null)
                                                loadCompleteListener.onLoadFailed(msg);
                                        }
                                    });
                                }

                                @Override
                                public void onLoadFailed(String msg) {

                                    if (loadCompleteListener != null)
                                        loadCompleteListener.onLoadFailed(msg);
                                }
                            });
                        }

                        @Override
                        public void onLoadFailed(String msg) {
                            if (loadCompleteListener != null)
                                loadCompleteListener.onLoadFailed(msg);
                        }
                    });
                }

                @Override
                public void onLoadFailed(String msg) {
                    if (loadCompleteListener != null) loadCompleteListener.onLoadFailed(msg);
                }
            });
        }else{
            if (loadCompleteListener != null) loadCompleteListener.onLoadFailed("No Internet");
        }

        /*  }*/
    }

    public void updateAllData(LoadCompleteListener loadCompleteListener) {
        this.loadProduct(new LoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                loadOutlet(new LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        loadOutletType(new LoadCompleteListener() {
                            @Override
                            public void onLoadComplete() {

                                loadDistrict(new LoadCompleteListener() {
                                    @Override
                                    public void onLoadComplete() {
                                        loadThana(new LoadCompleteListener() {
                                            @Override
                                            public void onLoadComplete() {
                                                if (loadCompleteListener != null)
                                                    loadCompleteListener.onLoadComplete();
                                            }

                                            @Override
                                            public void onLoadFailed(String msg) {
                                                if (loadCompleteListener != null)
                                                    loadCompleteListener.onLoadFailed(msg);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onLoadFailed(String msg) {
                                        if (loadCompleteListener != null)
                                            loadCompleteListener.onLoadFailed(msg);
                                    }
                                });
                            }

                            @Override
                            public void onLoadFailed(String msg) {
                                if (loadCompleteListener != null)
                                    loadCompleteListener.onLoadFailed(msg);
                            }
                        });
                    }

                    @Override
                    public void onLoadFailed(String msg) {
                        if (loadCompleteListener != null) loadCompleteListener.onLoadFailed(msg);
                    }
                });
            }

            @Override
            public void onLoadFailed(String msg) {
                if (loadCompleteListener != null) loadCompleteListener.onLoadFailed(msg);
            }
        });


        //     this.loadReason();
    }

    public void loadProduct(LoadCompleteListener loadCompleteListener) {
        if (sessionManager.isLoggedIn()) {
            Call<VariantProductResponse> call = apiService.getProducts();
            call.enqueue(new Callback<VariantProductResponse>() {
                @Override
                public void onResponse(Call<VariantProductResponse> call, Response<VariantProductResponse> response) {
                    if (response.code() == 200) {
                        try {


                            if (response.body() != null) {
                                List<ProductsItem> products = response.body().getResult().getProducts();

                                //for removing previous data
                                //  databaseHandler.removeProductCategoryBrand();
                                databaseHandler.addProduct(products);

                                loadCategory(new LoadCompleteListener() {
                                    @Override
                                    public void onLoadComplete() {
                                        loadBrand(new LoadCompleteListener() {
                                            @Override
                                            public void onLoadComplete() {
                                                loadPriceVariation(new LoadCompleteListener() {
                                                    @Override
                                                    public void onLoadComplete() {
                                                        loadTradePromoion(new LoadCompleteListener() {
                                                            @Override
                                                            public void onLoadComplete() {
                                                                if (loadCompleteListener != null)
                                                                    loadCompleteListener.onLoadComplete();
                                                            }

                                                            @Override
                                                            public void onLoadFailed(String msg) {
                                                                if (loadCompleteListener != null)
                                                                    loadCompleteListener.onLoadFailed(msg);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onLoadFailed(String msg) {
                                                        if (loadCompleteListener != null)
                                                            loadCompleteListener.onLoadFailed(msg);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onLoadFailed(String msg) {
                                                if (loadCompleteListener != null)
                                                    loadCompleteListener.onLoadFailed(msg);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onLoadFailed(String msg) {
                                        if (loadCompleteListener != null)
                                            loadCompleteListener.onLoadFailed(msg);
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<VariantProductResponse> call, Throwable t) {
                    t.printStackTrace();
                    if (loadCompleteListener != null)
                        loadCompleteListener.onLoadFailed("load product: " + t.getMessage());
                }
            });
        }
    }

    private void loadPriceVariation(@Nullable LoadCompleteListener loadCompleteListener) {
        Call<PriceVariationResponse> call = apiService.getPriceVariation();
        call.enqueue(new Callback<PriceVariationResponse>() {
            @Override
            public void onResponse(Call<PriceVariationResponse> call, Response<PriceVariationResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        try {
                            List<Product> products = response.body().getResult().getProducts();
                            databaseHandler.add_price_Variation(products);
                            if (loadCompleteListener != null) loadCompleteListener.onLoadComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<PriceVariationResponse> call, Throwable t) {
                t.getMessage();
                if (loadCompleteListener != null)
                    loadCompleteListener.onLoadFailed("load price variation: " + t.getMessage());

            }
        });
    }

    private void loadTradePromoion(@Nullable LoadCompleteListener loadCompleteListener) {
        Call<TradePromotion> call = apiService.getTradePromotion();
        call.enqueue(new Callback<TradePromotion>() {
            @Override
            public void onResponse(Call<TradePromotion> call, Response<TradePromotion> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        try {
                            List<Result> results = response.body().getResult();
                            databaseHandler.add_trade_promotion(results);
                            if (loadCompleteListener != null) loadCompleteListener.onLoadComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<TradePromotion> call, Throwable t) {
                t.getMessage();
                if (loadCompleteListener != null)
                    loadCompleteListener.onLoadFailed("load trade promo: " + t.getMessage());

            }
        });
    }

    public void loadCategory(@Nullable LoadCompleteListener loadCompleteListener) {
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
                        if (loadCompleteListener != null) loadCompleteListener.onLoadComplete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                t.printStackTrace();
                if (loadCompleteListener != null)
                    loadCompleteListener.onLoadFailed("load category: " + t.getMessage());
            }
        });

    }

    public void loadBrand(@Nullable LoadCompleteListener loadCompleteListener) {
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
                        if (loadCompleteListener != null) loadCompleteListener.onLoadComplete();
                    } catch (Exception e) {
                        Log.e("tareq_test", "Brand: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {

                Log.e("tareq_test", "Brand: " + t.getMessage());
                if (loadCompleteListener != null)
                    loadCompleteListener.onLoadFailed("load brand: " + t.getMessage());
            }
        });
    }

    public void loadOutlet(LoadCompleteListener callback) {
        Call<Outlets> call = apiService.getOutlets();
        call.enqueue(new Callback<Outlets>() {
            @Override
            public void onResponse(Call<Outlets> call, Response<Outlets> response) {
               // Gson gson = new Gson();
                if (response.code() == 200) {
                    //  String string;
                    if (response.body() != null) {
                       // string = response.body().string();

                      //  Outlets getOutletListSuccessful = gson.fromJson(string, Outlets.class);
                        /* if (!fullUpdate) {
                            if (getOutletListSuccessful.outletsResult.outlets.size() != databaseHandler.getSizeOfOutlet()) {
                            databaseHandler.removeOutlet();
                            databaseHandler.addOutlet(getOutletListSuccessful.outletsResult.outlets);
                          }
                      } else {
                        databaseHandler.removeOutlet();*/
                        databaseHandler.addOutlet(response.body().outletsResult.outlets);
                        if (callback != null) callback.onLoadComplete();
                        //}
                    } else {
                        if (callback != null)
                            callback.onLoadFailed("Null response from Server");
                    }
                }
            }

            @Override
            public void onFailure(Call<Outlets> call, Throwable t) {
                t.printStackTrace();
                if (callback != null) callback.onLoadFailed("load outlet: " + t.getMessage());
            }
        });
    }


    public void saveOutletRoutePlan(List<RouteDetailsResponse.OutletItem> outletItemList) {
        //databaseHandler.removeOutlet();
        databaseHandler.updateOutletRoutePlan(outletItemList);
    }

    public void loadDistrict(LoadCompleteListener loadCompleteListener) {
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

                    if (loadCompleteListener != null) loadCompleteListener.onLoadComplete();
                }
            }

            @Override
            public void onFailure(Call<DistrictResponse> call, Throwable t) {
                Log.d("Response", t.toString());
                if (loadCompleteListener != null)
                    loadCompleteListener.onLoadFailed("load district: " + t.getMessage());
            }
        });
    }

    public void loadThana(LoadCompleteListener loadCompleListener) {
        Call<ThanaResponse> call = apiService.getThanas();
        call.enqueue(new Callback<ThanaResponse>() {
            @Override
            public void onResponse(Call<ThanaResponse> call, Response<ThanaResponse> response) {
                Gson gson = new Gson();
                Log.d("tareq_test", "Response Thana:  " + response.code() + " " + response.body().toString());
                if (response.code() == 200) {
                    try {
                        databaseHandler.setThana(response.body().getResult().getThana());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (loadCompleListener != null) loadCompleListener.onLoadComplete();
                }
            }

            @Override
            public void onFailure(Call<ThanaResponse> call, Throwable t) {
                Log.d("Response", t.toString());
                if (loadCompleListener != null)
                    loadCompleListener.onLoadFailed("load thana: " + t.getMessage());

            }
        });
    }

    public void loadOutletType(LoadCompleteListener loadCompleListener) {
        Call<OutletTypeResponse> call = apiService.getOutletTypes();
        call.enqueue(new Callback<OutletTypeResponse>() {
            @Override
            public void onResponse(Call<OutletTypeResponse> call, Response<OutletTypeResponse> response) {


                if (response.code() == 200) {
                    try {
                        databaseHandler.setOutletType(response.body().getResult().getType());
                        Log.d("tareq_test", "outlet Type: " + new Gson().toJson(response.body().getResult().getType()));
                    } catch (Exception e) {
                        Log.e("tareq_test", "outlet Type error: " + e.getMessage());
                    }


                    if (loadCompleListener != null) loadCompleListener.onLoadComplete();
                }
            }

            @Override
            public void onFailure(Call<OutletTypeResponse> call, Throwable t) {
                Log.d("Response", t.toString());
                if (loadCompleListener != null)
                    loadCompleListener.onLoadFailed("load outlet type: " + t.getMessage());
            }
        });
    }

    private void loadReason() {
        Call<ReasonResponse> resultCall = apiService.getSalesReturnReasony();
        resultCall.enqueue(new Callback<ReasonResponse>() {
            @Override
            public void onResponse(Call<ReasonResponse> call, Response<ReasonResponse> response) {
                if (response.code() == 200) {
                    try {
                        databaseHandler.setSellsResponseReason(response.body().getResult());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReasonResponse> call, Throwable t) {

            }
        });
    }


    public interface LoadCompleteListener {
        void onLoadComplete();

        void onLoadFailed(String reason);
    }

}
