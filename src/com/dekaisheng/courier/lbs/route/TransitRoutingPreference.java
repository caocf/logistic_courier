package com.dekaisheng.courier.lbs.route;

/**
 * 换乘选项，可以选择最少步行，或者最少换乘
 * @author Dorian
 *
 */
public enum TransitRoutingPreference {

	LESS_WALKING("less_walking"),
	FEWER_TRANSFERS("fewer_transfers");
	private String value;
	
	public String getVal(){
		return value;
	}
	
	private TransitRoutingPreference(String val){
		this.value = val;
	}
}
