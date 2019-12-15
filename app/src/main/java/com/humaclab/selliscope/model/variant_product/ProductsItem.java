package com.humaclab.selliscope.model.variant_product;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * Created by leon on 9/3/2017.
 */

public class ProductsItem {

    @SerializedName("img")
    private String img;

    private String variantRow;
    private String stock;

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
    private Pitch pitch;

    @SerializedName("promotion")
    private Promotion promotion;

    @SerializedName("variants")
    private List<VariantsItem> variants;

    private boolean hasVariant;

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

    public String getVariantRow() {
        return variantRow;
    }

    public void setVariantRow(String variantRow) {
        this.variantRow = variantRow;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
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

    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public List<VariantsItem> getVariants() {
        return variants;
    }

    public void setVariants(List<VariantsItem> variants) {
        this.variants = variants;
    }

    public boolean isHasVariant() {
        return hasVariant;
    }

    public void setHasVariant(boolean hasVariant) {
        this.hasVariant = hasVariant;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductsItem that = (ProductsItem) o;
        return discount == that.discount &&
                id == that.id &&
                hasVariant == that.hasVariant &&
                Objects.equals(img, that.img) &&
            /*   Objects.equals(variantRow, that.variantRow) &&*/
             /*   Objects.equals(stock,that.stock) &&*/
               /* Objects.equals(godown, that.godown) &&
                Objects.equals(price, that.price) &&*/
              /*  Objects.equals(name, that.name) &&*/
             /*    Objects.equals(pitch, that.pitch) &&
                Objects.equals(promotion, that.promotion) &&
                Objects.equals(variants, that.variants) &&*/
                Objects.equals(category, that.category) &&
                Objects.equals(brand, that.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(img, variantRow, stock, godown, price, name, discount, id, pitch, promotion, variants, hasVariant, category, brand);
    }
}