
package com.humaclab.selliscope.geo_fence.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Data {

    @SerializedName("client_id")
    private String mClientId;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("getin_time")
    private Object mGetinTime;
    @SerializedName("getout_time")
    private String mGetoutTime;
    @SerializedName("id")
    private Long mId;
    @SerializedName("outlet_id")
    private String mOutletId;
    @SerializedName("updated_at")
    private String mUpdatedAt;
    @SerializedName("user_id")
    private String mUserId;

    public String getClientId() {
        return mClientId;
    }

    public void setClientId(String clientId) {
        mClientId = clientId;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public Object getGetinTime() {
        return mGetinTime;
    }

    public void setGetinTime(Object getinTime) {
        mGetinTime = getinTime;
    }

    public String getGetoutTime() {
        return mGetoutTime;
    }

    public void setGetoutTime(String getoutTime) {
        mGetoutTime = getoutTime;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getOutletId() {
        return mOutletId;
    }

    public void setOutletId(String outletId) {
        mOutletId = outletId;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

}
