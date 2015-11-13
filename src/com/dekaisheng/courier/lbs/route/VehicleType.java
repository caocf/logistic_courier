package com.dekaisheng.courier.lbs.route;

/**
 * 交通工具类型枚举
 * 1.RAIL	Rail.
 * 2.METRO_RAIL	Light rail transit.
 * 3.SUBWAY	Underground light rail.
 * 4.TRAM	Above ground light rail. 电车
 * 5.MONORAIL	Monorail.
 * 6.HEAVY_RAIL	Heavy rail.
 * 7.COMMUTER_TRAIN	Commuter rail.
 * 8.HIGH_SPEED_TRAIN	High speed train.
 * 9.BUS	Bus.
 * 10.INTERCITY_BUS	Intercity bus.
 * 11.TROLLEYBUS	Trolleybus.
 * 12.SHARE_TAXI	Share taxi is a kind of bus with the ability to drop off and pick up passengers anywhere on its route.
 * 13.FERRY	Ferry.
 * 14.CABLE_CAR	A vehicle that operates on a cable, usually on the ground. Aerial cable cars may be of the type GONDOLA_LIFT.
 * 15.GONDOLA_LIFT	An aerial cable car.
 * 16.FUNICULAR	A vehicle that is pulled up a steep incline by a cable. A Funicular typically consists of two cars, with each car acting as a counterweight for the other.
 * 17.OTHER	All other vehicles will return this type.
 * @author Dorian
 *
 */
public enum VehicleType {
	RAIL("Rail"),
	METRO_RAIL("Light rail transit"),
	SUBWAY("Subway"),
	TRAM("Tram"),
	MONORAIL("Monorail"),
	HEAVY_RAIL("Heavy rail"),
	COMMUTER_TRAIN("Commuter rail"),
	HIGH_SPEED_TRAIN("High speed train"),
	BUS("Bus"),
	INTERCITY_BUS("Intercity bus"),
	TROLLEYBUS("Trolleybus"),
	SHARE_TAXI("Share taxi"),
	FERRY("Ferry"),
	CABLE_CAR("Cable car"),
	GONDOLA_LIFT("Gondola lift"),
	FUNICULAR("Funicular"),
	OTHER("Other");
	private String value;
	
	public String getVal(){
		return value;
	}
	
	private VehicleType(String val){
		this.value = val;
	}
}
