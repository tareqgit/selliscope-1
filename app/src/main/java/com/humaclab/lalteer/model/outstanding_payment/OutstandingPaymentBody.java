package com.humaclab.lalteer.model.outstanding_payment;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("com.robohorse.robopojogenerator")
public class OutstandingPaymentBody implements Serializable {

	@SerializedName("amount")
	private double amount;

	@SerializedName("payment_date")
	private String paymentDate;

	public void setAmount(double amount){
		this.amount = amount;
	}

	public double getAmount(){
		return amount;
	}

	public void setPaymentDate(String paymentDate){
		this.paymentDate = paymentDate;
	}

	public String getPaymentDate(){
		return paymentDate;
	}


	public OutstandingPaymentBody(double amount, String paymentDate) {
		this.amount = amount;
		this.paymentDate = paymentDate;
	}

	@Override
 	public String toString(){
		return 
			"OutstandingPaymentBody{" + 
			"amount = '" + amount + '\'' + 
			",payment_date = '" + paymentDate + '\'' + 
			"}";
		}
}