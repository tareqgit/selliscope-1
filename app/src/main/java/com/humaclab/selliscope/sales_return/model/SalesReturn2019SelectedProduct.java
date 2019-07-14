package com.humaclab.selliscope.sales_return.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/***
 * Created by mtita on 09,July,2019.
 */
public class SalesReturn2019SelectedProduct implements Serializable {

   private String  productName;
   @SerializedName("sku")
   private String productSKU;
   @SerializedName("product_id")
   private int productId;
   @SerializedName("variant_row")
   private int productVariantRow;
   @SerializedName("cause")
   private int reason;
   @SerializedName("rate")
   private Double productRate;
   @SerializedName("quantity")
   private Double productQty;
   private Double productTotal;
   @SerializedName("return_date")
   private String return_date;
   @SerializedName("godown_id")
   private int godown_id;


    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public void setGodown_id(int godown_id) {
        this.godown_id = godown_id;
    }

    private SalesReturn2019SelectedProduct(Builder builder) {
        productName = builder.productName;
        productSKU = builder.productSKU;
        productId = builder.productId;
        productVariantRow = builder.productVariantRow;
        reason = builder.reason;
        productRate = builder.productRate;
        productQty = builder.productQty;
        productTotal = builder.productTotal;
        return_date = builder.return_date;
        godown_id = builder.godown_id;
    }


    public String getProductName() {
        return productName;
    }

    public String getProductSKU() {
        return productSKU;
    }

    public int getProductId() {
        return productId;
    }

    public int getProductVariantRow() {
        return productVariantRow;
    }

    public int getReason() {
        return reason;
    }

    public Double getProductRate() {
        return productRate;
    }

    public Double getProductQty() {
        return productQty;
    }

    public Double getProductTotal() {
        return productTotal;
    }

    public String getReturn_date() {
        return return_date;
    }

    public int getGodown_id() {
        return godown_id;
    }

    public static final class Builder {
        private String productName;
        private String productSKU;
        private int productId;
        private int productVariantRow;
        private int reason;
        private Double productRate;
        private Double productQty;
        private Double productTotal;
        private String return_date;
        private int godown_id;

        public Builder() {
        }

        public Builder withProductName(String val) {
            productName = val;
            return this;
        }

        public Builder withProductSKU(String val) {
            productSKU = val;
            return this;
        }

        public Builder withProductId(int val) {
            productId = val;
            return this;
        }

        public Builder withProductVariantRow(int val) {
            productVariantRow = val;
            return this;
        }

        public Builder withReason(int val) {
            reason = val;
            return this;
        }

        public Builder withProductRate(Double val) {
            productRate = val;
            return this;
        }

        public Builder withProductQty(Double val) {
            productQty = val;
            return this;
        }

        public Builder withProductTotal(Double val) {
            productTotal = val;
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

        public SalesReturn2019SelectedProduct build() {
            return new SalesReturn2019SelectedProduct(this);
        }
    }
}
