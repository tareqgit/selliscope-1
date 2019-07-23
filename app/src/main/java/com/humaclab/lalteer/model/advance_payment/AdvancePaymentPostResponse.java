package com.humaclab.lalteer.model.advance_payment;

public class AdvancePaymentPostResponse{
	private String result;
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
