package com.humaclab.lalteer.model.outstanding_payment;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class OutstandingPostResponse{

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

	@Override
 	public String toString(){
		return 
			"OutstandingPostResponse{" + 
			"result = '" + result + '\'' + 
			",error = '" + error + '\'' + 
			"}";
		}
}