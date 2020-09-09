package com.sokrio.sokrio_classic.sales_return.model.post;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class JsonMemberReturn{

	@SerializedName("outlet_id")
	private int outletId;

	@SerializedName("order_id")
	private int orderId;

	@SerializedName("products")
	private List<SalesReturn2019SelectedProduct> products;

	public void setOutletId(int outletId){
		this.outletId = outletId;
	}

	public int getOutletId(){
		return outletId;
	}

	public void setOrderId(int orderId){
		this.orderId = orderId;
	}

	public int getOrderId(){
		return orderId;
	}

	public void setProducts(List<SalesReturn2019SelectedProduct> products){
		this.products = products;
	}

	public List<SalesReturn2019SelectedProduct> getProducts(){
		return products;
	}
}