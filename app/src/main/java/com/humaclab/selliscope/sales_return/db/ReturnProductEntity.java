package com.humaclab.selliscope.sales_return.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/***
 * Created by mtita on 11,July,2019.
 */

@Entity(tableName = "return_product_entity")
public class ReturnProductEntity implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int returnProductId_db;

    @SerializedName("order_return_id") //this id is for relation between order table and SalesReturn table
    public int order_return_id;

    @SerializedName("outlet_id")
    public int outlet_id;
    @SerializedName("product_id")
    public int product_id;
    @SerializedName("variant_row")
    public int variant_row;
    @SerializedName("quantity")
    public int quantity;
    @SerializedName("rate")
    public double rate;
    @SerializedName("sku")
    public String sku;
    @SerializedName("cause")
    public int cause;
    @SerializedName("return_date")
    public String return_date;
    @SerializedName("godown_id")
    public int godown_id;


    public ReturnProductEntity() {
    }

    private ReturnProductEntity(Builder builder) {
        returnProductId_db = builder.returnProductId_db;
        order_return_id = builder.order_return_id;
        outlet_id = builder.outlet_id;
        product_id = builder.product_id;
        variant_row = builder.variant_row;
        quantity = builder.quantity;
        rate = builder.rate;
        sku = builder.sku;
        cause = builder.cause;
        return_date = builder.return_date;
        godown_id = builder.godown_id;
    }


    public static final class Builder {
        private int returnProductId_db;
        private int order_return_id;
        private int outlet_id;
        private int product_id;
        private int variant_row;
        private int quantity;
        private double rate;
        private String sku;
        private int cause;
        private String return_date;
        private int godown_id;

        public Builder() {
        }

        public Builder withReturnProductId_db(int val) {
            returnProductId_db = val;
            return this;
        }

        public Builder withOrder_return_id(int val) {
            order_return_id = val;
            return this;
        }

        public Builder withOutlet_id(int val) {
            outlet_id = val;
            return this;
        }

        public Builder withProduct_id(int val) {
            product_id = val;
            return this;
        }

        public Builder withVariant_row(int val) {
            variant_row = val;
            return this;
        }

        public Builder withQuantity(int val) {
            quantity = val;
            return this;
        }

        public Builder withRate(double val) {
            rate = val;
            return this;
        }

        public Builder withSku(String val) {
            sku = val;
            return this;
        }

        public Builder withCause(int val) {
            cause = val;
            return this;
        }

        public Builder withReturn_date(String val) {
            return_date = val;
            return this;
        }

        public Builder withGodown_id(int val) {
            godown_id = val;
            return this;
        }

        public ReturnProductEntity build() {
            return new ReturnProductEntity(this);
        }
    }
}
