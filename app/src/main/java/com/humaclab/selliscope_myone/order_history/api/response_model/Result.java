package com.humaclab.selliscope_myone.order_history.api.response_model;

import java.util.List;

public class Result{
	private List<OrdersItem> orders;

	public void setOrders(List<OrdersItem> orders){
		this.orders = orders;
	}

	public List<OrdersItem> getOrders(){
		return orders;
	}

	@Override
 	public String toString(){
		return 
			"Result{" + 
			"orders = '" + orders + '\'' + 
			"}";
		}
}