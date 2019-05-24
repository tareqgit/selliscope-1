/*
 * Created by Tareq Islam on 5/23/19 1:36 PM
 *
 *  Last modified 5/23/19 1:36 PM
 */

/*
 * Created by Tareq Islam on 5/23/19 1:35 PM
 *
 *  Last modified 5/22/19 8:51 PM
 */

package com.humaclab.selliscope_myone.outlet_paging.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "outlets")
public class OutletItem implements Serializable {

	@SerializedName("owner")
	public String owner;

	@SerializedName("address")
	public String address;

	@SerializedName("type")
	public String type;

	@SerializedName("thana")
	public String thana;

	@SerializedName("phone")
	public String phone;

	@SerializedName("creditBalance")
	public String creditBalance;

	@SerializedName("district")
	public String district;

	@SerializedName("xtr")
	public String xtr;

	@SerializedName("name")
	public String name;

	@SerializedName("creditLimit")
	public String creditLimit;

	@SerializedName("xgcus")
	public String xgcus;

	@NonNull
	@PrimaryKey
	@SerializedName("id")
	public String id;

	@SerializedName("longitude")
	public String longitude;

	@SerializedName("latitude")
	public String latitude;


}
