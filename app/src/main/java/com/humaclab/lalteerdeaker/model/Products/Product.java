
package com.humaclab.lalteerdeaker.model.Products;


import com.google.gson.annotations.SerializedName;


public class Product {

    @SerializedName("brand_id")
    private Long mBrandId;
    @SerializedName("category_id")
    private Long mCategoryId;
    @SerializedName("created_date")
    private String mCreatedDate;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("hasVariant")
    private Boolean mHasVariant;
    @SerializedName("id")
    private Long mId;
    @SerializedName("image")
    private String mImage;
    @SerializedName("name")
    private String mName;
    @SerializedName("price")
    private Double mPrice;
    @SerializedName("slug")
    private String mSlug;
    @SerializedName("status")
    private Boolean mStatus;
    @SerializedName("updated_date")
    private String mUpdatedDate;

    public Long getBrandId() {
        return mBrandId;
    }

    public void setBrandId(Long brandId) {
        mBrandId = brandId;
    }

    public Long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(Long categoryId) {
        mCategoryId = categoryId;
    }

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        mCreatedDate = createdDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Boolean getHasVariant() {
        return mHasVariant;
    }

    public void setHasVariant(Boolean hasVariant) {
        mHasVariant = hasVariant;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Double getPrice() {
        return mPrice;
    }

    public void setPrice(Double price) {
        mPrice = price;
    }

    public String getSlug() {
        return mSlug;
    }

    public void setSlug(String slug) {
        mSlug = slug;
    }

    public Boolean getStatus() {
        return mStatus;
    }

    public void setStatus(Boolean status) {
        mStatus = status;
    }

    public String getUpdatedDate() {
        return mUpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        mUpdatedDate = updatedDate;
    }

}
