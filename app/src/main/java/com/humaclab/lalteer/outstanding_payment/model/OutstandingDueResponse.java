/*
 * Created by Tareq Islam on 10/28/19 2:29 PM
 *
 *  Last modified 10/16/19 12:11 PM
 */

package com.humaclab.lalteer.outstanding_payment.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class OutstandingDueResponse {

	@SerializedName("outstanding_due")
	private double outstandingDue;

	@SerializedName("due")
	private double due;

	@SerializedName("paid")
	private double paid;

	@SerializedName("error")
	private boolean error;

	public void setOutstandingDue(double outstandingDue){
		this.outstandingDue = outstandingDue;
	}

	public double getOutstandingDue(){
		return outstandingDue;
	}

	public void setDue(double due){
		this.due = due;
	}

	public double getDue(){
		return due;
	}

	public void setPaid(double paid){
		this.paid = paid;
	}

	public double getPaid(){
		return paid;
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
			"OutstandingDueResponse{" +
			"outstanding_due = '" + outstandingDue + '\'' + 
			",due = '" + due + '\'' + 
			",paid = '" + paid + '\'' + 
			",error = '" + error + '\'' + 
			"}";
		}
}