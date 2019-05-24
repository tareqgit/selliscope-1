/*
 * Created by Tareq Islam on 5/22/19 3:49 PM
 *
 *  Last modified 5/22/19 3:49 PM
 */

package com.humaclab.selliscope_myone.outlet_paging;

import android.content.Context;
import android.support.annotation.NonNull;

import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.SelliscopeApplication;
import com.humaclab.selliscope_myone.outlet_paging.data.OutletRepository;
import com.humaclab.selliscope_myone.outlet_paging.db.OutletDatabase;
import com.humaclab.selliscope_myone.outlet_paging.db.OutletLocalCache;
import com.humaclab.selliscope_myone.outlet_paging.ui.OutletViewModelFactory;
import com.humaclab.selliscope_myone.utils.SessionManager;

import java.util.concurrent.Executors;

/***
 * Created by mtita on 22,May,2019.
 */
public class OutletInjection {
    /**
     * Creates an instance of  based on the database DAO.
     */
    @NonNull
    private static OutletLocalCache provideCache(Context context) {
        OutletDatabase repoDatabase = (OutletDatabase) OutletDatabase.getInstance(context);
        return new OutletLocalCache(repoDatabase.reposDao(), Executors.newSingleThreadExecutor());
    }


    @NonNull
    private static OutletRepository provideOutletRepository(Context context) {
        SessionManager    sessionManager = new SessionManager(context);
        return new OutletRepository(SelliscopeApplication.getRetrofitInstance(sessionManager.getUserEmail(),sessionManager.getUserPassword(),false).create(SelliscopeApiEndpointInterface.class)
                , provideCache(context));
    }



    @NonNull
    public static OutletViewModelFactory provideViewModelFactory(Context context) {
        return new OutletViewModelFactory(provideOutletRepository(context));
    }
}
