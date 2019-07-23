package com.humaclab.lalteer.model.advance_payment;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("com.robohorse.robopojogenerator")
public class AdvancePaymentsItem implements Serializable {

	@SerializedName("amount")
	private Double amount;

	@SerializedName("comments")
	private String comments;

	@SerializedName("products_info")
	private String productsInfo;

	@SerializedName("bank_name")
	private String bankName;

	@SerializedName("id")
	private int id;

	@SerializedName("payment_date")
	private String paymentDate;

	public AdvancePaymentsItem(Double amount, String comments, String productsInfo, String bankName,  String paymentDate) {
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

	public int getId(){
		return id;
	}

	public String getPaymentDate(){
		return paymentDate;
	}
}