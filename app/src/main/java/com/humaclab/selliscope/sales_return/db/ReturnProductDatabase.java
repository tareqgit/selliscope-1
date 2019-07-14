package com.humaclab.selliscope.sales_return.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/***
 * Created by mtita on 11,July,2019.
 */
@Database(entities = {ReturnProductEntity.class}, version = 1)
public abstract class ReturnProductDatabase extends RoomDatabase {

    private static volatile RoomDatabase INSTANCE;

    public static RoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ReturnProductDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }



    private static ReturnProductDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                ReturnProductDatabase.class,
                "product-return-db"
        ).build();
    }


    public abstract ReturnProductDao returnProductDao();
}
