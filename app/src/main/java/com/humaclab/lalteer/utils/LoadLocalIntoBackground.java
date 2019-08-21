package com.humaclab.lalteer.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.humaclab.lalteer.SelliscopeApiEndpointInterface;
import com.humaclab.lalteer.SelliscopeApplication;
import com.humaclab.lalteer.model.BrandResponse;
import com.humaclab.lalteer.model.CategoryResponse;
import com.humaclab.lalteer.model.District.DistrictResponse;
import com.humaclab.lalteer.model.OutletType.OutletTypeResponse;
import com.humaclab.lalteer.model.Outlets;
import com.humaclab.lalteer.model.Products.Product;
import com.humaclab.lalteer.model.Products.ProductResponse;
import com.humaclab.lalteer.model.RoutePlan.RouteDetailsResponse;
import com.humaclab.lalteer.model.Thana.ThanaResponse;

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
    private OnOutletUpdateComplete mOnOutletUpdateComplete;

    public void setOnOutletUpdateComplete(OnOutletUpdateComplete onOutletUpdateComplete) {
        mOnOutletUpdateComplete = onOutletUpdateComplete;
    }

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
            sessionManager.setAllDataLoaded();
        }
    }

    public void updateAllData() {
        this.loadProduct();
        this.loadOutlet(true);
        this.loadOutletType();
        this.loadDistrict();
        this.loadThana();
    }

    public void loadProduct() {
        if (sessionManager.isLoggedIn()) {
            Call<ProductResponse> call = apiService.getProducts();
            call.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                    if (response.code() == 200) {
                        try {
                            List<Product> products = null;
                            if (response.body() != null) {
                                products = response.body().getResult().getProducts();
                            }
                            //for removing previous data
                            databaseHandler.removeProductCategoryBrand();
                            databaseHandler.addProduct(products);
                            loadCategory();
                            loadBrand();
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
        Call<CategoryResponse> call = apiService.getCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {

                if (response.code() == 200) {

                    try {
                        List<CategoryResponse.CategoryResult> categories = null;
                        if (response.body() != null) {
                            categories = response.body().result.categoryResults;
                        }
                        if (categories != null) {
                            for (CategoryResponse.CategoryResult result : categories) {
                                databaseHandler.addCategory(result.id, result.name);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("tareq_test", "Categories error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.d("tareq_test", "Caregories failed");
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
                        List<BrandResponse.BrandResult> brands = null;
                        if (response.body() != null) {
                            brands = response.body().result.brandResults;
                        }
                        if (brands != null) {
                            for (BrandResponse.BrandResult result : brands) {
                                databaseHandler.addBrand(result.id, result.name);
                            }
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
                        Outlets getOutletListSuccessful = null;
                        if (response.body() != null) {
                            getOutletListSuccessful = gson.fromJson(response.body().string(), Outlets.class);
                        }
                        if (!fullUpdate) {
                            // if (getOutletListSuccessful.outletsResult.outlets.size() != databaseHandler.getSizeOfOutlet()) {
                            databaseHandler.removeOutlet();
                            if (getOutletListSuccessful != null) {
                                databaseHandler.addOutlet(getOutletListSuccessful.outletsResult.outlets);
                            }
                            if (mOnOutletUpdateComplete != null)
                                mOnOutletUpdateComplete.onUpdateComplete();
                            //  }
                        } else {
                            databaseHandler.removeOutlet();
                            if (getOutletListSuccessful != null) {
                                databaseHandler.addOutlet(getOutletListSuccessful.outletsResult.outlets);
                            }
                            if (mOnOutletUpdateComplete != null)
                                mOnOutletUpdateComplete.onUpdateComplete();
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


    public void saveOutletRoutePlan(List<RouteDetailsResponse.OutletItem> outletItemList) {
        //databaseHandler.removeOutlet();
        databaseHandler.updateOutletRoutePlan(outletItemList);
    }

    private void loadDistrict() {
        Call<DistrictResponse> call = apiService.getDistricts();
        call.enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                Gson gson = new Gson();

                if (response.code() == 200) {
                    try {
                        if (response.body() != null) {
                            databaseHandler.setDistrict(response.body().getResult().getDistrict());
                        }
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

                if (response.code() == 200) {
                    try {
                        if (response.body() != null) {
                            databaseHandler.setThana(response.body().getResult().getThana());
                        }
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

                if (response.code() == 200) {
                    try {
                        if (response.body() != null) {
                            databaseHandler.setOutletType(response.body().getResult().getType());
                        }
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

    public interface OnOutletUpdateComplete {
        void onUpdateComplete();
    }
}
