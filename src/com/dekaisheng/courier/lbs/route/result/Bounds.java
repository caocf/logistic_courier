package com.dekaisheng.courier.lbs.route.result;

import java.io.Serializable;

/**
 * 晚点会被删除，LatLngBounds类代替
 * 一个矩形方框，由南西-北东两个坐标唯一确定
 * @author Dorian
 *
 */
public class Bounds implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GeoPoint southwest;
	public GeoPoint northeast;
}

