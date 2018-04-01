package com.humaclab.selliscope_rangs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by leon on 21/12/17.
 */

public class ProductResponse implements Serializable {
    @SerializedName("error")
    private boolean error;

    @SerializedName("result")
    private ProductResult productResult;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ProductResult getProductResult() {
        return productResult;
    }

    public void setProductResult(ProductResult productResult) {
        this.productResult = productResult;
    }

    public class ProductResult implements Serializable {
        @SerializedName("products")
        private List<Product> products;

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }
    }

    public class Product implements Serializable {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("price")
        private String price;

        @SerializedName("category")
        private String category;

        @SerializedName("brand")
        private String brand;

        @SerializedName("img")
        private String img;

        @SerializedName("stock_type")
        private String stock_type;

        @SerializedName("stock")
        private String stock;

        public String getId() {
            return id;
        }

        public void setId(String id) {
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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getStock_type() {
            return stock_type;
        }

        public void setStock_type(String stock_type) {
            this.stock_type = stock_type;
        }

        public String getStock() {
            return stock;
        }

        public void setStock(String stock) {
            this.stock = stock;
        }
    }
}
