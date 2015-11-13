package com.dekaisheng.courier.lbs.route;

/**
 * 路径规划的单位
 * @author Dorian
 *
 */
public enum Unit {
	
	METRIC("metric"),IMPERIAL("imperial");
	private String value;
	
	public String getVal(){
		return value;
	}
	
	private Unit(String val){
		this.value = val;
	}
}
