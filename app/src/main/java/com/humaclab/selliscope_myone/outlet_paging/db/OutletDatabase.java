/*
 * Created by Tareq Islam on 5/22/19 2:19 PM
 *
 *  Last modified 5/22/19 2:19 PM
 */

package com.humaclab.selliscope_myone.outlet_paging.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.humaclab.selliscope_myone.outlet_paging.api.OutletItem;

/***
 * Created by mtita on 22,May,2019.
 */
@Database(entities = {OutletItem.class}, version = 1, exportSchema = false)
public abstract class OutletDatabase extends RoomDatabase {

    private static volatile RoomDatabase INSTANCE;

    public static RoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (OutletDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }


    private static OutletDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                OutletDatabase.class,
                "Outlet.db"
        ).build();
    }

    //RepoDao is a DAO class annotated with @Dao
    public abstract OutletDao reposDao();
}
