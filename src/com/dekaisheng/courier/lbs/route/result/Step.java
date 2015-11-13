package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;
import java.util.List;


/**
 * 一条Route由多个Leg组成，一个Leg由多个Step组成，一个Step也可能由多个Step组成
 * @author Dorian
 *
 */
public class Step implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 距离，由一个文本和一个基本值组成，如13787(m)，13.8km{@link UnitAndValue}
	 */
	public UnitAndValue distance;
	
	/**
	 * 所需时间，由一个值（单位秒）和一个描述文本组成，如2360，,3mins{@link UnitAndValue}
	 */
	public UnitAndValue duration;
	
	/**
	 * 这段终点的坐标
	 */
	public GeoPoint end_location;
	
	/**
	 * 这段起点坐标
	 */
	public GeoPoint start_location;
	
	/**
	 * 一段文本指令，如Walk to xxx
	 */
	public String html_instructions;
	
	/**
	 * 这一段路程的
	 */
	public PolyLine polyline;
	
	/**
	 * 方式：步行，驾车，骑车，换乘，详情请看{@link Mode}
	 */
	public String travel_mode;
	
	/**
	 * 换乘详情
	 */
	public TransitDetail transit_details;
	
	// 下面的steps和maneuver是在换乘路线时才有的
	
	/**
	 * ???
	 */
	public String maneuver;

	/**
	 * ???
	 */
	public List<Step> steps;
}








