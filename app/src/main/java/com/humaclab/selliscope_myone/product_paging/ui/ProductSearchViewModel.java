/*
 * Created by Tareq Islam on 5/23/19 2:05 PM
 *
 *  Last modified 5/23/19 1:36 PM
 */

/*
 * Created by Tareq Islam on 5/22/19 2:52 PM
 *
 *  Last modified 5/22/19 2:52 PM
 */

package com.humaclab.selliscope_myone.product_paging.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;

import com.humaclab.selliscope_myone.outlet_paging.data.OutletRepository;
import com.humaclab.selliscope_myone.outlet_paging.model.OutletItem;
import com.humaclab.selliscope_myone.outlet_paging.model.OutletSearchResult;
import com.humaclab.selliscope_myone.product_paging.data.ProductRepository;
import com.humaclab.selliscope_myone.product_paging.model.ProductSearchResult;
import com.humaclab.selliscope_myone.product_paging.model.ProductsItem;
import com.humaclab.selliscope_myone.utils.Constants;

/***
 * Created by mtita on 22,May,2019.
 */
public class ProductSearchViewModel extends ViewModel {

    private ProductRepository mProductRepository;

  //For quries
    private MutableLiveData<String> queryLiveData = new MutableLiveData<>();


    //Applying transformation to get RepoSearchResult for the given Search Query
    private LiveData<ProductSearchResult> repoReuslt = Transformations.map(queryLiveData,
            inputQuery -> mProductRepository.search(inputQuery)
    );

    //Applying transformation to get Live PagedList<Repo> from the RepoSearchResult
    private LiveData<PagedList<ProductsItem>> repos = Transformations.switchMap(repoReuslt,
            repoSearchResult -> repoSearchResult.getData()
    );

    //Applying transformation to get Live Network Errors from the RepoSearchResult
    private LiveData<String> networkErrors = Transformations.switchMap(repoReuslt,
            repoSearchResult -> repoSearchResult.getNetworkError()
    );

    //Applying transformation to get Live Network State from the RepoSearchResult
    private LiveData<Constants.NETWORK_STATE> networkStates = Transformations.switchMap(repoReuslt,
            repoSearchResult -> repoSearchResult.getNetworkState()
    );

    public ProductSearchViewModel(ProductRepository productRepository) {
        mProductRepository = productRepository;
    }

    public LiveData<PagedList<ProductsItem>> getRepos() {
        return repos;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    public LiveData<Constants.NETWORK_STATE> getNetworkStates() {
        return networkStates;
    }

    /**
     * Search a repository based on a query string.
     */
    public void searchProducts(String queryString) {
        queryLiveData.postValue(queryString);

    }

    /**
     * Get the last query value.
     */
    @Nullable
    String lastQueryValue() {
        return queryLiveData.getValue();
    }
}
