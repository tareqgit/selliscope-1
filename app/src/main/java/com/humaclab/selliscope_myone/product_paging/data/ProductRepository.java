/*
 * Created by Tareq Islam on 5/23/19 1:53 PM
 *
 *  Last modified 5/23/19 1:36 PM
 */

/*
 * Created by Tareq Islam on 5/22/19 2:47 PM
 *
 *  Last modified 5/22/19 2:47 PM
 */

package com.humaclab.selliscope_myone.product_paging.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.humaclab.selliscope_myone.SelliscopeApiEndpointInterface;
import com.humaclab.selliscope_myone.product_paging.db.ProductLocalCache;
import com.humaclab.selliscope_myone.product_paging.model.ProductSearchResult;
import com.humaclab.selliscope_myone.product_paging.model.ProductsItem;
import com.humaclab.selliscope_myone.utils.Constants;

/***
 * Created by mtita on 22,May,2019.
 */
public class ProductRepository {

    //Constant for the number of items to be loaded at once from the DataSource by the PagedList
    private static final int DATABASE_PAGE_SIZE = 40;
    SelliscopeApiEndpointInterface apiService;
    private ProductLocalCache mLocalCache;

    public ProductRepository(SelliscopeApiEndpointInterface apiService, ProductLocalCache localCache) {
        this.apiService = apiService;
        mLocalCache = localCache;
    }

    /**
     * Search repositories whose names match the query.
     */
    public ProductSearchResult search(String query) {
        Log.d("tareq_test", "search: NewQuery");

        //Get Data source factory from the local cache
        DataSource.Factory<Integer, ProductsItem> reposByName = mLocalCache.productsByName(query);

        //Construct the boundary callback
        ProductBoundaryCallback boundaryCallback = new ProductBoundaryCallback(query, apiService, mLocalCache);

        LiveData<String> networkErrors = boundaryCallback.getNetworkError();

        LiveData<Constants.NETWORK_STATE> network_stateLiveData = boundaryCallback.getNetworkState();

        // Set the Page size for the Paged list
        PagedList.Config pagedConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(DATABASE_PAGE_SIZE)
                .setPrefetchDistance(60)
                .build();

        // Get the Live Paged list
        LiveData<PagedList<ProductsItem>> data = new LivePagedListBuilder<>(reposByName, pagedConfig)
                .setBoundaryCallback(boundaryCallback)
                .build();

        // Get the Search result with the network errors exposed by the boundary callback
        return new ProductSearchResult(data, networkErrors,network_stateLiveData);
    }
}
