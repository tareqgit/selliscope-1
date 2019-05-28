package com.humaclab.selliscope_myone.order_history.api.response_model;

import java.util.List;

public class OrdersItem{
	private String orderDate;
	private String outletId;
	private int subTotal;
	private String discount;
	private int id;
	private String grandTotal;
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

	@Override
 	public String toString(){
		return 
			"OrdersItem{" + 
			"order_date = '" + orderDate + '\'' + 
			",outlet_id = '" + outletId + '\'' + 
			",sub_total = '" + subTotal + '\'' + 
			",discount = '" + discount + '\'' + 
			",id = '" + id + '\'' + 
			",grand_total = '" + grandTotal + '\'' + 
			",products = '" + products + '\'' + 
			"}";
		}
}