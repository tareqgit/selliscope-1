package com.humaclab.selliscope.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by leon on 5/18/17.
 */

public class Outlets implements Serializable {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public OutletsResult outletsResult;

    public static class OutletsResult implements Serializable {
        @SerializedName("outlet")
        public List<Outlet> outlets;
    }

    public static class Outlet implements Serializable {
        @SerializedName("id")
        public int outletId;
        @SerializedName("type")
        public String outletType;
        @SerializedName("name")
        public String outletName;
        @SerializedName("ClientID")
        private String ClientID;
        @SerializedName("owner")
        public String ownerName;
        @SerializedName("address")
        public String outletAddress;
        @SerializedName("district")
        public String district;
        @SerializedName("thana")
        public String thana;
        @SerializedName("phone")
        public String phone;
        @Nullable
        @SerializedName("img")
        public String outletImgUrl;
        @SerializedName("latitude")
        public Double outletLatitude;
        @SerializedName("longitude")
        public Double outletLongitude;
        @SerializedName("due")
        public String outletDue;
        @SerializedName("outlet_routeplan")
        private String outlet_routeplan;
        @SerializedName("refphone")
        public String outletrefPhoneNumber;

        private double distance_from_cur_location; //only for sorting lists according to distance

        public String getClientID() {
            return ClientID;
        }

        public void setClientID(String clientID) {
           if(clientID==null) this.ClientID="";
           else ClientID = clientID;
        }

        public String getOutlet_routeplan() {
            return outlet_routeplan;
        }

        public void setOutlet_routeplan(String outlet_routeplan) {
            if(outlet_routeplan==null) this.outlet_routeplan="0";
            else   this.outlet_routeplan = outlet_routeplan;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Outlet outlet = (Outlet) o;
            return outletId == outlet.outletId &&
                    Objects.equals(outletType, outlet.outletType) &&
                    Objects.equals(outletName, outlet.outletName) &&

                    Objects.equals(ownerName, outlet.ownerName) &&
                    Objects.equals(outletAddress, outlet.outletAddress) &&
                    Objects.equals(district, outlet.district) &&
                    Objects.equals(thana, outlet.thana) &&
                    Objects.equals(phone, outlet.phone) &&

                  /*  Objects.equals(outletLatitude, outlet.outletLatitude) &&
                    Objects.equals(outletLongitude, outlet.outletLongitude) &&*/
                         Objects.equals(outletImgUrl, outlet.outletImgUrl) &&
                        Objects.equals(outletDue, outlet.outletDue) &&

                    Objects.equals(outletrefPhoneNumber, outlet.outletrefPhoneNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(outletId, outletType, outletName,  ownerName, outletAddress, district, thana, phone, outletImgUrl, outletLatitude, outletLongitude, outletDue, outletrefPhoneNumber);
        }

        public double getDistance_from_cur_location() {
            return distance_from_cur_location;
        }

        public void setDistance_from_cur_location(double distance_from_cur_location) {
            this.distance_from_cur_location = distance_from_cur_location;
        }
    }
}
