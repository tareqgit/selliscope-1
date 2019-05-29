package com.humaclab.selliscope_myone.order_history.api.response_model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Result{

	@SerializedName("orders")
	private List<OrdersItem> orders;

	public void setOrders(List<OrdersItem> orders){
		this.orders = orders;
	}

	public List<OrdersItem> getOrders(){
		return orders;
	}
}