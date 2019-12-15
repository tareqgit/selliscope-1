package com.humaclab.selliscope.performance.leaderboard.db_model.top_user;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class DataItem{

	@SerializedName("Position")
	private int position;

	@SerializedName("image_url")
	private String imageUrl;

	@SerializedName("name")
	private String name;

	@SerializedName("grand_total")
	private double grandTotal;

	@SerializedName("amount")
	private double amount;

	@SerializedName("total_outlet")
	private int total_outlet;


	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getTotal_outlet() {
		return total_outlet;
	}

	public void setTotal_outlet(int total_outlet) {
		this.total_outlet = total_outlet;
	}

	public void setPosition(int position){
		this.position = position;
	}

	public int getPosition(){
		return position;
	}

	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}

	public String getImageUrl(){
		return imageUrl;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setGrandTotal(double grandTotal){
		this.grandTotal = grandTotal;
	}

	public double getGrandTotal(){
		return grandTotal;
	}
}