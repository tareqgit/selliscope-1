/*
 * Created by Tareq Islam on 10/28/19 2:35 PM
 *
 *  Last modified 10/28/19 2:35 PM
 */

package com.humaclab.lalteer.outstanding_payment.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.humaclab.lalteer.outstanding_payment.model.OutstandingPaymentLedger;

import java.util.List;


/***
 * Created by mtita on 28,October,2019.
 */
@Dao
public interface OutstandingPaymentLedgerDao {


    @Insert
    void insertOutstandingPaymentLedger( OutstandingPaymentLedger outstandingPaymentLedger);


    @Update
    void updateOutstandingPaymentLedger( OutstandingPaymentLedger outstandingPaymentLedger);

    @Query("SELECT * FROM outstandingpaymentledger")
    List<OutstandingPaymentLedger> getAllOutstandingPaymentLedgerList();


    @Query("SELECT * FROM outstandingpaymentledger WHERE outlet_id = :outletId")
    OutstandingPaymentLedger getOutstandingPaymentLedger(int outletId);


    @Delete
    void deleteOutstandingPaymentLedger( OutstandingPaymentLedger outstandingPaymentLedger);
}
