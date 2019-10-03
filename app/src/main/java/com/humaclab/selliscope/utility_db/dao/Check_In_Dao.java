package com.humaclab.selliscope.utility_db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.humaclab.selliscope.model.UserLocation;

import java.util.List;

/***
 * Created by mtita on 02,October,2019.
 */
@Dao
public interface Check_In_Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCheck_In(UserLocation.Visit visit);

    @Delete
    void deleteCheck_In(UserLocation.Visit visit);

    @Query("SELECT * FROM check_in")
    List<UserLocation.Visit> getCheckInList();

    @Query("DELETE FROM check_in")
    void deleteAllCheck_In();


}
