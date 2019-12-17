
package com.easyopstech.easyops.model.performance.payments_model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

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
