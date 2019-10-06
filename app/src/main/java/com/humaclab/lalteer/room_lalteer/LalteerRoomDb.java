/*
 * Created by Tareq Islam on 9/30/19 4:55 PM
 *
 *  Last modified 9/30/19 4:55 PM
 */

package com.humaclab.lalteer.room_lalteer;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.humaclab.lalteer.performance.claim.model.Claim;
import com.humaclab.lalteer.performance.claim.model.ReasonItem;
import com.humaclab.lalteer.performance.claim.repository.dao.ClaimDAO;
import com.humaclab.lalteer.performance.claim.repository.dao.ClaimReasonDAO;

/***
 * Created by mtita on 30,September,2019.
 */

@Database(entities = {ReasonItem.class, Claim.class}, version = 1, exportSchema = false)
public abstract class LalteerRoomDb extends RoomDatabase {

    private static volatile RoomDatabase INSTANCE;


    public static RoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LalteerRoomDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }


    private static LalteerRoomDb buildDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                LalteerRoomDb.class,
                "lalteer-db"
        ).build();
    }

    public abstract ClaimReasonDAO returnClaimReasonsDao();
    public abstract ClaimDAO returnClaimDao();

}
