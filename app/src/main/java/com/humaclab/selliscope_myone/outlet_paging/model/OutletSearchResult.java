/*
 * Created by Tareq Islam on 5/22/19 2:49 PM
 *
 *  Last modified 5/22/19 2:49 PM
 */

package com.humaclab.selliscope_myone.outlet_paging.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.humaclab.selliscope_myone.outlet_paging.api.OutletItem;
import com.humaclab.selliscope_myone.utils.Constants;

/***
 * Created by mtita on 22,May,2019.
 */
public class OutletSearchResult {

    //LiveData for search Result
    private final LiveData<PagedList<OutletItem>> data;

    //LiveData for Network Errors
    private final LiveData<String> networkError;

    //LiveData for Network Errors
    private final LiveData<Constants.NETWORK_STATE> networkState;

    public OutletSearchResult(LiveData<PagedList<OutletItem>> data, LiveData<String> networkError, LiveData<Constants.NETWORK_STATE> networkState) {
        this.data = data;
        this.networkError = networkError;
        this.networkState = networkState;
    }


    public LiveData<PagedList<OutletItem>> getData() {
        return data;
    }

    public LiveData<String> getNetworkError() {
        return networkError;
    }

    public LiveData<Constants.NETWORK_STATE> getNetworkState() {
        return networkState;
    }
}
