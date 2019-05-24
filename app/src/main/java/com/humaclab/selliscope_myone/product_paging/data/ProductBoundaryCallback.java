/*
 * Created by Tareq Islam on 5/23/19 1:53 PM
 *
 *  Last modified 5/23/19 1:36 PM
 */

/*
 * Created by Tareq Islam on 5/22/19 2:28 PM
 *
 *  Last modified 5/22/19 2:28 PM
 */

package com.humaclab.selliscope_myone.product_paging.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.util.Log;

import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;

import com.humaclab.selliscope_myone.product_paging.api.ProductServiceApi;
import com.humaclab.selliscope_myone.product_paging.db.ProductLocalCache;
import com.humaclab.selliscope_myone.product_paging.model.ProductsItem;
import com.humaclab.selliscope_myone.utils.Constants;

import java.util.List;

/***
 * Created by mtita on 22,May,2019.
 */
public class ProductBoundaryCallback extends PagedList.BoundaryCallback<ProductsItem>  implements ProductServiceApi.ApiCallback {


    // Constant for the Number of items in a page to be requested from the Github API
    private static final int NETWORK_PAGE_SIZE = 100;
    private String query;

    private SelliscopeApiEndpointInterface apiService;
    private ProductLocalCache mProductLocalCache;

    // Keep the last requested page. When the request is successful, increment the page number.
    private int lastRequestedPage = 1;
    // Avoid triggering multiple requests in the same time
    private boolean isRequestInProgress = false;

    //LiveData of network error
    private MutableLiveData<String> networkError = new MutableLiveData<>();

    //LiveData of network State
    private MutableLiveData<Constants.NETWORK_STATE> networkState= new MutableLiveData<>();


    public ProductBoundaryCallback(String query, SelliscopeApiEndpointInterface apiService, ProductLocalCache productLocalCache) {
        this.query = query;
        this.apiService = apiService;
        mProductLocalCache = productLocalCache;
    }

    public MutableLiveData<String> getNetworkError() {
        return networkError;
    }

    public MutableLiveData<Constants.NETWORK_STATE> getNetworkState() {
        return networkState;
    }

    /**
     * Method to request data from Github API for the given search query
     * and save the results.
     *
     * @param query The query to use for retrieving the repositories from API
     */
    private void requestAndSaveData(String query) {
        //Exiting if the request is in progress
        if(isRequestInProgress) return;

        //set to true as we are starting the network request
        isRequestInProgress = true;

        networkState.postValue(Constants.NETWORK_STATE.LOADING);
        //CAlling the client API to retrieve the repos for the given query
        ProductServiceApi.searchProducts(apiService, query, lastRequestedPage, NETWORK_PAGE_SIZE, this);
    }

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    @Override
    public void onZeroItemsLoaded() {
        Log.d("tareq_test", "onZeorItem loaded: Started");
        requestAndSaveData(query);

    }


    /**
     * Called when the item at the end of the PagedList has been loaded, and access has
     * occurred within {@link PagedList.Config#prefetchDistance} of it.
     * <p>
     * No more data will be appended to the PagedList after this item.
     *
     * @param itemAtEnd The first item of PagedList
     */
    @Override
    public void onItemAtEndLoaded(@NonNull ProductsItem itemAtEnd) {
        Log.d("tareq_test", "onItemEndLoaded: Started");
        requestAndSaveData(query);

    }



    @Override
    public void onSuccess(List<ProductsItem> items) {

        networkState.postValue(Constants.NETWORK_STATE.LOADED);
        //Inserting records in the database thread
        mProductLocalCache.insert(items, () -> {
            //Updating the last requested page number when the request was successful
            //and the results were inserted successfully
            lastRequestedPage++;
            //Marking the request progress as completed
            isRequestInProgress = false;
        });
    }

    @Override
    public void onError(String errorMessage) {
        //Update the Network error to be shown
        networkError.postValue(errorMessage);
        //Mark the request progress as completed
        isRequestInProgress = false;
    }
}
