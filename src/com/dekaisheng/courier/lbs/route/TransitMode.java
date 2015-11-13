package com.dekaisheng.courier.lbs.route;

/**
 * 换乘工具选择，可以是一下的一个或者多个，多个用"|"分隔，如bus|subway|train
 * rail=train|tram|subway
 * @author Dorian
 *
 */
public enum TransitMode {

	BUS("bus"),
	SUBWAY("subway"),
	TRAIN("train"),
	TRAM("tram"),/* tram(电车) and light rail(铁路)*/
	RAIL("rail");
	
	private String value;
	
	public String getVal(){
		return value;
	}
	
	private TransitMode(String val){
		this.value = val;
	}
	
}
