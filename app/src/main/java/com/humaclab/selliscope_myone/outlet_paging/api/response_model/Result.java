/*
 * Created by Tareq Islam on 5/23/19 1:35 PM
 *
 *  Last modified 5/22/19 2:01 PM
 */

package com.humaclab.selliscope_myone.outlet_paging.api.response_model;

import com.humaclab.selliscope_myone.outlet_paging.model.OutletItem;

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