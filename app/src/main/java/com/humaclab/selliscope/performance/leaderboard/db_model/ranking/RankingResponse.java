package com.humaclab.selliscope.performance.leaderboard.db_model.ranking;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class RankingResponse{

	@SerializedName("previous_1st")
	private Previous1st previous1st;

	@SerializedName("authentic_user")
	private AuthenticUser authenticUser;

	@SerializedName("next_2nd")
	private Next2nd next2nd;

	@SerializedName("previous_2nd")
	private Previous2nd previous2nd;

	@SerializedName("message")
	private String message;

	@SerializedName("next_1st")
	private Next1st next1st;

	@SerializedName("status")
	private int status;

	public void setPrevious1st(Previous1st previous1st){
		this.previous1st = previous1st;
	}

	public Previous1st getPrevious1st(){
		return previous1st;
	}

	public void setAuthenticUser(AuthenticUser authenticUser){
		this.authenticUser = authenticUser;
	}

	public AuthenticUser getAuthenticUser(){
		return authenticUser;
	}

	public void setNext2nd(Next2nd next2nd){
		this.next2nd = next2nd;
	}

	public Next2nd getNext2nd(){
		return next2nd;
	}

	public void setPrevious2nd(Previous2nd previous2nd){
		this.previous2nd = previous2nd;
	}

	public Previous2nd getPrevious2nd(){
		return previous2nd;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setNext1st(Next1st next1st){
		this.next1st = next1st;
	}

	public Next1st getNext1st(){
		return next1st;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}