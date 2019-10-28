/*
 * Created by Tareq Islam on 10/28/19 2:29 PM
 *
 *  Last modified 10/16/19 2:04 PM
 */

package com.humaclab.lalteer.outstanding_payment.model;

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