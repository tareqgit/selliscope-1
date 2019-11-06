/*
 * Created by Tareq Islam on 10/28/19 2:34 PM
 *
 *  Last modified 10/28/19 2:34 PM
 */

package com.humaclab.lalteer.outstanding_payment.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/***
 * Created by mtita on 28,October,2019.
 */
@Entity
public class OutstandingPaymentLedger {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private int id;


    @SerializedName("outlet_id")
    private int outlet_id;  //this for api path not for body


    @SerializedName("paid")
    private double paid;

    @SerializedName("due")
    private double due;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(int outlet_id) {
        this.outlet_id = outlet_id;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }

    public OutstandingPaymentLedger( int outlet_id, double paid, double due) {

        this.outlet_id = outlet_id;
        this.paid = paid;
        this.due = due;
    }
}
