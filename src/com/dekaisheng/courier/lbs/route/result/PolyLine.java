package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

/**
 * 任何一个Route、Leg、Step对象都有一个PolyLine，由一组有序的坐标点组成的线
 * @author Dorian
 *
 */
public class PolyLine implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 编码过的字符串，需要解码成List<LatLng>
	 */
	public String points;
	
	/**
	 * 添加的字段，方便直接取坐标点
	 */
	private List<LatLng> wayPoint;
	
	public List<LatLng> getWayPoint(){
		if(wayPoint == null){
			/**
			 * 调用的是google maps android api 的一个开源工具项目
			 */
			wayPoint = com.google.maps.android.PolyUtil.decode(points);
		}
		return wayPoint;
	}
}

