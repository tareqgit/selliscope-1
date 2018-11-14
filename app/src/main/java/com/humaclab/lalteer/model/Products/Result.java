
package com.humaclab.lalteer.model.Products;

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class Result {

    @SerializedName("products")
    private List<Product> mProducts;

    public List<Product> getProducts() {
        return mProducts;
    }

    public void setProducts(List<Product> products) {
        mProducts = products;
    }

}
