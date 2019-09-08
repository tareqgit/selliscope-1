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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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

    private CompositeDisposable mCompositeDisposable;


    public LoadLocalIntoBackground(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.databaseHandler = new DatabaseHandler(context);
        this.apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
    }

    public LoadLocalIntoBackground(Context context, CompositeDisposable compositeDisposable) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.databaseHandler = new DatabaseHandler(context);
        this.apiService = SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(), sessionManager.getUserPassword(), false).create(SelliscopeApiEndpointInterface.class);
        mCompositeDisposable = compositeDisposable;
    }

    public void loadAll(LoadCompleteListener loadCompleteListener) {

        this.loadProduct(new LoadCompleteListener() {
            @Override
            public void onLoadComplete() {
                loadCategory(new LoadCompleteListener() {
                    @Override
                    public void onLoadComplete() {
                        loadBrand(new LoadCompleteListener() {
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
                                                            public void onLoadFailed(String reason) {
                                                                if (loadCompleteListener != null)
                                                                    loadCompleteListener.onLoadFailed(reason);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onLoadFailed(String reason) {
                                                        if (loadCompleteListener != null)
                                                            loadCompleteListener.onLoadFailed(reason);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onLoadFailed(String reason) {
                                                if (loadCompleteListener != null)
                                                    loadCompleteListener.onLoadFailed(reason);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onLoadFailed(String reason) {
                                        if (loadCompleteListener != null)
                                            loadCompleteListener.onLoadFailed(reason);
                                    }
                                });

                            }

                            @Override
                            public void onLoadFailed(String reason) {
                                if (loadCompleteListener != null)
                                    loadCompleteListener.onLoadFailed(reason);
                            }
                        });

                    }

                    @Override
                    public void onLoadFailed(String reason) {
                        if (loadCompleteListener != null)
                            loadCompleteListener.onLoadFailed(reason);
                    }
                });
            }

            @Override
            public void onLoadFailed(String reason) {
                if (loadCompleteListener != null)
                    loadCompleteListener.onLoadFailed(reason);
            }
        });


    }

    public void updateAllData() {
        this.loadProduct(null);
        this.loadCategory(null);
        this.loadBrand(null);
        this.loadOutlet( null);
        this.loadOutletType(null);
        this.loadDistrict(null);
        this.loadThana(null);
    }

    public void loadProduct(@Nullable LoadCompleteListener loadCompleteListener) {
    /*    if (sessionManager.isLoggedIn()) {*/

            apiService.getProducts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Response<ProductResponse>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                        }

                        @Override
                        public void onSuccess(Response<ProductResponse> response) {
                            if (response.code() == 200) {
                                try {
                                    List<Product> products = null;
                                    if (response.body() != null) {
                                        products = response.body().getResult().getProducts();
                                    }
                                    //for removing previous data
                                    databaseHandler.removeProduct();
                                    if (products != null) {
                                        databaseHandler.addProduct(products);
                                    }
                                    if (loadCompleteListener != null)
                                        loadCompleteListener.onLoadComplete();

                                } catch (Exception e) {
                                    e.printStackTrace();

                                    if (loadCompleteListener != null)
                                        loadCompleteListener.onLoadFailed("Load Product Error: "+e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("tareq_test", "LoadLocalIntoBackground #122: onError:  " + e.getMessage());
                            if (loadCompleteListener != null)
                                loadCompleteListener.onLoadFailed("Load Product Error: "+e.getMessage());
                        }
                    });

     /*   }*/
    }

    public void loadCategory(@Nullable LoadCompleteListener loadCompleteListener) {
        apiService.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<CategoryResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<CategoryResponse> response) {
                        if (response.code() == 200) {

                            try {
                                List<CategoryResponse.CategoryResult> categories = null;
                                if (response.body() != null) {
                                    categories = response.body().result.categoryResults;
                                }
                                if (categories != null) {

                                    //for removing previous data
                                    databaseHandler.removeAllCategory();

                                    for (CategoryResponse.CategoryResult result : categories) {
                                        databaseHandler.addCategory(result.id, result.name);
                                    }
                                    if (loadCompleteListener != null)
                                        loadCompleteListener.onLoadComplete();
                                }
                            } catch (Exception e) {
                                Log.d("tareq_test", "Categories error: " + e.getMessage());
                                e.printStackTrace();
                                loadCompleteListener.onLoadFailed("load category: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("tareq_test", "Categories error: " + e.getMessage());
                        if (loadCompleteListener != null)
                            loadCompleteListener.onLoadFailed("load category: " + e.getMessage());

                    }
                });
      /*  call.enqueue(new Callback<CategoryResponse>() {
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
                            if (loadCompleteListener != null) loadCompleteListener.onLoadComplete();
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
                if (loadCompleteListener != null) loadCompleteListener.onLoadFailed("load category: "+t.getMessage());
            }
        });*/

    }

    public void loadBrand(@Nullable LoadCompleteListener loadCompleteListener) {
        apiService.getBrands().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<BrandResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<BrandResponse> response) {
                        if (response.code() == 200) {
                            try {
                                List<BrandResponse.BrandResult> brands = null;
                                if (response.body() != null) {
                                    brands = response.body().result.brandResults;
                                }
                                if (brands != null) {

                                    //for removing previous data
                                    databaseHandler.removeAllBrand();

                                    for (BrandResponse.BrandResult result : brands) {
                                        databaseHandler.addBrand(result.id, result.name);
                                    }
                                    if (loadCompleteListener != null)
                                        loadCompleteListener.onLoadComplete();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (loadCompleteListener != null) {
                                    loadCompleteListener.onLoadFailed("load brand: " + e.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadCompleteListener != null)
                            loadCompleteListener.onLoadFailed("load brand: " + e.getMessage());

                    }
                });

    }

    public void loadOutlet( LoadCompleteListener loadCompleteListener) {
        apiService.getOutlets().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ResponseBody> response) {
                        Gson gson = new Gson();
                        if (response.code() == 200) {
                            try {
                                Outlets getOutletListSuccessful = null;
                                if (response.body() != null) {
                                    getOutletListSuccessful = gson.fromJson(response.body().string(), Outlets.class);
                                }
                           /*     if (!fullUpdate) {
                                    // if (getOutletListSuccessful.outletsResult.outlets.size() != databaseHandler.getSizeOfOutlet()) {
                                    databaseHandler.removeOutlet();
                                    if (getOutletListSuccessful != null) {
                                        databaseHandler.addOutlet(getOutletListSuccessful.outletsResult.outlets);
                                    }
                                    if (mOnOutletUpdateComplete != null)
                                        mOnOutletUpdateComplete.onUpdateComplete();
                                    //  }
                                } else {
                                    databaseHandler.removeOutlet();*/

                                if (getOutletListSuccessful != null) {
                                  Executors.newSingleThreadExecutor().execute(() -> {

                                  });

                                }


                                if (loadCompleteListener != null)
                                    loadCompleteListener.onLoadComplete();
                            } catch (IOException e) {
                                e.printStackTrace();
                                if (loadCompleteListener != null)
                                    loadCompleteListener.onLoadFailed("Load Outlets: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadCompleteListener != null)
                            loadCompleteListener.onLoadFailed("Load Outlets: " + e.getMessage());
                    }
                });
        /*call.enqueue(new Callback<ResponseBody>() {
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
        });*/
    }


    public void saveOutletRoutePlan(List<RouteDetailsResponse.OutletItem> outletItemList) {
        //databaseHandler.removeOutlet();
        databaseHandler.updateOutletRoutePlan(outletItemList);
    }

    public void loadDistrict(LoadCompleteListener loadCompleteListener) {
        apiService.getDistricts().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<DistrictResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<DistrictResponse> response) {
                        Gson gson = new Gson();

                        if (response.code() == 200) {
                            try {
                                if (response.body() != null) {
                                    databaseHandler.setDistrict(response.body().getResult().getDistrict());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (loadCompleteListener != null) loadCompleteListener.onLoadComplete();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Response", e.toString());
                        if (loadCompleteListener != null)
                            loadCompleteListener.onLoadFailed("load district: " + e.getMessage());

                    }
                });

    }

    public void loadThana(LoadCompleteListener loadCompleListener) {
        apiService.getThanas().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ThanaResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ThanaResponse> response) {
                        Gson gson = new Gson();

                        if (response.code() == 200) {
                            try {
                                if (response.body() != null) {
                                    databaseHandler.setThana(response.body().getResult().getThana());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (loadCompleListener != null) loadCompleListener.onLoadComplete();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Response", e.toString());
                        if (loadCompleListener != null)
                            loadCompleListener.onLoadFailed("load thana: " + e.getMessage());

                    }
                });


    }

    public void loadOutletType(LoadCompleteListener loadCompleListener) {
        apiService.getOutletTypes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<OutletTypeResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        if (mCompositeDisposable != null) mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<OutletTypeResponse> response) {
                        Gson gson = new Gson();

                        if (response.code() == 200) {
                            try {
                                if (response.body() != null) {
                                    databaseHandler.setOutletType(response.body().getResult().getType());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (loadCompleListener != null) loadCompleListener.onLoadComplete();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Response", e.toString());
                        if (loadCompleListener != null)
                            loadCompleListener.onLoadFailed("load outlet type: " + e.getMessage());

                    }
                });
    }




    public interface LoadCompleteListener {
        void onLoadComplete();

        void onLoadFailed(String reason);
    }
}
