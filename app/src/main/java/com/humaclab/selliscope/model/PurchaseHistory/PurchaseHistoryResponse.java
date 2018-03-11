package com.humaclab.selliscope.model.PurchaseHistory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by leon on 6/3/18.
 */

public class PurchaseHistoryResponse implements Serializable {
    @SerializedName("result")
    private Result result;

    @SerializedName("error")
    private boolean error;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public static class Result implements Serializable {
        @SerializedName("purchase_history")
        private List<PurchaseHistoryItem> purchaseHistory;

        @SerializedName("total_paid")
        private String totalPaid;

        @SerializedName("total_due")
        private String totalDue;

        public List<PurchaseHistoryItem> getPurchaseHistory() {
            return purchaseHistory;
        }

        public void setPurchaseHistory(List<PurchaseHistoryItem> purchaseHistory) {
            this.purchaseHistory = purchaseHistory;
        }

        public String getTotalPaid() {
            return totalPaid;
        }

        public void setTotalPaid(String totalPaid) {
            this.totalPaid = totalPaid;
        }

        public String getTotalDue() {
            return totalDue;
        }

        public void setTotalDue(String totalDue) {
            this.totalDue = totalDue;
        }
    }
}
