package com.humaclab.selliscope.model.VariantProduct;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by leon on 9/3/2017.
 */

public class ProductsItem {

    @SerializedName("img")
    private String img;

    @SerializedName("godown")
    private List<GodownItem> godown;

    @SerializedName("price")
    private String price;

    @SerializedName("name")
    private String name;

    @SerializedName("discount")
    private int discount;

    @SerializedName("id")
    private int id;

    @SerializedName("pitch")
    private String pitch;

    @SerializedName("promotion")
    private String promotion;

    @SerializedName("variants")
    private List<VariantsItem> variants;

    @SerializedName("category")
    private Category category;

    @SerializedName("brand")
    private Brand brand;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<GodownItem> getGodown() {
        return godown;
    }

    public void setGodown(List<GodownItem> godown) {
        this.godown = godown;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<VariantsItem> getVariants() {
        return variants;
    }

    public void setVariants(List<VariantsItem> variants) {
        this.variants = variants;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }
}