
/*
 * Created by Tareq Islam on 4/3/19 2:38 PM
 *
 *  Last modified 3/28/19 10:52 AM
 */

package com.humaclab.lalteer.model.performance.paymentsModel;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Datum {

    @SerializedName("outlet_name")
    private String mOutletName;
    @SerializedName("total_amount")
    private Double mTotalAmount;

    public String getOutletName() {
        return mOutletName;
    }

    public void setOutletName(String outletName) {
        mOutletName = outletName;
    }

    public Double getTotalAmount() {
        return mTotalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        mTotalAmount = totalAmount;
    }

}
