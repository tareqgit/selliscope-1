package com.humaclab.lalteer.model.advance_payment;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class AdvancedPaymentResponse{

	@SerializedName("advance_payments")
	private List<AdvancePaymentsItem> advancePayments;

	@SerializedName("total_paid")
	private int totalPaid;

	@SerializedName("error")
	private boolean error;

	public List<AdvancePaymentsItem> getAdvancePayments(){
		return advancePayments;
	}

	public int getTotalPaid(){
		return totalPaid;
	}

	public boolean isError(){
		return error;
	}
}