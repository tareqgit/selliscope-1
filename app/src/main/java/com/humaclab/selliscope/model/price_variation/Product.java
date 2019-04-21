package com.humaclab.selliscope.model.price_variation;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("id")

    private Integer id;
    @SerializedName("name")

    private String name;
    @SerializedName("price")

    private String price;
    @SerializedName("priceVariation")

    private List<PriceVariation> priceVariation ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<PriceVariation> getPriceVariation() {
        return priceVariation;
    }

    public void setPriceVariation(List<PriceVariation> priceVariation) {
        this.priceVariation = priceVariation;
    }

}
