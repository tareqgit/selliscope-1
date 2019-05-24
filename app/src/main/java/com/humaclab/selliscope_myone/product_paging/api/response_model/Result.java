/*
 * Created by Tareq Islam on 5/23/19 1:38 PM
 *
 *  Last modified 5/23/19 1:23 PM
 */

package com.humaclab.selliscope_myone.product_paging.api.response_model;

import com.humaclab.selliscope_myone.product_paging.model.ProductsItem;

import java.util.List;

public class Result{
	private List<ProductsItem> products;

	public void setProducts(List<ProductsItem> products){
		this.products = products;
	}

	public List<ProductsItem> getProducts(){
		return products;
	}
}