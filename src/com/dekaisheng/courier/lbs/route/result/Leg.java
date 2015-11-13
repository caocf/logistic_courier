package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;
import java.util.List;

/**
 * 一段路程，一条完整的Route分由多段组成，
 * @author Dorian
 *
 */
public class Leg implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1327972567889076781L;

	/**
	 * 距离，由一个文本和一个基本值组成，如13787(m)，13.8km
	 */
	public UnitAndValue distance;
	
	/**
	 * 所需时间，由一个值（单位秒）和一个描述文本组成，如2360，,3mins
	 */
	public UnitAndValue duration;
	
	/**
	 * 这段终点的地址信息
	 */
	public String end_address;
	
	/**
	 * 这段终点的坐标
	 */
	public GeoPoint end_location;
	
	/**
	 * 这段起点地址信息
	 */
	public String start_address;
	
	/**
	 * 这段起点坐标
	 */
	public GeoPoint start_location;
	
	/**
	 * 每一个Leg由多个Step组成
	 */
	public List<Step> steps;
	
	/**
	 * 经过的途经点？这个未测试过
	 */
	public List<Integer> via_waypoint;
	
	// 下面两个是换乘时候才有的
	
	/**
	 * 到达时间，注意value值表示的是基于1970年1月1日？开始计算的毫秒值
	 * text 会帮你换算成当天的什么时间
	 * 还有一个time_zone表示时区
	 */
	public UnitAndValue arrival_time;
	
	/**
	 * 出发时间，参考{@link arrival_time}
	 */
	public UnitAndValue departure_time;

}

