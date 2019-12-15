package com.humaclab.selliscope.sales_return.model.post;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class SalesReturn2019Response {

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