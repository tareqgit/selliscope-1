/*
 * Created by Tareq Islam on 5/23/19 1:39 PM
 *
 *  Last modified 5/23/19 1:28 PM
 */

package com.humaclab.selliscope_myone.product_paging.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "products")
public class ProductsItem implements Serializable {

	@SerializedName("stock_type")
	public String stockType;

	@SerializedName("img")
	public String img;
	@SerializedName("price")
	public String price;
	@SerializedName("name")
	public String name;
	@NonNull
	@PrimaryKey
	@SerializedName("id")
	public String id;
	@SerializedName("category")
	public String category;
	@SerializedName("brand")
	public String brand;


}
