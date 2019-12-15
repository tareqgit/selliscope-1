package com.humaclab.selliscope.sales_return.model.get;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("com.robohorse.robopojogenerator")
public class DataItem implements Serializable {

	@SerializedName("order_id")
	private int order_id;

	@SerializedName("date")
	private String date;

	@SerializedName("product")
	private String product;

	@SerializedName("rate")
	private int rate;

	@SerializedName("qty")
	private int qty;

	@SerializedName("cause")
	private String cause;

	@SerializedName("id")
	private int id;

	@SerializedName("sku")
	private String sku;

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setProduct(String product){
		this.product = product;
	}

	public String getProduct(){
		return product;
	}

	public void setRate(int rate){
		this.rate = rate;
	}

	public int getRate(){
		return rate;
	}

	public void setQty(int qty){
		this.qty = qty;
	}

	public int getQty(){
		return qty;
	}

	public void setCause(String cause){
		this.cause = cause;
	}

	public String getCause(){
		return cause;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setSku(String sku){
		this.sku = sku;
	}

	public String getSku(){
		return sku;
	}

	public int getOrder_id() {
		return order_id;
	}

	public void setOrder_id(int order_id) {
		this.order_id = order_id;
	}
}