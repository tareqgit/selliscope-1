package com.sokrio.sokrio_classic.sales_return.model.post;

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