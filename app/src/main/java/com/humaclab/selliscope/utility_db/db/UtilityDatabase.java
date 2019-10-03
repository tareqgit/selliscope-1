package com.humaclab.selliscope.utility_db.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.humaclab.selliscope.model.UserLocation;
import com.humaclab.selliscope.utility_db.dao.Check_In_Dao;
import com.humaclab.selliscope.utility_db.dao.UtilityDao;
import com.humaclab.selliscope.utility_db.model.RegularPerformanceEntity;


/***
 * Created by mtita on 22,August,2019.
 */
@Database(entities = {RegularPerformanceEntity.class, UserLocation.Visit.class}, version = 2)

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
        return Room.databaseBuilder(context.getApplicationContext(),UtilityDatabase.class,"utility-db")
                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `check_in` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lat` REAL NOT NULL, `long` REAL NOT NULL, `address` TEXT, `created_at` TEXT, `outlet_id` INTEGER NOT NULL, `img` TEXT, `comment` TEXT)");
        }
    };

    public abstract UtilityDao returnUtilityDao();

    public abstract Check_In_Dao returnCheckInDao();
}
