/*
 * Created by Tareq Islam on 10/27/19 4:34 PM
 *
 *  Last modified 10/27/19 4:34 PM
 */

package com.humaclab.lalteer.outstanding_payment.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.humaclab.lalteer.outstanding_payment.model.OutstandingPaymentBody;

import java.util.List;

/***
 * Created by mtita on 27,October,2019.
 */

@Dao
public interface OutstandingPaymentDao {

    @Insert
    void insertOutstandingPayment(OutstandingPaymentBody outstandingPaymentBody);


    @Query("SELECT * FROM OutstandingPaymentBody")
    List<OutstandingPaymentBody> getAllOutstandingPaymentList();

    @Delete
    void deleteOutstandingPayment(OutstandingPaymentBody outstandingPaymentBody);




}
