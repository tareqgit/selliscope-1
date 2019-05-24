/*
 * Created by Tareq Islam on 5/23/19 1:44 PM
 *
 *  Last modified 5/23/19 1:36 PM
 */
package com.humaclab.selliscope_myone.product_paging.db;

import android.arch.paging.DataSource;
import android.util.Log;

import com.humaclab.selliscope_myone.product_paging.model.ProductsItem;

import java.util.List;
import java.util.concurrent.Executor;

/***
 * Created by mtita on 22,May,2019.
 */
public class ProductLocalCache {

    //Dao for Repo Entity
    private ProductDao mProductDao;
    //Single Thread Executor for database operations
    private Executor ioExecutor;

    public ProductLocalCache(ProductDao productDao, Executor ioExecutor) {
        this.mProductDao = productDao;
        this.ioExecutor = ioExecutor;
    }


    /**
     * Insert a list of repos in the database, on a background thread.
     */
    public void insert(final List<ProductsItem> products, final InsertCallback insertCallback) {
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("tareq_test", "insert: inserting " + products.size());

                mProductDao.insert(products);

                insertCallback.insertFinished();
            }
        });
    }


    /**
     * Request a DataSource.Factory<Integer, Repo> from the Dao, based on a repo name. If the name contains
     * multiple words separated by spaces, then we're emulating the GitHub API behavior and allow
     * any characters between the words.
     *
     * @param name repository name
     */
    public DataSource.Factory<Integer, ProductsItem> productsByName(String name) {
        // appending '%' so we can allow other characters to be before and after the query string
        return mProductDao.productsByName("%" + name.replace(' ', '%') + "%");
    }


    public interface InsertCallback {
        /**
         * Callback method invoked when the insert operation
         * completes.
         */
        void insertFinished();
    }
}
