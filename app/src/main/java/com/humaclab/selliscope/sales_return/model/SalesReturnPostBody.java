package com.humaclab.selliscope.sales_return.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class SalesReturnPostBody{

	@SerializedName("return")
	private JsonMemberReturn jsonMemberReturn;

	public void setJsonMemberReturn(JsonMemberReturn jsonMemberReturn){
		this.jsonMemberReturn = jsonMemberReturn;
	}

	public JsonMemberReturn getJsonMemberReturn(){
		return jsonMemberReturn;
	}
}