/*
 * Created by Tareq Islam on 6/25/19 12:30 PM
 *
 *  Last modified 6/25/19 12:13 PM
 */

package com.humaclab.lalteer.model.advance_payment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class AdvancePaymentsSendItem implements Serializable {

	@SerializedName("amount")
	private Double amount;

	@SerializedName("comments")
	private String comments;

	@SerializedName("products")
	private String productsInfo;

	@SerializedName("bank_name")
	private String bankName;


	@SerializedName("payment_date")
	private String paymentDate;

	public AdvancePaymentsSendItem(Double amount, String comments, String productsInfo, String bankName, String paymentDate) {
		this.amount = amount;
		this.comments = comments;
		this.productsInfo = productsInfo;
		this.bankName = bankName;

		this.paymentDate = paymentDate;
	}

	public Double getAmount(){
		return amount;
	}

	public String getComments(){
		return comments;
	}

	public String getProductsInfo(){
		return productsInfo;
	}

	public String getBankName(){
		return bankName;
	}

	public String getPaymentDate(){
		return paymentDate;
	}
}