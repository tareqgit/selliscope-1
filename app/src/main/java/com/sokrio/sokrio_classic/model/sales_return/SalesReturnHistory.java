package com.sokrio.sokrio_classic.model.sales_return;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SalesReturnHistory implements Serializable {
    @SerializedName("error")
    public boolean error;
    @SerializedName("result")
    public List<Result> results ;

    public static class Result implements Serializable{
        @SerializedName("id")
        public Integer id;
        @SerializedName("outlet")
        public String outlet;
        @SerializedName("user")
        public String user;
        @SerializedName("order")
        public Integer order;
        @SerializedName("product")
        public String product;
        @SerializedName("warehouse")
        public String warehouse;
        @SerializedName("invoice_no")
        public Object invoiceNo;
        @SerializedName("qty")
        public Integer qty;
        @SerializedName("note")
        public Object note;
        @SerializedName("return_date")
        public String returnDate;
        @SerializedName("stage")
        public String stage;
        @SerializedName("details")
        public List<Detail> details;
    }

    public class Detail implements Serializable {

        @SerializedName("reason")
        public String reason;
        @SerializedName("sku")
        public Object sku;
        @SerializedName("qty")
        public String qty;

    }
}
