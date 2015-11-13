package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class GeoPoint implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double lat;
	public double lng;
	
	public GeoPoint(double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	public LatLng toLatLng(){
		return new LatLng(lat,lng);
	}
}
