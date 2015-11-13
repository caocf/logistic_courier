package com.dekaisheng.courier.lbs.route;

/**
 * 路径规划的类型：驾车(2)，步行(3)，骑行(1)，换乘(4)
 * @author Dorian
 *
 */
public enum Mode {

	DRIVING("driving", 2), WALKING("walking", 3),
	BICYCLING("bicycling", 1),TRANSIT("transit", 4);
	
	private String value;
	
	private int mode;
	
	public String getVal(){
		return value;
	}
	
	public int getMode(){
		return mode;
	}
	private Mode(String val, int mode){
		this.value = val;
		this.mode = mode;
	}
}
