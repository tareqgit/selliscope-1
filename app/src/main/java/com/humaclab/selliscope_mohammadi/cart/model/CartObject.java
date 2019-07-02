/*
 * Created by Tareq Islam on 7/1/19 2:27 PM
 *
 *  Last modified 7/1/19 2:27 PM
 */

package com.humaclab.selliscope_mohammadi.cart.model;

/***
 * Created by mtita on 01,July,2019.
 */
public class CartObject {
    private String dia;
    private String grade;
    private Double rate;
    private Double qty;
    private Double total_price;
    private boolean selected;

    private CartObject(Builder builder) {
        dia = builder.dia;
        grade = builder.grade;
        rate = builder.rate;
        qty = builder.qty;
        total_price = builder.total_price;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public void setTotal_price(Double total_price) {
        this.total_price = total_price;
    }

    public String getDia() {
        return dia;
    }

    public String getGrade() {
        return grade;
    }

    public Double getRate() {
        return rate;
    }

    public Double getQty() {
        return qty;
    }

    public Double getTotal_price() {
        return total_price;
    }


    public static final class Builder {
        private String dia;
        private String grade;
        private Double rate;
        private Double qty;
        private Double total_price;

        public Builder() {
        }

        public Builder withDia(String val) {
            dia = val;
            return this;
        }

        public Builder withGrade(String val) {
            grade = val;
            return this;
        }

        public Builder withRate(Double val) {
            rate = val;
            return this;
        }

        public Builder withQty(Double val) {
            qty = val;
            return this;
        }

        public Builder withTotal_price(Double val) {
            total_price = val;
            return this;
        }

        public CartObject build() {
            return new CartObject(this);
        }
    }
}
