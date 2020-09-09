package com.sokrio.sokrio_classic.utility_db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sokrio.sokrio_classic.model.InspectionResponse;

import java.util.List;

/***
 * Created by mtita on 15,October,2019.
 */

@Dao
public interface InspectionDao  {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addInspection(InspectionResponse.Inspection inspection);

    @Delete
    void deleteInspection(InspectionResponse.Inspection inspection);

    @Query("SELECT * FROM inspection")
    List<InspectionResponse.Inspection> getInspections();

    @Query("DELETE FROM inspection")
    void deleteAllInspections();
}
