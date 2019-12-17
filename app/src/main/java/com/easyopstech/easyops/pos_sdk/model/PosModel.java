package com.easyopstech.easyops.pos_sdk.model;

import java.util.List;

/***
 * Created by mtita on 15,May,2019.
 */
public class PosModel {

    private PosModel(Builder builder) {
        outletName = builder.outletName;
        number = builder.number;
        receipt = builder.receipt;
        cashier = builder.cashier;
        printTimeStamp = builder.printTimeStamp;
        customerName = builder.customerName;
        customerAddr = builder.customerAddr;
        mProducts = builder.mProducts;
        total_quantity = builder.total_quantity;
        invTotal = builder.invTotal;
        netAmount = builder.netAmount;
        total_C_Amount = builder.total_C_Amount;
        paytype = builder.paytype;
        payOrder = builder.payOrder;
        amount = builder.amount;
        totalPaid = builder.totalPaid;
        due = builder.due;
    }

    public static enum PAYTYPE{
        Cash, Credit
    }
    private String outletName;
    private String number;
    private String receipt;
    private String cashier;
    private String printTimeStamp;
    private String customerName;
    private String customerAddr;
    private List<Product> mProducts;
    private double total_quantity;

    public static class Product{
       public String p_Name;
        public double p_Rate;
        public double p_C_Amount;
        public double p_Quantity;
        public double p_Net;

        public Product() {
        }


    }
    private double invTotal;
    private double netAmount;
    private double total_C_Amount;
    private PAYTYPE paytype;
    private String payOrder;
    private double amount;
    private double totalPaid;
    private double due;

    public String getOutletName() {
        return outletName;
    }

    public String getReceipt() {
        return receipt;
    }

    public String getCashier() {
        return cashier;
    }

    public String getPrintTimeStamp() {
        return printTimeStamp;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddr() {
        return customerAddr;
    }

    public List<Product> getProducts() {
        return mProducts;
    }

    public double getTotal_quantity() {
        return total_quantity;
    }

    public double getInvTotal() {
        return invTotal;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public double getTotal_C_Amount() {
        return total_C_Amount;
    }

    public PAYTYPE getPaytype() {
        return paytype;
    }

    public String getPayOrder() {
        return payOrder;
    }

    public double getAmount() {
        return amount;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public double getDue() {
        return due;
    }

    public String getNumber() {
        return number;
    }


    public static final class Builder {
        private String outletName;
        private String number;
        private String receipt;
        private String cashier;
        private String printTimeStamp;
        private String customerName;
        private String customerAddr;
        private List<Product> mProducts;
        private double total_quantity;
        private double invTotal;
        private double netAmount;
        private double total_C_Amount;
        private PAYTYPE paytype;
        private String payOrder;
        private double amount;
        private double totalPaid;
        private double due;

        public Builder() {
        }

        public Builder withOutletName(String val) {
            outletName = val;
            return this;
        }

        public Builder withNumber(String val) {
            number = val;
            return this;
        }

        public Builder withReceipt(String val) {
            receipt = val;
            return this;
        }

        public Builder withCashier(String val) {
            cashier = val;
            return this;
        }

        public Builder withPrintTimeStamp(String val) {
            printTimeStamp = val;
            return this;
        }

        public Builder withCustomerName(String val) {
            customerName = val;
            return this;
        }

        public Builder withCustomerAddr(String val) {
            customerAddr = val;
            return this;
        }

        public Builder withMProducts(List<Product> val) {
            mProducts = val;
            return this;
        }

        public Builder withTotal_quantity(double val) {
            total_quantity = val;
            return this;
        }

        public Builder withInvTotal(double val) {
            invTotal = val;
            return this;
        }

        public Builder withNetAmount(double val) {
            netAmount = val;
            return this;
        }

        public Builder withTotal_C_Amount(double val) {
            total_C_Amount = val;
            return this;
        }

        public Builder withPaytype(PAYTYPE val) {
            paytype = val;
            return this;
        }

        public Builder withPayOrder(String val) {
            payOrder = val;
            return this;
        }

        public Builder withAmount(double val) {
            amount = val;
            return this;
        }

        public Builder withTotalPaid(double val) {
            totalPaid = val;
            return this;
        }

        public Builder withDue(double val) {
            due = val;
            return this;
        }

        public PosModel build() {
            return new PosModel(this);
        }
    }
}
