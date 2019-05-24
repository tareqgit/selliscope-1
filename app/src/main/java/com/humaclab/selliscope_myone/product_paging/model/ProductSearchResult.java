/*
 * Created by Tareq Islam on 5/23/19 1:57 PM
 *
 *  Last modified 5/23/19 1:36 PM
 */


package com.humaclab.selliscope_myone.product_paging.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.humaclab.selliscope_myone.outlet_paging.model.OutletItem;
import com.humaclab.selliscope_myone.utils.Constants;

/***
 * Created by mtita on 22,May,2019.
 */
public class ProductSearchResult {

    //LiveData for search Result
    private final LiveData<PagedList<ProductsItem>> data;

    //LiveData for Network Errors
    private final LiveData<String> networkError;

    //LiveData for Network Errors
    private final LiveData<Constants.NETWORK_STATE> networkState;

    public ProductSearchResult(LiveData<PagedList<ProductsItem>> data, LiveData<String> networkError, LiveData<Constants.NETWORK_STATE> networkState) {
        this.data = data;
        this.networkError = networkError;
        this.networkState = networkState;
    }


    public LiveData<PagedList<ProductsItem>> getData() {
        return data;
    }

    public LiveData<String> getNetworkError() {
        return networkError;
    }

    public LiveData<Constants.NETWORK_STATE> getNetworkState() {
        return networkState;
    }
}
