package com.humaclab.selliscope_myone.order_history.api.response_model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class OrderHistoryResponse{

	@SerializedName("result")
	private Result result;

	@SerializedName("error")
	private boolean error;

	public void setResult(Result result){
		this.result = result;
	}

	public Result getResult(){
		return result;
	}

	public void setError(boolean error){
		this.error = error;
	}

	public boolean isError(){
		return error;
	}
}