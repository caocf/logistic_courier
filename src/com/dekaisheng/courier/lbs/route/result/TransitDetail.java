package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;

/**
 * 换乘途路线某段的详情
 * @author Dorian
 *
 */
public class TransitDetail implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Station arrival_stop;
	public UnitAndValue arrival_time;
	public Station departure_stop;
	public UnitAndValue departure_time;
	public String headsign; // 方向，如地铁往哪个方向
	public int headway;  // ???什么意思
	public int num_stops; // 经过的站点数目
	public Line line;
}

