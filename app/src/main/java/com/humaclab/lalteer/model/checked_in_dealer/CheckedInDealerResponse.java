package com.humaclab.lalteer.model.checked_in_dealer;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class CheckedInDealerResponse implements Serializable {

	@SerializedName("error")
	private boolean error;

	@SerializedName("dealer_ids")
	private List<Integer> dealerIds;

	public void setError(boolean error){
		this.error = error;
	}

	public boolean isError(){
		return error;
	}

	public void setDealerIds(List<Integer> dealerIds){
		this.dealerIds = dealerIds;
	}

	public List<Integer> getDealerIds(){
		return dealerIds;
	}
}