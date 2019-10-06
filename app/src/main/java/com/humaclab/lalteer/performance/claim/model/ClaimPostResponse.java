package com.humaclab.lalteer.performance.claim.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class ClaimPostResponse{

	@SerializedName("result")
	private String result;

	@SerializedName("error")
	private boolean error;

	public void setResult(String result){
		this.result = result;
	}

	public String getResult(){
		return result;
	}

	public void setError(boolean error){
		this.error = error;
	}

	public boolean isError(){
		return error;
	}
}