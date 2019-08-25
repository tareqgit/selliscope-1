package com.humaclab.selliscope.utility_db.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

/***
 * Created by mtita on 22,August,2019.
 */
@Dao
public interface UtilityDao {

  @Query("SELECT * FROM regular_performance_entity")
    List<RegularPerformanceEntity> getAllRegularPerformance();

  @Query("SELECT * FROM regular_performance_entity WHERE date = :date")
  List<RegularPerformanceEntity> getRegularPerformance(String date);


  @Query("UPDATE regular_performance_entity SET distance = :distance WHERE date = :date")
  void updateRegularPerformance(double distance, String date);


  @Query("UPDATE regular_performance_entity SET outlets_checked_in = :outlets_cheaked WHERE date = :date")
  void updateRegularPerformanceOutlets(String outlets_cheaked, String date);




  @Query("DELETE FROM regular_performance_entity")
  void deleteAllRegularPerformance();



    @Insert
    void insertRegularPerformance(RegularPerformanceEntity regularPerformanceEntity);

    @Delete
    void deleteRegularPerformance(RegularPerformanceEntity regularPerformanceEntity);

}
