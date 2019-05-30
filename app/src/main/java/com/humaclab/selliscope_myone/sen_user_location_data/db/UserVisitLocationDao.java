/*
 * Created by Tareq Islam on 5/30/19 1:29 PM
 *
 *  Last modified 5/30/19 1:25 PM
 */

/*
 * Created by Tareq Islam on 5/30/19 1:25 PM
 *
 *  Last modified 5/30/19 1:25 PM
 */

package com.humaclab.selliscope_myone.sen_user_location_data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.humaclab.selliscope_myone.sen_user_location_data.model.UserVisit;

import java.util.List;

import io.reactivex.Flowable;

/***
 * Created by mtita on 30,May,2019.
 */
@Dao
public interface UserVisitLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserVisit visitList);


    @Query("SELECT * FROM user_visits")
   Flowable<List<UserVisit>> getUserVisitedLocations();

    @Query("DELETE  FROM user_visits")
    void deleteUserVisitedLoactions();

    @Query("DELETE  FROM user_visits  where (id =:id)")
    void deleteUserVisitedLoactions(String id);

}
