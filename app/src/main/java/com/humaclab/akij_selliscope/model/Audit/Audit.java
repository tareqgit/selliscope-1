package com.humaclab.akij_selliscope.model.Audit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Audit implements Serializable {

    @SerializedName("question1")
    private String question1;
    @SerializedName("question2")
    private String question2;
    @SerializedName("question3")
    private String question3;
    @SerializedName("question4")
    private String question4;
    @SerializedName("question5")
    private String question5;
    @SerializedName("question6")
    private String question6;

    public String getQuestion7() {
        return question7;
    }

    public void setQuestion7(String question7) {
        this.question7 = question7;
    }

    @SerializedName("question7")
    private String question7;
    @SerializedName("audit_date")
    private String auditDate;
    @SerializedName("outlet_id")
    private Integer outletId;


    public String getOrdertId() {
        return ordertId;
    }

    public void setOrdertId(String ordertId) {
        this.ordertId = ordertId;
    }

    @SerializedName("order_id")
    private String ordertId;


    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public String getQuestion3() {
        return question3;
    }

    public void setQuestion3(String question3) {
        this.question3 = question3;
    }

    public String getQuestion4() {
        return question4;
    }

    public void setQuestion4(String question4) {
        this.question4 = question4;
    }

    public String getQuestion5() {
        return question5;
    }

    public void setQuestion5(String question5) {
        this.question5 = question5;
    }

    public String getQuestion6() {
        return question6;
    }

    public void setQuestion6(String question6) {
        this.question6 = question6;
    }

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }

    public Integer getOutletId() {
        return outletId;
    }

    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }


    public class AuditResponse implements Serializable{

        @SerializedName("error")
        private Boolean error;
        @SerializedName("result")
        private String result;

        public Boolean getError() {
            return error;
        }

        public void setError(Boolean error) {
            this.error = error;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

    }

}