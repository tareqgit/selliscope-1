/*
 * Created by Tareq Islam on 10/1/19 12:52 PM
 *
 *  Last modified 10/1/19 12:48 PM
 */

/*
 * Created by Tareq Islam on 9/30/19 4:56 PM
 *
 *  Last modified 9/30/19 4:56 PM
 */

package com.humaclab.lalteer.performance.claim.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.humaclab.lalteer.performance.claim.model.ReasonItem;

import java.util.List;

/***
 * Created by mtita on 30,September,2019.
 */
@Dao
public interface ClaimReasonDAO {


    @Query("SELECT * FROM reason")
    LiveData<List<ReasonItem>> getAllReason();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReasonItem reasonItem);

    @Query("DELETE  FROM reason")
    void deleteAll();
}
