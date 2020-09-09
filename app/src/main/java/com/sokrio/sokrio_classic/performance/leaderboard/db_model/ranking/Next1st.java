package com.sokrio.sokrio_classic.performance.leaderboard.db_model.ranking;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Next1st{

	@SerializedName("total_outlet")
	private int totalOutlet;

	@SerializedName("amount")
	private double amount;

	@SerializedName("grand_total")
	private double grand_total;

	@SerializedName("Position")
	private int position;

	@SerializedName("image_url")
	private String imageUrl;

	@SerializedName("name")
	private String name;

	public double getGrand_total() {
		return grand_total;
	}

	public void setGrand_total(double grand_total) {
		this.grand_total = grand_total;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setTotalOutlet(int totalOutlet){
		this.totalOutlet = totalOutlet;
	}

	public int getTotalOutlet(){
		return totalOutlet;
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
}