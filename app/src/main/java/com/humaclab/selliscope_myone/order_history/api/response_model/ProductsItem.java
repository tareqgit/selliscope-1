package com.humaclab.selliscope_myone.order_history.api.response_model;

public class ProductsItem{
	private int total;
	private String price;
	private String productId;
	private int qty;
	private int id;
	private int orderId;

	public void setTotal(int total){
		this.total = total;
	}

	public int getTotal(){
		return total;
	}

	public void setPrice(String price){
		this.price = price;
	}

	public String getPrice(){
		return price;
	}

	public void setProductId(String productId){
		this.productId = productId;
	}

	public String getProductId(){
		return productId;
	}

	public void setQty(int qty){
		this.qty = qty;
	}

	public int getQty(){
		return qty;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setOrderId(int orderId){
		this.orderId = orderId;
	}

	public int getOrderId(){
		return orderId;
	}

	@Override
 	public String toString(){
		return 
			"ProductsItem{" + 
			"total = '" + total + '\'' + 
			",price = '" + price + '\'' + 
			",product_id = '" + productId + '\'' + 
			",qty = '" + qty + '\'' + 
			",id = '" + id + '\'' + 
			",order_id = '" + orderId + '\'' + 
			"}";
		}
}
