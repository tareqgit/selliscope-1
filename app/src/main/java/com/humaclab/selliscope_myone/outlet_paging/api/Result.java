package com.humaclab.selliscope_myone.outlet_paging.api;

import java.util.List;

public class Result{
	private List<OutletItem> outlet;

	public void setOutlet(List<OutletItem> outlet){
		this.outlet = outlet;
	}

	public List<OutletItem> getOutlet(){
		return outlet;
	}

	@Override
 	public String toString(){
		return 
			"Result{" + 
			"outlet = '" + outlet + '\'' + 
			"}";
		}
}