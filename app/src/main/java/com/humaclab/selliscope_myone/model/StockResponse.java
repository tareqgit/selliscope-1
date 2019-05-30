/*
 * Created by Tareq Islam on 5/30/19 12:50 PM
 *
 *  Last modified 5/19/19 1:08 PM
 */

package com.humaclab.selliscope_myone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by leon on 24/12/17.
 */

public class StockResponse implements Serializable {
    @SerializedName("error")
    private boolean error;

    @SerializedName("result")
    private Stock stock;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public class Stock implements Serializable {
        @SerializedName("stock")
        private String stock;

        public String getStock() {
            return stock;
        }

        public void setStock(String stock) {
            this.stock = stock;
        }
    }
}
