package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;

import com.dekaisheng.courier.lbs.route.VehicleType;

/**
 * 
 * 交通工具，所有类型请看{@link VehicleType}
 * @author Dorian
 *
 */
public class Vehicle implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String icon;
	public String local_icon;
	public String name; // 交通工具名称如：Subway
	public String type; // 交通工具类型：SUBWAY
}