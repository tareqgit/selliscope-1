/*
 * Created by Tareq Islam on 5/23/19 1:44 PM
 *
 *  Last modified 5/23/19 1:36 PM
 */

/*
 * Created by Tareq Islam on 5/22/19 2:19 PM
 *
 *  Last modified 5/22/19 2:19 PM
 */

package com.humaclab.selliscope_myone.product_paging.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.humaclab.selliscope_myone.product_paging.model.ProductsItem;

/***
 * Created by mtita on 22,May,2019.
 */
@Database(entities = {ProductsItem.class}, version = 1, exportSchema = false)
public abstract class ProductDatabase extends RoomDatabase {

    private static volatile RoomDatabase INSTANCE;

    public static RoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ProductDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }


    private static ProductDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                ProductDatabase.class,
                "Product.db"
        ).build();
    }

    //RepoDao is a DAO class annotated with @Dao
    public abstract ProductDao priductsDao();
}
