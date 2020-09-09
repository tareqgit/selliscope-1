package com.sokrio.sokrio_classic.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 6/29/17.
 */

public class InspectionResponse implements Serializable {
    @SerializedName("error")
    public String error;
    @SerializedName("message")
    public String message;

    @SerializedName("inspection")
    public Inspection inspection;


    @Entity(tableName = "inspection")
    public static class Inspection implements Serializable {

        @PrimaryKey(autoGenerate = true)
        private int id;


        @SerializedName("outlet_id")
        public int outletID;
        @SerializedName("image")
        public String image;
        @SerializedName("quantity")
        public int quantity;
        @SerializedName("promotion_type")
        public String promotionType;
        @SerializedName("is_damaged")
        public boolean iDamaged;
        @SerializedName("condition")
        public String condition;


        @Ignore
        public Inspection() {
        }



        public Inspection(int outletID, String image, int quantity, String promotionType, boolean iDamaged, String condition) {
            this.outletID = outletID;
            this.image = image;
            this.quantity = quantity;
            this.promotionType = promotionType;
            this.iDamaged = iDamaged;
            this.condition = condition;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getOutletID() {
            return outletID;
        }

        public void setOutletID(int outletID) {
            this.outletID = outletID;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getPromotionType() {
            return promotionType;
        }

        public void setPromotionType(String promotionType) {
            this.promotionType = promotionType;
        }

        public boolean isiDamaged() {
            return iDamaged;
        }

        public void setiDamaged(boolean iDamaged) {
            this.iDamaged = iDamaged;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }
    }
}
