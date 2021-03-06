package com.easyopstech.easyops.performance.leaderboard.db_model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("com.robohorse.robopojogenerator")
public class LeaderboardTotalPerticipatesResponse implements Serializable {

	@SerializedName("Participants")
	private int participants;

	@SerializedName("message")
	private String message;

	@SerializedName("Collected")
	private double collected;

	@SerializedName("status")
	private int status;

	@SerializedName("total_outlets")
	private int total_outlets;

	public int getTotal_outlets() {
		return total_outlets;
	}

	public void setTotal_outlets(int total_outlets) {
		this.total_outlets = total_outlets;
	}

	public void setParticipants(int participants){
		this.participants = participants;
	}

	public int getParticipants(){
		return participants;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setCollected(double collected){
		this.collected = collected;
	}

	public double getCollected(){
		return collected;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}