package com.humaclab.lalteer.performance.claim.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class ClaimResponse{

	@SerializedName("result")
	private List<ReasonItem> result;

	@SerializedName("error")
	private boolean error;

	public void setResult(List<ReasonItem> result){
		this.result = result;
	}

	public List<ReasonItem> getResult(){
		return result;
	}

	public void setError(boolean error){
		this.error = error;
	}

	public boolean isError(){
		return error;
	}
}