package com.humaclab.selliscope.helper;

import java.io.Serializable;

/**
 * Created by leon on 21/3/18.
 */

public class SelectedProductHelper implements Serializable {

    private String productID;
    private String productName;
    private String productQuantity;
    private String productPrice;
    private String totalPrice;
    private String productRow;
    private String tpDiscount;
    private String tppromotionGrandPrice;

    public String getTppromotionGrandPrice() {
        return tppromotionGrandPrice;
    }

    public void setTppromotionGrandPrice(String tppromotionGrandPrice) {
        this.tppromotionGrandPrice = tppromotionGrandPrice;
    }


    public SelectedProductHelper(String productID, String productName, String productQuantity, String productPrice, String totalPrice, String productRow,
                                 String tpDiscount, String tppromotionGrandPrice ) {
        this.productID = productID;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.totalPrice = totalPrice;
        this.productRow = productRow;
        this.tpDiscount = tpDiscount;
        this.tppromotionGrandPrice = tppromotionGrandPrice;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductRow() {
        return productRow;
    }

    public void setProductRow(String productRow) {
        this.productRow = productRow;
    }


    public String getTpDiscount() {
        return tpDiscount;
    }

    public void setTpDiscount(String tpDiscount) {
        this.tpDiscount = tpDiscount;
    }
}
