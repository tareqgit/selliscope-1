package com.humaclab.lalteer.performance.claim.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
@Entity(tableName = "claim")
public class Claim {

	@PrimaryKey(autoGenerate = true)
	private int id;

	@SerializedName("other")
	private String other;

	@SerializedName("claim_date")
	private String claimDate;

	@SerializedName("reason_id")
	private int reasonId;

	public void setOther(String other){
		this.other = other;
	}

	public String getOther(){
		return other;
	}

	public void setClaimDate(String claimDate){
		this.claimDate = claimDate;
	}

	public String getClaimDate(){
		return claimDate;
	}

	public void setReasonId(int reasonId){
		this.reasonId = reasonId;
	}

	public int getReasonId(){
		return reasonId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Claim(String other, String claimDate, int reasonId) {
		this.other = other;
		this.claimDate = claimDate;
		this.reasonId = reasonId;
	}
}