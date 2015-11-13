package com.dekaisheng.courier.lbs.geocode;

import java.util.List;

import com.dekaisheng.courier.core.webapi.bean.UnFinishedOrderForm;

import android.location.Address;

public class GeocodeBean {
	public List<Address> result;
	public UnFinishedOrderForm order;
	
	public GeocodeBean(){
		
	}
	
	public GeocodeBean(List<Address> result, UnFinishedOrderForm order){
		this.result = result;
		this.order = order;
	}
}
