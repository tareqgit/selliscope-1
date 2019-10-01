/*
 * Created by Tareq Islam on 10/1/19 1:05 PM
 *
 *  Last modified 10/1/19 1:05 PM
 */

package com.humaclab.lalteer.performance.claim.repository.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.humaclab.lalteer.performance.claim.model.Claim;

import java.util.List;

/***
 * Created by mtita on 01,October,2019.
 */
@Dao
public interface ClaimDAO {

    @Insert
    void insertClaim(Claim claim);


    @Query("SELECT * FROM Claim")
    List<Claim> getClaimList();


    @Delete
    void deleteClaim(Claim claim);
}
