/*
 * Created by Tareq Islam on 5/22/19 2:02 PM
 *
 *  Last modified 5/22/19 2:02 PM
 */

package com.humaclab.selliscope_myone.outlet_paging.db;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.humaclab.selliscope_myone.outlet_paging.api.OutletItem;

import java.util.List;

/***
 * Created by mtita on 22,May,2019.
 */
@Dao
public interface OutletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<OutletItem> posts);


    // Do a similar query as the search API:
    // Look for repos that contain the query string in the name or in the description
    // and order those results descending, by the number of stars and then by name ascending
    @Query("SELECT * FROM outlets where (name LIKE :queryString)")
    DataSource.Factory<Integer, OutletItem> outletsByName(String queryString);
}
