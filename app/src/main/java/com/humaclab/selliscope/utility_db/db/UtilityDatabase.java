package com.humaclab.selliscope.utility_db.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


/***
 * Created by mtita on 22,August,2019.
 */
@Database(entities = {RegularPerformanceEntity.class}, version = 1)

public abstract class UtilityDatabase extends RoomDatabase {


    private static volatile RoomDatabase INSTANCE;


    public static RoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UtilityDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }

    private static UtilityDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                UtilityDatabase.class,
                "utility-db"
        ).build();
    }

    public abstract UtilityDao returnUtilityDao();
}
