package com.easyopstech.easyops.utility_db.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.easyopstech.easyops.model.InspectionResponse;
import com.easyopstech.easyops.model.UserLocation;
import com.easyopstech.easyops.utility_db.dao.Check_In_Dao;
import com.easyopstech.easyops.utility_db.dao.InspectionDao;
import com.easyopstech.easyops.utility_db.dao.UtilityDao;
import com.easyopstech.easyops.utility_db.model.RegularPerformanceEntity;


/***
 * Created by mtita on 22,August,2019.
 */
@Database(entities = {RegularPerformanceEntity.class, UserLocation.Visit.class, InspectionResponse.Inspection.class}, version = 4, exportSchema = false)

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
                .addMigrations(new Migration[]{MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4})
                .build();
    }

     // Array of all migrations



  private   static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `check_in` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lat` REAL NOT NULL, `long` REAL NOT NULL, `address` TEXT, `created_at` TEXT, `outlet_id` INTEGER NOT NULL, `img` TEXT, `comment` TEXT)");
        }
    };

    private static final Migration MIGRATION_2_3 =  new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `inspection` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `outletID` INTEGER NOT NULL, `image` TEXT, `quantity` INTEGER NOT NULL, `promotionType` TEXT, `iDamaged` INTEGER NOT NULL, `condition` TEXT)");
        }
    };
    private static final Migration MIGRATION_3_4 =  new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `check_in` ADD  'battery_status' TEXT");
        }
    };

    public abstract UtilityDao returnUtilityDao();

    public abstract Check_In_Dao returnCheckInDao();

    public abstract InspectionDao returnInspectionDao();
}
