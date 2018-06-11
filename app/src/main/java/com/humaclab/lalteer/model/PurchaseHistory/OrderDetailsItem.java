package com.humaclab.lalteer.model.PurchaseHistory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 6/3/18.
 */

public class OrderDetailsItem implements Serializable {

    @SerializedName("product_discount")
    private String productDiscount;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("product_rate")
    private String productRate;

    @SerializedName("product_qty")
    private int productQty;

    @SerializedName("product_price")
    private String productPrice;

    @SerializedName("product_name")
    private String productName;

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductRate() {
        return productRate;
    }

    public void setProductRate(String productRate) {
        this.productRate = productRate;
    }

    public int getProductQty() {
        return productQty;
    }

    public void setProductQty(int productQty) {
        this.productQty = productQty;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}