/*
 * Created by Tareq Islam on 9/30/19 4:55 PM
 *
 *  Last modified 9/30/19 4:55 PM
 */

package com.humaclab.lalteer.room_lalteer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.humaclab.lalteer.outstanding_payment.dao.OutstandingPaymentLedgerDao;
import com.humaclab.lalteer.outstanding_payment.model.OutstandingPaymentBody;
import com.humaclab.lalteer.outstanding_payment.dao.OutstandingPaymentDao;
import com.humaclab.lalteer.outstanding_payment.model.OutstandingPaymentLedger;
import com.humaclab.lalteer.performance.claim.model.Claim;
import com.humaclab.lalteer.performance.claim.model.ReasonItem;
import com.humaclab.lalteer.performance.claim.repository.dao.ClaimDAO;
import com.humaclab.lalteer.performance.claim.repository.dao.ClaimReasonDAO;

/***
 * Created by mtita on 30,September,2019.
 */

@Database(entities = {ReasonItem.class, Claim.class, OutstandingPaymentBody.class, OutstandingPaymentLedger.class}, version = 2, exportSchema = false)
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
        return Room.databaseBuilder(context.getApplicationContext(),  LalteerRoomDb.class, "lalteer-db")
                .fallbackToDestructiveMigration()
                .addMigrations(new Migration[]{MIGRATION_1_2})
                .build();
    }


    private   static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(" CREATE TABLE `OutstandingPaymentBody` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `outlet_id` INTEGER NOT NULL, `amount` REAL NOT NULL, `paymentDate` TEXT, `img` TEXT, `comments` TEXT, `type` INTEGER NOT NULL, `cheque_no` TEXT, `bank_name` TEXT, `deposit_to` TEXT, `deposit_from` TEXT, `depositedSlipNumber` TEXT, `cheque_date` TEXT, PRIMARY KEY(`id`))");
            database.execSQL(" CREATE TABLE `OutstandingPaymentLedger` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `outlet_id` INTEGER NOT NULL, `paid` REAL NOT NULL, `due` REAL NOT NULL, PRIMARY KEY(`id`))");

        }
    };

    public abstract ClaimReasonDAO returnClaimReasonsDao();
    public abstract ClaimDAO returnClaimDao();
    public abstract OutstandingPaymentDao returnOutstandingPaymentDao();
    public abstract OutstandingPaymentLedgerDao returnOutstandingPaymentLadger();


}
