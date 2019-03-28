
package com.humaclab.selliscope.model.performance.paymentsModel;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Datum {

    @SerializedName("outlet_name")
    private String mOutletName;
    @SerializedName("total_amount")
    private Long mTotalAmount;

    public String getOutletName() {
        return mOutletName;
    }

    public void setOutletName(String outletName) {
        mOutletName = outletName;
    }

    public Long getTotalAmount() {
        return mTotalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        mTotalAmount = totalAmount;
    }

}
