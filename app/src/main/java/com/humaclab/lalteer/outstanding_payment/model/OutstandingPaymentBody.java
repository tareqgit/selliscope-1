/*
 * Created by Tareq Islam on 10/28/19 2:29 PM
 *
 *  Last modified 10/27/19 5:16 PM
 */

package com.humaclab.lalteer.outstanding_payment.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Generated("com.robohorse.robopojogenerator")
public class OutstandingPaymentBody implements Serializable {

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	@SerializedName("id")
	private int id;


	@SerializedName("outlet_id")
	private int outlet_id;  //this for api path not for body


	@SerializedName("amount")
	private double amount;

	@SerializedName("payment_date")
	private String paymentDate;

	@SerializedName("img")
	private String img;

	@SerializedName("comments")
	private String comments;

	@SerializedName("type")
	private int type;


	@SerializedName("cheque_no")
	private String cheque_no;

	@SerializedName("bank_name")
	private String bank_name;

	@SerializedName("deposit_to")
	private String deposit_to;

	@SerializedName("deposit_from")
	private String deposit_from;


	@SerializedName("depositedSlipNumber")
	private String depositedSlipNumber;

	@SerializedName("cheque_date")
	private String cheque_date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOutlet_id() {
		return outlet_id;
	}

	public void setOutlet_id(int outlet_id) {
		this.outlet_id = outlet_id;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCheque_no() {
		return cheque_no;
	}

	public void setCheque_no(String cheque_no) {
		this.cheque_no = cheque_no;
	}

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getDeposit_to() {
		return deposit_to;
	}

	public void setDeposit_to(String deposit_to) {
		this.deposit_to = deposit_to;
	}

	public String getDeposit_from() {
		return deposit_from;
	}

	public void setDeposit_from(String deposit_from) {
		this.deposit_from = deposit_from;
	}

	public String getDepositedSlipNumber() {
		return depositedSlipNumber;
	}

	public void setDepositedSlipNumber(String depositedSlipNumber) {
		this.depositedSlipNumber = depositedSlipNumber;
	}

	public String getCheque_date() {
		return cheque_date;
	}

	public void setCheque_date(String cheque_date) {
		this.cheque_date = cheque_date;
	}

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


	public OutstandingPaymentBody(int id, int outlet_id, double amount, String paymentDate, String img, String comments, int type, String cheque_no, String bank_name, String deposit_to, String deposit_from, String depositedSlipNumber, String cheque_date) {
		this.id = id;
		this.outlet_id = outlet_id;
		this.amount = amount;
		this.paymentDate = paymentDate;
		this.img = img;
		this.comments = comments;
		this.type = type;
		this.cheque_no = cheque_no;
		this.bank_name = bank_name;
		this.deposit_to = deposit_to;
		this.deposit_from = deposit_from;
		this.depositedSlipNumber = depositedSlipNumber;
		this.cheque_date = cheque_date;
	}

	@Ignore
	public OutstandingPaymentBody(double amount, String paymentDate) {
		this.amount = amount;
		this.paymentDate = paymentDate;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OutstandingPaymentBody that = (OutstandingPaymentBody) o;
		return id == that.id &&
				outlet_id == that.outlet_id &&
				Double.compare(that.amount, amount) == 0 &&
				type == that.type &&
				Objects.equals(paymentDate, that.paymentDate) &&
				Objects.equals(img, that.img) &&
				Objects.equals(comments, that.comments) &&
				Objects.equals(cheque_no, that.cheque_no) &&
				Objects.equals(bank_name, that.bank_name) &&
				Objects.equals(deposit_to, that.deposit_to) &&
				Objects.equals(deposit_from, that.deposit_from) &&
				Objects.equals(depositedSlipNumber, that.depositedSlipNumber) &&
				Objects.equals(cheque_date, that.cheque_date);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, outlet_id, amount, paymentDate, img, comments, type, cheque_no, bank_name, deposit_to, deposit_from, depositedSlipNumber, cheque_date);
	}
}