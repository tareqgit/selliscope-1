/*
 * Created by Tareq Islam on 5/23/19 2:13 PM
 *
 *  Last modified 5/23/19 2:13 PM
 */


package com.humaclab.selliscope_myone.product_paging;

import android.content.Context;
import android.support.annotation.NonNull;

import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.outlet_paging.data.OutletRepository;
import com.humaclab.selliscope_myone.outlet_paging.db.OutletDatabase;
import com.humaclab.selliscope_myone.outlet_paging.db.OutletLocalCache;
import com.humaclab.selliscope_myone.outlet_paging.ui.OutletViewModelFactory;
import com.humaclab.selliscope_myone.product_paging.data.ProductRepository;
import com.humaclab.selliscope_myone.product_paging.db.ProductDatabase;
import com.humaclab.selliscope_myone.product_paging.db.ProductLocalCache;
import com.humaclab.selliscope_myone.product_paging.ui.ProductViewModelFactory;
import com.humaclab.selliscope_myone.utils.SessionManager;

import java.util.concurrent.Executors;

/***
 * Created by mtita on 22,May,2019.
 */
public class ProductInjection {
    /**
     * Creates an instance of  based on the database DAO.
     */
    @NonNull
    private static ProductLocalCache provideCache(Context context) {
        ProductDatabase repoDatabase = (ProductDatabase) ProductDatabase.getInstance(context);
        return new ProductLocalCache(repoDatabase.priductsDao(), Executors.newSingleThreadExecutor());
    }


    @NonNull
    private static ProductRepository provideOutletRepository(Context context) {
        SessionManager    sessionManager = new SessionManager(context);
        return new ProductRepository(SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),sessionManager.getUserPassword(),false).create(SelliscopeApiEndpointInterface.class)
                , provideCache(context));
    }



    @NonNull
    public static ProductViewModelFactory provideViewModelFactory(Context context) {
        return new ProductViewModelFactory(provideOutletRepository(context));
    }
}
