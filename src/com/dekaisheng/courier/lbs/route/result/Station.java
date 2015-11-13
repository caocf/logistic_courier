package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;

/**
 * 换乘站点
 * @author Dorian
 *
 */
public class Station implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GeoPoint location;
	public String name;
}

