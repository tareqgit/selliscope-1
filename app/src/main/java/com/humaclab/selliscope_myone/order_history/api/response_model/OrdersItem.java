package com.humaclab.selliscope_myone.order_history.api.response_model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class OrdersItem{

	@SerializedName("order_date")
	private String orderDate;

	@SerializedName("outlet_id")
	private String outletId;

	@SerializedName("sub_total")
	private int subTotal;

	@SerializedName("discount")
	private String discount;

	@SerializedName("id")
	private int id;

	@SerializedName("grand_total")
	private String grandTotal;

	@SerializedName("products")
	private List<ProductsItem> products;

	public void setOrderDate(String orderDate){
		this.orderDate = orderDate;
	}

	public String getOrderDate(){
		return orderDate;
	}

	public void setOutletId(String outletId){
		this.outletId = outletId;
	}

	public String getOutletId(){
		return outletId;
	}

	public void setSubTotal(int subTotal){
		this.subTotal = subTotal;
	}

	public int getSubTotal(){
		return subTotal;
	}

	public void setDiscount(String discount){
		this.discount = discount;
	}

	public String getDiscount(){
		return discount;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setGrandTotal(String grandTotal){
		this.grandTotal = grandTotal;
	}

	public String getGrandTotal(){
		return grandTotal;
	}

	public void setProducts(List<ProductsItem> products){
		this.products = products;
	}

	public List<ProductsItem> getProducts(){
		return products;
	}
}