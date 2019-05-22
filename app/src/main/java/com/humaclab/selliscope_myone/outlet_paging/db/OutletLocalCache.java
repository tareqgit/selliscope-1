/*
 * Created by Tareq Islam on 5/22/19 2:23 PM
 *
 *  Last modified 5/22/19 2:23 PM
 */

package com.humaclab.selliscope_myone.outlet_paging.db;

import android.arch.paging.DataSource;
import android.util.Log;

import com.humaclab.selliscope_myone.outlet_paging.api.OutletItem;

import java.util.List;
import java.util.concurrent.Executor;

/***
 * Created by mtita on 22,May,2019.
 */
public class OutletLocalCache {

    //Dao for Repo Entity
    private OutletDao outletDao;
    //Single Thread Executor for database operations
    private Executor ioExecutor;

    public OutletLocalCache(OutletDao outletDao, Executor ioExecutor) {
        this.outletDao = outletDao;
        this.ioExecutor = ioExecutor;
    }


    /**
     * Insert a list of repos in the database, on a background thread.
     */
    public void insert(final List<OutletItem> outlets, final InsertCallback insertCallback) {
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("tareq_test", "insert: inserting " + outlets.size());

                outletDao.insert(outlets);

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
    public DataSource.Factory<Integer, OutletItem> reposByName(String name) {
        // appending '%' so we can allow other characters to be before and after the query string
        return outletDao.outletsByName("%" + name.replace(' ', '%') + "%");
    }


    public interface InsertCallback {
        /**
         * Callback method invoked when the insert operation
         * completes.
         */
        void insertFinished();
    }
}
