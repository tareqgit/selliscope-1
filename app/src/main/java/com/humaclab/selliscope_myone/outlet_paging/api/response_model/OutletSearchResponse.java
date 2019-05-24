/*
 * Created by Tareq Islam on 5/23/19 1:35 PM
 *
 *  Last modified 5/22/19 8:51 PM
 */

package com.humaclab.selliscope_myone.outlet_paging.api.response_model;

public class  OutletSearchResponse{
	private int nextPage;
	private Result result;
	private boolean error;

	public void setNextPage(int nextPage){
		this.nextPage = nextPage;
	}

	public int getNextPage(){
		return nextPage;
	}

	public void setResult(Result result){
		this.result = result;
	}

	public Result getResult(){
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
			"OutletSearchResponse{" + 
			"next_page = '" + nextPage + '\'' + 
			",result = '" + result + '\'' + 
			",error = '" + error + '\'' + 
			"}";
		}
}
