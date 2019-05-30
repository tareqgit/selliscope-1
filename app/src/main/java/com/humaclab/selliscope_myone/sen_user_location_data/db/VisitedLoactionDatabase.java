/*
 * Created by Tareq Islam on 5/30/19 1:33 PM
 *
 *  Last modified 5/30/19 1:33 PM
 */

package com.humaclab.selliscope_myone.sen_user_location_data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.humaclab.selliscope_myone.product_paging.db.ProductDao;
import com.humaclab.selliscope_myone.product_paging.db.ProductDatabase;
import com.humaclab.selliscope_myone.sen_user_location_data.model.UserVisit;

/***
 * Created by mtita on 30,May,2019.
 */
@Database(entities = {UserVisit.class}, version = 1, exportSchema = false)
public abstract class VisitedLoactionDatabase extends RoomDatabase {

    private static volatile RoomDatabase INSTANCE;

    public static RoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (VisitedLoactionDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }

    private static VisitedLoactionDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                VisitedLoactionDatabase.class,
                "VisitedLocations.db"
        ).build();
    }

    //RepoDao is a DAO class annotated with @Dao
    public abstract UserVisitLocationDao mUserVisitLocationDao();
}
