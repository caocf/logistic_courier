package com.dekaisheng.courier.lbs.route;

/**
 * 路径规划要避开的，roads/bridges，如高速路，渡轮、摆渡，indoor
 * @author Dorian
 *
 */
public enum Avoid {

	TOLLS("tolls"),
	HIGHWAYS("highways"),
	FERRIES("ferries"), 
	INDOOR("indoor");
	
	private String value;
	
	public String getVal(){
		return value;
	}
	private Avoid(String val){
		this.value = val;
	}
}
