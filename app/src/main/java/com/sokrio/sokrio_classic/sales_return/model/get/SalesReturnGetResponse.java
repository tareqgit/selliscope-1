package com.sokrio.sokrio_classic.sales_return.model.get;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class SalesReturnGetResponse{

	@SerializedName("next_page")
	private int nextPage;

	@SerializedName("result")
	private Result result;

	@SerializedName("error")
	private boolean error;

	public void setNextPage(int nextPage){
		this.nextPage = nextPage;
	}

	public int getNextPage(){
		return nextPage;
	}

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